package dev.frozenmilk.util.collections

import org.jetbrains.annotations.Contract
import java.util.function.Predicate

/**
 * weight balanced tree
 *
 * derived from:
 * https://www.cambridge.org/core/journals/journal-of-functional-programming/article/balancing-weightbalanced-trees/7281C4DE7E56B74F2D13F06E31DCBC5B,
 * https://www.cambridge.org/core/journals/journal-of-functional-programming/article/functional-pearls-efficient-setsa-balancing-act/0CAA1C189B4F7C15CE9B8C02D0D4B54E,
 * https://ia800908.us.archive.org/13/items/djoyner-papers/SHA256E-s234215--592b97774eca4193a05ed9472ab6e23788d3a0bea5d1b98cef301460ab4010ee.pdf
 */
open class WBT<K, E : Any>(val keySelector: (E) -> K, val ord: Ord<in K>) {
    companion object {
        @Suppress("FunctionName")
        @JvmStatic
        @Contract(pure = true)
        fun <K, E : Any> Make(keySelector: (E) -> K, ord: Ord<in K>) = WBT(keySelector, ord)

        private val keySelectorSet = { x: Any -> x }
        private val identitySet = WBT(keySelectorSet, Ord.IdentityHashCode)
        private val hashSet = WBT(keySelectorSet, Ord.HashCode)

        @Suppress("FunctionName", "UNCHECKED_CAST")
        @JvmStatic
        @Contract(pure = true)
        fun <K : Any> MakeSet(ord: Ord<in K>) = when (ord) {
            Ord.IdentityHashCode -> identitySet
            Ord.HashCode -> hashSet
            else -> WBT(keySelectorSet as (K) -> K, ord)
        } as WBT<K, K>

        private val identityValueMap = WBT(ValueEntry.keySelector, Ord.IdentityHashCode)
        private val hashValueMap = WBT(ValueEntry.keySelector, Ord.HashCode)

        @Suppress("FunctionName", "UNCHECKED_CAST")
        @JvmStatic
        @Contract(pure = true)
        fun <K> MakeValueMap(ord: Ord<in K>) = when (ord) {
            Ord.IdentityHashCode -> identityValueMap
            Ord.HashCode -> hashValueMap
            else -> WBT(ValueEntry.keySelector as (ValueEntry<K, *>) -> K, ord)
        } as WBT<K, ValueEntry<K, *>>

        private val identityVariableMap = WBT(VariableEntry.keySelector, Ord.IdentityHashCode)
        private val hashVariableMap = WBT(VariableEntry.keySelector, Ord.HashCode)

        @Suppress("FunctionName", "UNCHECKED_CAST")
        @JvmStatic
        @Contract(pure = true)
        fun <K> MakeVariableMap(ord: Ord<in K>) = when (ord) {
            Ord.IdentityHashCode -> identityVariableMap
            Ord.HashCode -> hashVariableMap
            else -> WBT(VariableEntry.keySelector as (VariableEntry<K, *>) -> K, ord)
        } as WBT<K, VariableEntry<K, *>>

        private const val DELTA = 3
        private const val GAMMA = 2

        @JvmStatic
        @Contract(pure = true)
        fun <K, V> WBT<K, VariableEntry<K, *>>.upsert(
            tree: Tree<VariableEntry<K, V>>?,
            k: K,
            v: V,
        ): Tree<VariableEntry<K, V>> = if (tree == null) cons(VariableEntry(k, v))
        else when (compare(k, tree.v.k)) {
            Ord.Result.LT -> balance(tree.v, upsert(tree.l, k, v), tree.r)
            Ord.Result.GT -> balance(tree.v, tree.l, upsert(tree.r, k, v))
            Ord.Result.EQ -> {
                tree.v.v = v
                tree
            }
        }
    }

    data class ValueEntry<K, out V>(val k: K, val v: V) {
        companion object {
            val keySelector = { x: ValueEntry<Any?, Any?> -> x.k }
        }
    }

    data class VariableEntry<K, V>(val k: K, var v: V) {
        companion object {
            val keySelector = { x: VariableEntry<Any?, Any?> -> x.k }
        }
    }

    @ExposedCopyVisibility
    data class Tree<V> internal constructor(
        val v: V,
        val size: Int,
        val l: Tree<V>?,
        val r: Tree<V>?,
    )

    //
    // internals
    //

    private fun size(tree: Tree<*>?) = tree?.size ?: 0

