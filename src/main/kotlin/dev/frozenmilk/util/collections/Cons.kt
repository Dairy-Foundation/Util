package dev.frozenmilk.util.collections

import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

/**
 * a cons cell singly linked list implementation
 *
 * typically a cons cell is a raw pair, but we need the inductive type
 * for the list, so this only supports cons cell lists
 *
 * WARNING: this class is not safe to share to user code
 * as many manipulating methods 'consume' the list
 * mutating it in place, or freeing the cells
 *
 * in exchange, by ensuring that there is only ever 1
 * reference to a cons cell and using the supplied methods,
 * cell allocations are cached and reused across the program lifetime,
 * and allocations will only be made when there are none available
 */
class Cons<T> private constructor(car: T, cdr: Cons<T>?) {
    /**
     * WARNING: this method is VERY unsafe, and should probably not be used by api consumers
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> write(car: T, cdr: Cons<T>?) = run {
        val res = this as Cons<T>
        res.car = car
        res.cdr = cdr
        res
    }

    operator fun component1() = car
    operator fun component2() = cdr

    var car = car
        private set

    var cdr = cdr
        private set

    override fun toString() = cdr.let { cdr ->
        if (cdr == null) "($car)"
        else "($car ${cdr.internalToString()}"
    }

    private fun internalToString(): String = cdr.let { cdr ->
        if (cdr == null) "$car)"
        else "$car ${cdr.internalToString()}"
    }


    private object FreeList {
        // TODO: cap len
        private class ThreadState(var head: Cons<Nothing?>? = null, var len: Int = 0)

        private val state = ThreadLocal.withInitial { ThreadState() }
        fun pop() = state.get().run {
            head?.also {
                head = it.cdr
                len--
            }
        }

        fun drop(cons: Cons<*>) = state.get().run {
            head = cons.write(null, head)
            len++
        }
    }

    companion object {
        // NOTE:
        // kotlin can't do both inline and tailrec,
        // so we need to duplicate the inline impls,
        // and hand unroll the loop
        // which is annoying

        /**
         * cons(truct) a new cons cell of value [car] to next node [cdr]
         * (https://en.wikipedia.org/wiki/CAR_and_CDR)
         *
         * this method will take a cons cell off the free list if there is one available,
         * otherwise it will allocate new one
         *
         * this means that this call may be very cheap if it is already allocated
         */
        @JvmStatic
        fun <T> cons(car: T, cdr: Cons<T>?) = FreeList.pop()?.write(car, cdr) ?: Cons(car, cdr)

        /**
         * convert list to cons
         */
        @JvmStatic
        fun <T> from(ls: List<T>) = ls.foldRight(null as Cons<T>?) { value, cons ->
            cons(value, cons)
        }

        /**
         * convert array to cons
         */
        @JvmStatic
        fun <T> from(arr: Array<T>) = arr.foldRight(null as Cons<T>?) { value, cons ->
            cons(value, cons)
        }

        /**
         * clone the list structure, not its contents
         *
         * the returned list's underlying cells are independent of the first's,
         * thus the list consuming methods can be applied to
         * one without affecting the structure of the other
         */
        @JvmStatic
        fun <T> clone(cons: Cons<T>?): Cons<T>? = if (cons == null) null
        else {
            val (car, cdr) = cons
            val head = cons(car, null)
            clone(cdr, head)
            head
        }

        /**
         * internal clone that is tailrec mod cons
         */
        private tailrec fun <T> clone(cons: Cons<T>?, tail: Cons<T>) {
            if (cons == null) return
            val next = cons(cons.car, null)
            tail.write(tail.car, next)
            clone(cons.cdr, next)
        }

        /**
         * WARNING: consumes [cons]
         */
        @JvmStatic
        tailrec fun dropAll(cons: Cons<*>?) {
            if (cons == null) return
            val cdr = cons.cdr
            FreeList.drop(cons)
            dropAll(cdr)
        }

        /**
         * WARNING: consumes the head of [cons]
         */
        @JvmStatic
        fun drop(cons: Cons<*>) = FreeList.drop(cons)

