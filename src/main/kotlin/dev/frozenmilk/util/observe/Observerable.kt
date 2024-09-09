package dev.frozenmilk.util.observe

/**
 * combines [Observer] and [Observable]
 */
interface Observerable<T> : Observable<T>, Observer<T> {
	/**
	 * [bind]s [observerable] to listen to this
	 *
	 * then [bind]s this to listen to [observerable]
	 *
	 * if [bind] is set up correctly, then [observerable] will gain this observable value
	 */
	fun bindBoth(observerable: Observerable<T>) {
		bind(observerable)
		observerable.bind(this)
	}
}