    @Contract(pure = true)
    fun <V : E> cons(v: V) = Tree(v, 1, null, null)
    private fun <V> cons(
        v: V,
        l: Tree<V>?,
        r: Tree<V>?,
    ) = Tree(
        v,
        size(l) + size(r) + 1,
        l,
        r,
    )

    private fun balanced(l: Int, r: Int): Boolean = DELTA * (l + 1) >= r + 1
    private fun balanced(l: Tree<*>?, r: Tree<*>?) = balanced(size(l), size(r))

    private fun single(l: Tree<*>?, r: Tree<*>?) = size(l) + 1 < GAMMA * (size(r) + 1)

    private fun <V : E> balance(
        v: V,
        l: Tree<V>?,
        r: Tree<V>?,
    ) = run {
        val ln = size(l)
        val rn = size(r)
        if (ln + rn < 2) cons(v, l, r)
        else if (!balanced(ln, rn)) rotateL(v, l, r!!)
        else if (!balanced(rn, ln)) rotateR(v, l!!, r)
        else cons(v, l, r)
    }

    private fun <V : E> rotateL(
        v: V,
        l: Tree<V>?,
        r: Tree<V>,
    ) = if (single(r.l, r.r)) singleL(v, l, r)
    else doubleL(v, l, r)

    private fun <V : E> singleL(
        v: V,
        l: Tree<V>?,
        r: Tree<V>,
    ) = cons(
        r.v,
        cons(v, l, r.l),
        r.r,
    )

    private fun <V : E> doubleL(
        v: V,
        l: Tree<V>?,
        r: Tree<V>,
    ) = run {
        val rl = r.l!!
        cons(
            rl.v,
            cons(
                v,
                l,
                rl.l,
            ),
            cons(
                r.v,
                rl.r,
                r.r,
            ),
        )
    }

    private fun <V : E> rotateR(
        v: V,
        l: Tree<V>,
        r: Tree<V>?,
    ) = if (single(l.r, l.l)) singleR(v, l, r)
    else doubleR(v, l, r)

    private fun <V : E> singleR(
        v: V,
        l: Tree<V>,
        r: Tree<V>?,
    ) = run {
        cons(
            l.v,
            l.l,
            cons(
                v,
                l.r,
                r,
            ),
        )
    }

    private fun <V : E> doubleR(
        v: V,
        l: Tree<V>,
        r: Tree<V>?,
    ) = run {
        val lr = l.r!!
        cons(
            lr.v,
            cons(
                l.v,
                l.l,
                lr.l,
            ),
            cons(
                v,
                lr.r,
                r,
            ),
        )
    }

    private fun compare(l: K, r: K) = ord.compare(l, r)

    //
    // externals
    //

    @Contract(pure = true)
    fun <V : E> empty(): Tree<V>? = null

    @Contract(pure = true)
    fun <V : E> of(vararg vs: V) = vs.fold(empty(), ::add)

    @Contract(pure = true)
    fun <V : E> ofs(v: V, vararg vs: V) = vs.fold(cons(v), ::add)

    fun <V : E> of(es: Iterable<V>) = es.fold(empty(), ::add)

    fun <V : E> min(tree: Tree<V>?) = tree?.let(::internalMin)

    private tailrec fun <V : E> internalMin(tree: Tree<V>): V = if (tree.l == null) tree.v
    else internalMin(tree.l)

    @Contract(pure = true)
    fun <V : E> getTree(
        tree: Tree<V>?,
        k: K,
    ): Tree<V>? = if (tree == null) null
    else when (compare(k, keySelector(tree.v))) {
        Ord.Result.LT -> getTree(tree.l, k)
        Ord.Result.GT -> getTree(tree.r, k)
        Ord.Result.EQ -> tree
    }

    @Contract(pure = true)
    fun <V : E> get(
        tree: Tree<V>?,
        k: K,
    ) = getTree(tree, k)?.v

    @Contract(pure = true)
    fun <V : E> add(
        tree: Tree<V>?,
        v: V,
    ): Tree<V> = if (tree == null) cons(v)
    else when (compare(keySelector(v), keySelector(tree.v))) {
        Ord.Result.LT -> balance(tree.v, add(tree.l, v), tree.r)
        Ord.Result.GT -> balance(tree.v, tree.l, add(tree.r, v))
        // replaces the previous entry
        Ord.Result.EQ -> cons(v, tree.l, tree.r)
    }