        /**
         * WARNING: consumes [cons]
         *
         * removes all values from [cons] that return false when [cond] is applied to them
         */
        @JvmStatic
        tailrec fun <T> filter(cons: Cons<T>?, cond: Predicate<T>): Cons<T>? =
            if (cons == null) null
            else {
                val (car, cdr) = cons
                if (cond.test(car)) {
                    val head = cons.write(car, null)
                    filter(cdr, cond, head)
                    head
                } else {
                    FreeList.drop(cons)
                    filter(cdr, cond)
                }
            }

        /**
         * internal filter that is tailrec mod cons
         */
        private tailrec fun <T> filter(cons: Cons<T>?, cond: Predicate<T>, tail: Cons<T>) {
            if (cons == null) return
            val (car, cdr) = cons
            if (cond.test(car)) {
                val next = cons.write(car, null)
                tail.write(tail.car, next)
                filter(cdr, cond, next)
            } else {
                FreeList.drop(cons)
                filter(cdr, cond, tail)
            }
        }

        /**
         * WARNING: consumes [cons]
         *
         * removes all values from [cons] that return false when [cond] is applied to them
         */
        @JvmStatic
        inline fun <T> filter(cons: Cons<T>?, cond: (T) -> Boolean): Cons<T>? =
            if (cons == null) null
            else {
                var fst: Cons<T>? = null
                var curr = cons
                while (curr != null && fst == null) {
                    val (car, cdr) = curr
                    if (cond(car)) {
                        fst = curr.write(car, null)
                        curr = cdr
                    } else {
                        drop(curr)
                        curr = cdr
                    }
                }
                if (fst == null) null
                else {
                    val head = fst
                    var tail: Cons<T> = fst
                    while (curr != null) {
                        val (car, cdr) = curr
                        if (cond(car)) {
                            curr.write(car, null)
                            tail.write(tail.car, curr)
                            tail = curr
                            curr = cdr
                        } else {
                            drop(curr)
                            curr = cdr
                        }
                    }
                    head
                }
            }

        /**
         * WARNING: consumes [cons]
         *
         * removes all values from [cons] that return null when [f] is applied to them
         */
        @JvmStatic
        tailrec fun <T, U : Any> filterMap(cons: Cons<T>?, f: Function<T, U?>): Cons<U>? =
            if (cons == null) null
            else {
                val (car, cdr) = cons
                val res = f.apply(car)
                if (res != null) {
                    val head = cons.write(res, null)
                    filterMap(cdr, f, head)
                    head
                } else {
                    FreeList.drop(cons)
                    filterMap(cdr, f)
                }
            }

        /**
         * internal filter that is tailrec mod cons
         */
        private tailrec fun <T, U : Any> filterMap(
            cons: Cons<T>?, f: Function<T, U?>, tail: Cons<U>
        ) {
            if (cons == null) return
            val (car, cdr) = cons
            val res = f.apply(car)
            if (res != null) {
                val next = cons.write(res, null)
                tail.write(tail.car, next)
                filterMap(cdr, f, next)
            } else {
                FreeList.drop(cons)
                filterMap(cdr, f, tail)
            }
        }

        /**
         * WARNING: consumes [cons]
         *
         * removes all values from [cons] that return null when [f] is applied to them
         */
        inline fun <T, U : Any> filterMap(cons: Cons<T>?, f: (T) -> U?): Cons<U>? =
            if (cons == null) null
            else {
                var curr = cons
                val head = run {
                    while (curr != null) {
                        val (car, cdr) = curr
                        val res = f(car)
                        if (res != null) {
                            val res = curr.write(res, null)
                            curr = cdr
                            return@run res
                        } else {
                            drop(curr)
                            curr = cdr
                        }
                    }
                    return null
                }
                var tail: Cons<U> = head
                while (curr != null) {
                    val (car, cdr) = curr
                    val res = f(car)
                    if (res != null) {
                        val next = curr.write(res, null)
                        tail.write(tail.car, next)
                        tail = next
                        curr = cdr
                    } else {
                        drop(curr)
                        curr = cdr
                    }
                }
                head
            }

        /**
         * WARNING: consumes [cons]
         */
        @JvmStatic
        fun <T> reverse(cons: Cons<T>?) = reverse(cons, null)
        private tailrec fun <T> reverse(cons: Cons<T>?, result: Cons<T>?): Cons<T>? {
            if (cons == null) return result
            val (car, cdr) = cons
            return reverse(cdr, cons.write(car, result))
        }

