package dev.frozenmilk.util.collections

class LimitedQ<T>(val limit: Int) {
    private var head: Cons<T>? = null
    private var tail: Cons<T>? = null
    private var len = 0

    val full
        get() = len >= limit

    fun append(value: T) {
        check(!full)
        val tail = this.tail
        val next = Cons.cons(value, null)
        len++
        if (tail == null) {
            head = next
            this.tail = head
        } else {
            tail.write(tail.car, next)
            this.tail = next
        }
    }

    fun empty() = head == null

    /**
     * WARNING: will panic if [empty]
     */
    fun pop(): T = run {
        val head = head!!
        val (car, cdr) = head
        len--
        Cons.drop(head)
        this.head = cdr
        car
    }
}