    @Contract(pure = true)
    fun <V : E> union(
        l: Tree<V>?,
        r: Tree<V>?,
    ): Tree<V>? = if (l == null) r
    else if (r == null) l
    else {
        val k = keySelector(r.v)
        val lSplit = splitLT(l, k)
        val rSplit = splitGT(l, k)
        concat3(r.v, union(lSplit, r.l), union(rSplit, r.r))
    }

    @Contract(pure = true)
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E> splitLT(
        tree: Tree<V>?,
        k: K,
    ): Tree<V>? = if (tree == null) null
    else when (compare(keySelector(tree.v), k)) {
        Ord.Result.LT -> concat3(tree.v, tree.l, splitLT(tree.r, k))
        Ord.Result.GT -> splitLT(tree.l, k)
        Ord.Result.EQ -> tree.l
    }

    @Contract(pure = true)
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E> splitGT(
        tree: Tree<V>?,
        k: K,
    ): Tree<V>? = if (tree == null) null
    else when (compare(keySelector(tree.v), k)) {
        Ord.Result.LT -> splitGT(tree.r, k)
        Ord.Result.GT -> concat3(tree.v, splitGT(tree.l, k), tree.r)
        Ord.Result.EQ -> tree.r
    }

    @Contract(pure = true)
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E> splitGTE(
        tree: Tree<V>?,
        k: K,
    ): Tree<V>? = if (tree == null) null
    else when (compare(keySelector(tree.v), k)) {
        Ord.Result.LT -> splitGTE(tree.r, k)
        Ord.Result.GT -> concat3(tree.v, splitGTE(tree.l, k), tree.r)
        Ord.Result.EQ -> tree
    }

    @Contract(pure = true)
    private fun <V : E> concat3(
        v: V,
        l: Tree<V>?,
        r: Tree<V>?,
    ): Tree<V> = if (l == null) add(r, v)
    else if (r == null) add(l, v)
    else if (!balanced(l, r)) balance(r.v, concat3(v, l, r.l), r.r)
    else if (!balanced(r, l)) balance(l.v, l.l, concat3(v, l.r, r))
    else cons(v, l, r)

    @Contract(pure = true)
    private fun <V : E> concat(
        l: Tree<V>?,
        r: Tree<V>?,
    ): Tree<V>? = if (r == null) l
    else if (l == null) r
    else if (!balanced(l, r)) balance(r.v, concat(l, r.l), r.r)
    else if (!balanced(r, l)) balance(l.v, l.l, concat(l.r, r))
    else deleteJoin(l, r)

    @Contract(pure = true)
    fun <V : E> delete(
        tree: Tree<V>?,
        k: K,
    ): Tree<V>? = if (tree == null) null
    else when (compare(k, keySelector(tree.v))) {
        Ord.Result.LT -> balance(tree.v, delete(tree.l, k), tree.r)
        Ord.Result.GT -> balance(tree.v, tree.l, delete(tree.r, k))
        // remove
        Ord.Result.EQ -> deleteJoin(tree.l, tree.r)
    }

    private fun <V : E> deleteJoin(
        l: Tree<V>?,
        r: Tree<V>?,
    ) = if (l == null) r
    else if (r == null) l
    else balance(internalMin(r), l, deleteMin(r))

    private fun <V : E> deleteMin(tree: Tree<V>): Tree<V>? = if (tree.l == null) tree.r
    else balance(tree.v, deleteMin(tree.l), tree.r)

    @Contract(pure = true)
    fun <V : E, R> inorderFold(
        tree: Tree<V>?,
        initial: R,
        f: (acc: R, value: V) -> R,
    ): R = if (tree == null) initial
    else inorderFold(
        tree.r,
        f(
            inorderFold(
                tree.l,
                initial,
                f,
            ),
            tree.v,
        ),
        f,
    )

    @Contract(pure = true)
    fun <V : E> any(
        tree: Tree<V>?,
        cond: (V) -> Boolean,
    ): Boolean = if (tree == null) false
    else cond(tree.v) || any(tree.l, cond) || any(tree.r, cond)

    @Contract(pure = true)
    fun <V : E> all(
        tree: Tree<V>?,
        cond: (V) -> Boolean,
    ): Boolean = if (tree == null) true
    else cond(tree.v) && any(tree.l, cond) && any(tree.r, cond)

    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E> foreach(
        tree: Tree<V>?,
        f: (V) -> Unit,
    ) {
        if (tree == null) return
        foreach(tree.l, f)
        f(tree.v)
        foreach(tree.r, f)
    }