        /**
         * WARNING: consumes [cons]
         *
         * applies [f] to all values of [cons]
         */
        @JvmStatic
        fun <T, U> map(cons: Cons<T>?, f: Function<T, U>) = if (cons == null) null
        else {
            val (car, cdr) = cons
            val head = cons(f.apply(car), null)
            map(cdr, f, head)
            head
        }

        private tailrec fun <T, U> map(cons: Cons<T>?, f: Function<T, U>, tail: Cons<U>) {
            if (cons == null) return
            val (car, cdr) = cons
            val next = cons.write(f.apply(car), null)
            tail.write(tail.car, next)
            map(cdr, f, next)
        }

        /**
         * WARNING: consumes [cons]
         *
         * applies [f] to all values of [cons]
         */
        inline fun <T, U> map(cons: Cons<T>?, f: (T) -> U) = if (cons == null) null
        else {
            var curr = cons.cdr
            val head = cons.write(f(cons.car), null)
            var tail = head
            while (curr != null) {
                val (car, cdr) = curr
                val next = curr.write(f(car), null)
                tail.write(tail.car, next)
                tail = next
                curr = cdr
            }
            head
        }

        /**
         * Accumulates value starting with [initial] and applying [f] from left to right
         * to current accumulator value and each element.
         *
         * Returns the specified initial value if the collection is empty.
         */
        @JvmStatic
        tailrec fun <T, R> fold(cons: Cons<T>?, initial: R, f: BiFunction<R, T, R>): R {
            if (cons == null) return initial
            val (car, cdr) = cons
            return fold(cdr, f.apply(initial, car), f)
        }

        /**
         * Accumulates value starting with [initial] and applying [f] from left to right
         * to current accumulator value and each element.
         *
         * Returns the specified initial value if the collection is empty.
         */
        inline fun <T, R> fold(cons: Cons<T>?, initial: R, f: (acc: R, value: T) -> R): R {
            var curr = cons
            var res = initial
            while (curr != null) {
                val (car, cdr) = curr
                res = f(res, car)
                curr = cdr
            }
            return res
        }

        /**
         * WARNING: consumes [cons]
         *
         * Accumulates value starting with [initial] and applying [f] from left to right
         * to current accumulator value and each element.
         *
         * Returns the specified initial value if the collection is empty.
         */
        @JvmStatic
        tailrec fun <T, R> drainFold(cons: Cons<T>?, initial: R, f: BiFunction<R, T, R>): R {
            if (cons == null) return initial
            val (car, cdr) = cons
            FreeList.drop(cons)
            return drainFold(cdr, f.apply(initial, car), f)
        }

        /**
         * WARNING: consumes [cons]
         *
         * Accumulates value starting with [initial] and applying [f] from left to right
         * to current accumulator value and each element.
         *
         * Returns the specified initial value if the collection is empty.
         */
        inline fun <T, R> drainFold(cons: Cons<T>?, initial: R, f: (acc: R, value: T) -> R): R {
            var curr = cons
            var res = initial
            while (curr != null) {
                val (car, cdr) = curr
                drop(curr)
                res = f(res, car)
                curr = cdr
            }
            return res
        }

        /**
         * Accumulates value starting with the first element and applying [f] from left to
         * right to current accumulator value and each element.
         */
        @JvmStatic
        fun <T> reduce(cons: Cons<T>, f: BiFunction<T, T, T>) = reduce(cons.cdr, cons.car, f)
        private tailrec fun <T> reduce(cons: Cons<T>?, result: T, f: BiFunction<T, T, T>): T {
            if (cons == null) return result
            val (car, cdr) = cons
            return reduce(cdr, f.apply(car, result), f)
        }

        /**
         * Accumulates value starting with the first element and applying [f] from left to
         * right to current accumulator value and each element.
         */
        inline fun <T> reduce(cons: Cons<T>, f: (acc: T, value: T) -> T): T {
            var res = cons.car
            var curr = cons.cdr
            while (curr != null) {
                val (car, cdr) = curr
                res = f(res, car)
                curr = cdr
            }
            return res
        }

