package dev.frozenmilk.util.collections

class Q<T : Any> {
    var head: Cons<T>? = null
    var tail: Cons<T>? = null

    fun append(value: T) {
        val tail = this.tail
        val next = Cons.cons(value, null)
        if (head == null || tail == null) {
            head = next
            this.tail = head
        } else {
            tail.write(tail.car, next)
            this.tail = next
        }
    }

    fun append(values: Cons<T>?) {
        if (values == null) return
        val tail = this.tail
        if (head == null || tail == null) {
            head = values
            this.tail = Cons.last(values)
        } else {
            tail.write(tail.car, values)
            this.tail = Cons.last(values)
        }
    }

    fun append(other: Q<T>) {
        if (other.empty) return
        val tail = this.tail
        if (head == null || tail == null) {
            head = other.head
            this.tail = other.tail
        } else {
            tail.write(tail.car, other.head)
            this.tail = other.tail
        }
        other.head = null
        other.tail = null
    }

    fun prepend(value: T) {
        if (head == null) {
            head = Cons.cons(value, null)
            tail = head
        } else {
            head = Cons.cons(value, head)
        }
    }

    inline fun removeFirst(f: (T) -> Boolean): T? {
        var head = head
        var prev: Cons<T>? = null
        while (head !== null) {
            if (f(head.car)) {
                val result = head.car
                if (prev === null) this.head = head.cdr
                else prev.write(prev.car, head.cdr)
                if (head === tail) tail = prev
                Cons.drop(head)
                return result
            }
            prev = head
            head = head.cdr
        }
        return null
    }

    inline fun replaceFirst(f: (T) -> T?): T? {
        var head = head
        while (head !== null) {
            val with = f(head.car)
            if (with !== null) {
                head.write(with, head.cdr)
                return with
            }
            head = head.cdr
        }
        return null
    }

    inline fun replaceAll(f: (T) -> T?) {
        var head = head
        var prev: Cons<T>? = null
        while (head !== null) {
            val with = f(head.car)
            if (with !== null) {
                head.write(with, head.cdr)
            }
            else {
                if (prev === null) this.head = head.cdr
                else prev.write(prev.car, head.cdr)
                if (head === tail) tail = prev
                Cons.drop(head)
            }
            prev = head
            head = head.cdr
        }
    }

    @get:JvmName("empty")
    val empty
        get() = head == null

    /**
     * WARNING: will panic if [empty]
     */
    fun pop(): T = run {
        val head = checkNotNull(head) { "attempted to pop an empty queue" }
        val (car, cdr) = head
        if (tail == head) {
            Cons.drop(head)
            this.head = null
            this.tail = null
        }
        else {
            Cons.drop(head)
            this.head = cdr
        }
        car
    }

    fun clear() {
        Cons.dropAll(head)
        head = null
        tail = null
    }

    override fun toString() = head?.toString() ?: "()"
}
