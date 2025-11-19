package dev.frozenmilk.util.collections

class Q<T> {
    private var head: Cons<T>? = null
    private var tail: Cons<T>? = null

    fun append(value: T) {
        val tail = this.tail
        val next = Cons.cons(value, null)
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
        val head = checkNotNull(head) { "attempted to pop an empty queue" }
        val (car, cdr) = head
        Cons.drop(head)
        this.head = cdr
        car
    }
}