        /**
         * WARNING: consumes [cons]
         *
         * Accumulates value starting with the first element and applying [f] from left to
         * right to current accumulator value and each element.
         */
        @JvmStatic
        fun <T> drainReduce(cons: Cons<T>, f: BiFunction<T, T, T>) =
            drainReduce(cons.cdr, cons.car, f)

        private tailrec fun <T> drainReduce(cons: Cons<T>?, result: T, f: BiFunction<T, T, T>): T {
            if (cons == null) return result
            val (car, cdr) = cons
            FreeList.drop(cons)
            return drainReduce(cdr, f.apply(car, result), f)
        }

        /**
         * WARNING: consumes [cons]
         *
         * Accumulates value starting with the first element and applying [f] from left to
         * right to current accumulator value and each element.
         */
        inline fun <T> drainReduce(cons: Cons<T>, f: (acc: T, value: T) -> T): T {
            var res = cons.car
            var curr = cons.cdr
            while (curr != null) {
                val (car, cdr) = curr
                drop(cons)
                res = f(res, car)
                curr = cdr
            }
            return res
        }

        /**
         * applies [f] to each value of [cons]
         */
        @JvmStatic
        tailrec fun <T> forEach(cons: Cons<T>?, f: Consumer<T>) {
            if (cons != null) {
                f.accept(cons.car)
                forEach(cons.cdr, f)
            }
        }

        /**
         * applies [f] to each value of [cons]
         */
        inline fun <T> forEach(cons: Cons<T>?, f: (T) -> Unit) {
            var curr = cons
            while (curr != null) {
                val (car, cdr) = curr
                f(car)
                curr = cdr
            }
        }

        /**
         * WARNING: consumes [cons]
         *
         * applies [f] to each value of [cons]
         */
        @JvmStatic
        tailrec fun <T> drainForEach(cons: Cons<T>?, f: Consumer<T>) {
            if (cons != null) {
                val (car, cdr) = cons
                FreeList.drop(cons)
                f.accept(car)
                drainForEach(cdr, f)
            }
        }

        /**
         * WARNING: consumes [cons]
         *
         * applies [f] to each value of [cons]
         */
        inline fun <T> drainForEach(cons: Cons<T>?, f: (T) -> Unit) {
            var curr = cons
            while (curr != null) {
                val (car, cdr) = curr
                drop(curr)
                f(car)
                curr = cdr
            }
        }

        fun interface IndexedConsumer<T> {
            fun accept(index: Int, t: T)
        }

        /**
         * applies [f] to each value of [cons]
         */
        @JvmStatic
        fun <T> forEachIndexed(cons: Cons<T>?, f: IndexedConsumer<T>) = forEachIndexed(cons, 0, f)
        private tailrec fun <T> forEachIndexed(cons: Cons<T>?, i: Int, f: IndexedConsumer<T>) {
            if (cons != null) {
                f.accept(i, cons.car)
                forEachIndexed(cons.cdr, i + 1, f)
            }
        }

        /**
         * applies [f] to each value of [cons]
         */
        inline fun <T> forEachIndexed(cons: Cons<T>?, f: (index: Int, value: T) -> Unit) {
            var curr = cons
            var i = 0
            while (curr != null) {
                val (car, cdr) = curr
                f(i, car)
                i++
                curr = cdr
            }
        }

        /**
         * like [forEach], but consumes [cons] as it runs
         *
         * @see forEach
         */
        @JvmStatic
        fun <T> drainForEachIndexed(cons: Cons<T>?, f: IndexedConsumer<T>) =
            drainForEachIndexed(cons, 0, f)

        private tailrec fun <T> drainForEachIndexed(cons: Cons<T>?, i: Int, f: IndexedConsumer<T>) {
            if (cons != null) {
                val car = cons.car
                val cdr = cons.cdr
                FreeList.drop(cons)
                f.accept(i, car)
                drainForEachIndexed(cdr, i + 1, f)
            }
        }

        /**
         * applies [f] to each value of [cons]
         */
        inline fun <T> drainForEachIndexed(cons: Cons<T>?, f: (index: Int, value: T) -> Unit) {
            var curr = cons
            var i = 0
            while (curr != null) {
                val (car, cdr) = curr
                drop(curr)
                f(i, car)
                i++
                curr = cdr
            }
        }
    }
}