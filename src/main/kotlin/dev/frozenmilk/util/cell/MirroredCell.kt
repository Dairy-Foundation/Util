package dev.frozenmilk.util.cell

import java.lang.reflect.Field
import java.util.function.Supplier

/**
 * a cell that manages a reference through reflection
 */
@Suppress("UNCHECKED_CAST")
open class MirroredCell<T>(
        private val parent: Any,
        private val field: Field,
) : LateInitCell<T>(null, "Attempted to obtain a null value from a mirrored cell") // we actually don't care about the ref in this circumstance
{
    constructor(parent: Any, field: String) : this(parent, recurseFindField(parent.javaClass, parent.javaClass, field))
    init {
        field.isAccessible = true
    }

    override var ref: T?
        get() = this.field.get(parent) as? T?
        set(value) { this.field.set(parent, value) }
}

@Suppress("UNCHECKED_CAST")
open class SupplierMirroredCell<T>(
        private val parentSupplier: Supplier<Any>,
        private val field: Field,
) : LateInitCell<T>(null, "Attempted to obtain a null value from a mirrored cell") // we actually don't care about the ref in this circumstance
{
    constructor(parentSupplier: Supplier<Any>, field: String) : this(parentSupplier, recurseFindField(parentSupplier.get().javaClass, parentSupplier.get().javaClass, field))
    init {
        field.isAccessible = true
    }

    override var ref: T?
        get() = this.field.get(parentSupplier.get()) as? T?
        set(value) { this.field.set(parentSupplier.get(), value) }
}

private fun recurseFindField(base: Class<*>, cls: Class<*>?, field: String): Field {
    if (cls == null) throw IllegalArgumentException("unable to find field \"$field\" after searching all classes and super classes for ${base.name}")
    return try {
        cls.getDeclaredField(field)
    } catch (_: Exception) {
        recurseFindField(base, cls.superclass, field)
    }
}