    @Contract(pure = true)
    fun <V : E, U> map(
        tree: Tree<V>?,
        f: (V) -> U,
    ): Tree<U>? = if (tree == null) null
    else cons(
        f(tree.v),
        map(tree.l, f),
        map(tree.r, f),
    )

    @Contract(pure = true)
    fun <V : E, U : E, W : E> zipFilterMap(
        l: Tree<V>?,
        r: Tree<U>?,
        f: (V?, U?) -> W?,
    ): Tree<W>? =
        if (l === null)
            if (r === null) null
            else {
                val w = f(null, r.v)
                if (w === null) deleteJoin(
                    zipFilterMap(null, r.l, f),
                    zipFilterMap(null, r.r, f),
                )
                else balance(
                    w,
                    zipFilterMap(null, r.l, f),
                    zipFilterMap(null, r.r, f),
                )
            }
        else if (r === null) {
            val w = f(l.v, null)
            if (w === null) deleteJoin(
                zipFilterMap(l.l, null, f),
                zipFilterMap(l.r, null, f),
            )
            else balance(
                w,
                zipFilterMap(l.l, null, f),
                zipFilterMap(l.r, null, f),
            )
        } else when (compare(keySelector(l.v), keySelector(r.v))) {
            Ord.Result.EQ -> {
                val w = f(l.v, r.v)
                if (w === null) deleteJoin(
                    zipFilterMap(l.l, r.l, f),
                    zipFilterMap(l.r, r.r, f),
                )
                else balance(
                    w,
                    zipFilterMap(l.l, r.l, f),
                    zipFilterMap(l.r, r.r, f),
                )
            }

            else -> {
                val k = keySelector(l.v)

                val lSplit = splitLT(r, k)
                val rSplit = splitGT(r, k)

                val w = f(l.v, get(r, k))
                if (w === null) concat(
                    zipFilterMap(l.l, lSplit, f),
                    zipFilterMap(l.r, rSplit, f),
                )
                else concat3(
                    w,
                    zipFilterMap(l.l, lSplit, f),
                    zipFilterMap(l.r, rSplit, f),
                )
            }
        }

    @Contract(pure = true)
    fun <V : E, U : E> filterMap(
        tree: Tree<V>?,
        f: (V) -> U?,
    ): Tree<U>? = if (tree == null) null
    else {
        val u = f(tree.v)
        if (u === null) deleteJoin(filterMap(tree.l, f), filterMap(tree.r, f))
        else balance(u, filterMap(tree.l, f), filterMap(tree.r, f))
    }

    @Contract(pure = true)
    fun <V : E, U : Any> filterMap(
        tree: Tree<V>?,
        module: WBT<*, U>,
        f: (V) -> U?,
    ): Tree<U>? = if (tree == null) null
    else {
        val u = f(tree.v)
        if (u === null) module.deleteJoin(
            filterMap(tree.l, module, f),
            filterMap(tree.r, module, f),
        )
        else module.balance(
            u,
            filterMap(tree.l, module, f),
            filterMap(tree.r, module, f),
        )
    }

    @Contract(pure = true)
    fun <V : E, U : V> mapEntry(
        tree: Tree<V>?,
        k: K,
        f: (V?) -> U?,
    ): Tree<V>? = run {
        val current = getTree(tree, k)?.v
        if (current === null) {
            val next = f(null)
            if (next === null) tree
            else add(tree, next)
        }
        else {
            val next = f(current)
            if (next === null) delete(tree, k)
            else when (compare(keySelector(current), keySelector(next))) {
                // same key, so this will replace the current entry
                Ord.Result.EQ -> add(tree, next)
                // remove and add
                else -> add(delete(tree, k), next)
            }
        }
    }

    @Contract(pure = true)
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E> findTree(tree: Tree<V>?, f: Predicate<V>): Tree<V>? = if (tree == null) null
    else if (f.test(tree.v)) tree
    else findTree(tree.l, f) ?: findTree(tree.r, f)

    @Contract(pure = true)
    fun <V : E> find(tree: Tree<V>?, f: Predicate<V>): V? = findTree(tree, f)?.v

    @Contract(pure = true)
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun <V : E, U : Any> findMap(tree: Tree<V>?, f: (V) -> U?): U? = if (tree == null) null
    else f(tree.v) ?: (findMap(tree.l, f) ?: findMap(tree.r, f))
}