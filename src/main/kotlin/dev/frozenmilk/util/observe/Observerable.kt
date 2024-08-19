package dev.frozenmilk.util.observe

/**
 * combines [Observer] and [Observable]
 */
interface Observerable<T> : Observable<T>, Observer<T>