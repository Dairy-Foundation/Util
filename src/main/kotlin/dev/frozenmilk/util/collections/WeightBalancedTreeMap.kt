package dev.frozenmilk.util.collections

/**
 * weight balanced tree
 *
 * derived from:
 * https://www.cambridge.org/core/journals/journal-of-functional-programming/article/balancing-weightbalanced-trees/7281C4DE7E56B74F2D13F06E31DCBC5B,
 * https://www.cambridge.org/core/journals/journal-of-functional-programming/article/functional-pearls-efficient-setsa-balancing-act/0CAA1C189B4F7C15CE9B8C02D0D4B54E,
 * https://ia600204.us.archive.org/0/items/djoyner-papers/SHA256E-s234215--592b97774eca4193a05ed9472ab6e23788d3a0bea5d1b98cef301460ab4010ee.pdf
 */
class WeightBalancedTreeMap<K, V> private constructor(
    val key: K,
    val value: V,
    val size: Int,
    val l: WeightBalancedTreeMap<K, V>?,
    val r: WeightBalancedTreeMap<K, V>?,
) {
    operator fun component1() = k
    operator fun component2() = v
    operator fun component3() = l
    operator fun component4() = r

    val k: K
        get() = key

    val v: V
        get() = value

    companion object {
        private fun <K, V> cons(
            k: K,
            v: V,
        ) = WeightBalancedTreeMap(
            k,
            v,
            1,
            null,
            null,
        )

        private fun <K, V> cons(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>?,
            r: WeightBalancedTreeMap<K, V>?,
        ) = WeightBalancedTreeMap(
            k,
            v,
            size(l) + size(r) + 1,
            l,
            r,
        )

        @JvmStatic
        fun size(tree: WeightBalancedTreeMap<*, *>?) = tree?.size ?: 0

        private const val DELTA = 3
        private fun balanced(l: Int, r: Int): Boolean = DELTA * (l + 1) >= r + 1
        private fun balanced(
            l: WeightBalancedTreeMap<*, *>?,
            r: WeightBalancedTreeMap<*, *>?,
        ): Boolean = balanced(size(l), size(r))

        @Suppress("NON_TAIL_RECURSIVE_CALL")
        private tailrec fun balanced(tree: WeightBalancedTreeMap<*, *>?): Boolean =
            if (tree == null) true
            else balanced(tree.l, tree.r) && balanced(
                tree.r, tree.l
            ) && balanced(tree.l) && balanced(
                tree.r
            )

        private const val GAMMA = 2
        private fun single(
            l: WeightBalancedTreeMap<*, *>?,
            r: WeightBalancedTreeMap<*, *>?,
        ): Boolean = size(l) + 1 < GAMMA * (size(r) + 1)

        private fun <K, V> balance(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>?,
            r: WeightBalancedTreeMap<K, V>?,
        ) = run {
            val ln = size(l)
            val rn = size(r)
            if (ln + rn < 2) cons(k, v, l, r)
            else if (!balanced(ln, rn)) rotateL(k, v, l, r!!)
            else if (!balanced(rn, ln)) rotateR(k, v, l!!, r)
            else cons(k, v, l, r)
        }

        private fun <K, V> rotateL(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>?,
            r: WeightBalancedTreeMap<K, V>,
        ) = if (single(r.l, r.r)) singleL(k, v, l, r)
        else doubleL(k, v, l, r)

        private fun <K, V> singleL(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>?,
            r: WeightBalancedTreeMap<K, V>,
        ) = run {
            cons(
                r.k,
                r.v,
                cons(k, v, l, r.l),
                r.r,
            )
        }

        private fun <K, V> doubleL(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>?,
            r: WeightBalancedTreeMap<K, V>,
        ) = run {
            val rl = r.l!!
            cons(
                rl.k,
                rl.v,
                cons(
                    k,
                    v,
                    l,
                    rl.l,
                ),
                cons(
                    r.k,
                    r.v,
                    rl.r,
                    r.r,
                ),
            )
        }

        private fun <K, V> rotateR(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>,
            r: WeightBalancedTreeMap<K, V>?,
        ) = if (single(l.l, l.r)) singleR(k, v, l, r)
        else doubleR(k, v, l, r)

        private fun <K, V> singleR(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>,
            r: WeightBalancedTreeMap<K, V>?,
        ) = run {
            cons(
                l.k,
                l.v,
                l.l,
                cons(
                    k,
                    v,
                    l.r,
                    r,
                ),
            )
        }

        private fun <K, V> doubleR(
            k: K,
            v: V,
            l: WeightBalancedTreeMap<K, V>,
            r: WeightBalancedTreeMap<K, V>?,
        ) = run {
            val lr = l.r!!
            cons(
                lr.k,
                lr.v,
                cons(
                    l.k,
                    l.v,
                    l.l,
                    lr.l,
                ),
                cons(
                    k,
                    v,
                    lr.r,
                    r,
                ),
            )
        }

        @JvmStatic
        tailrec fun <K, V> min(tree: WeightBalancedTreeMap<K, V>): WeightBalancedTreeMap<K, V> =
            if (tree.l == null) tree
            else min(tree.l)

        @JvmStatic
        fun <K, V> get(
            ord: Ord<in K>,
            tree: WeightBalancedTreeMap<K, V>?,
            k: K,
        ): WeightBalancedTreeMap<K, V>? = if (tree == null) null
        else run {
            when (ord.compare(k, tree.k)) {
                Ord.Result.LT -> get(ord, tree.l, k)
                Ord.Result.GT -> get(ord, tree.r, k)
                Ord.Result.EQ -> tree
            }
        }

        @JvmStatic
        fun <K, V> add(
            ord: Ord<in K>,
            tree: WeightBalancedTreeMap<K, V>?,
            k: K,
            v: V,
        ): WeightBalancedTreeMap<K, V> = if (tree == null) cons(k, v)
        else run {
            val (tk, tv, tl, tr) = tree
            when (ord.compare(k, tk)) {
                Ord.Result.LT -> balance(tk, tv, add(ord, tl, k, v), tr)
                Ord.Result.GT -> balance(tk, tv, tl, add(ord, tr, k, v))
                // replace
                Ord.Result.EQ -> cons(k, v, tl, tr)
            }
        }

        @JvmStatic
        fun <K, V> delete(
            ord: Ord<in K>,
            tree: WeightBalancedTreeMap<K, V>?,
            k: K,
        ): WeightBalancedTreeMap<K, V>? = if (tree == null) null
        else run {
            val (tk, tv, tl, tr) = tree
            when (ord.compare(k, tk)) {
                Ord.Result.LT -> balance(tk, tv, delete(ord, tl, k), tr)
                Ord.Result.GT -> balance(tk, tv, tl, delete(ord, tr, k))
                // remove
                Ord.Result.EQ -> deleteJoin(tl, tr)
            }
        }

        private fun <K, V> deleteJoin(
            l: WeightBalancedTreeMap<K, V>?, r: WeightBalancedTreeMap<K, V>?
        ) = if (l == null) r
        else if (r == null) l
        else {
            val (mink, minv, _, _) = min(r)
            balance(mink, minv, l, deleteMin(r))
        }

        @JvmStatic
        fun <K, V> deleteMin(tree: WeightBalancedTreeMap<K, V>): WeightBalancedTreeMap<K, V>? =
            run {
                val (tk, tv, tl, tr) = tree
                if (tl == null) tr
                else balance(tk, tv, deleteMin(tl), tr)
            }

        fun interface TreeFold<K, V, R> {
            fun fold(acc: R, key: K, value: V): R
        }

        @JvmStatic
        fun <K, V, R> inorderFold(
            tree: WeightBalancedTreeMap<K, V>?,
            initial: R,
            f: TreeFold<K, V, R>,
        ): R = if (tree == null) initial
        else run {
            val (k, v, l, r) = tree
            inorderFold(
                l,
                f.fold(
                    inorderFold(
                        r,
                        initial,
                        f,
                    ),
                    k,
                    v,
                ),
                f,
            )
        }
    }
}
