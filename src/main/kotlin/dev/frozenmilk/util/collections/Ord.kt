package dev.frozenmilk.util.collections

interface Ord<T> {
    enum class Result {
        LT, GT, EQ
    }

    fun compare(l: T, r: T): Result

    object IdentityHashCode : Ord<Any?> {
        override fun compare(l: Any?, r: Any?) = if (l === r) Result.EQ
        else {
            val l = System.identityHashCode(l)
            val r = System.identityHashCode(r)
            if (l > r) Result.GT
            else Result.LT
        }
    }

    object HashCode : Ord<Any?> {
        override fun compare(l: Any?, r: Any?) = if (l == r) Result.EQ
        else {
            val l = l.hashCode()
            val r = r.hashCode()
            if (l > r) Result.GT
            else Result.LT
        }
    }

    class Comparable<T : kotlin.Comparable<T>> : Ord<T> {
        override fun compare(l: T, r: T) = run {
            if (l == r) Result.EQ
            else if (l > r) Result.GT
            else Result.LT
        }
    }
}