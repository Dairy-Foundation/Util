package dev.frozenmilk.util.test.cell

import dev.frozenmilk.util.cell.LateInitCell
import dev.frozenmilk.util.cell.RefCell
import org.junit.Assert
import org.junit.Test

class Observation {
	@Test
	fun bind1() {
		val c1 = RefCell("start")
		val c2 = RefCell("")
		Assert.assertEquals("", c2.get())
		c1.bind(c2)
		Assert.assertEquals("start", c2.get())
		c1.accept("end")
		Assert.assertEquals("end", c2.get())
	}
	// tests a 2 way binding, very similar to bind1
	@Test
	fun bind2() {
		val c1 = RefCell("start")
		val c2 = RefCell("")
		Assert.assertEquals("", c2.get())
		c1.bindBoth(c2)
		Assert.assertEquals("start", c2.get())
		c2.accept("end")
		Assert.assertEquals("end", c1.get())
	}

	@Test
	fun bindLateInit() {
		val c1 = RefCell("start")
		val c2 = LateInitCell<String>()
		Assert.assertEquals(null, c2.safeGet())
		c1.bind(c2)
		// this bind won't run if the arg is null
		c2.bind { source, new -> c1.update(source, new ?: return@bind) }
		Assert.assertEquals("start", c2.get())
		c2.accept("end")
		Assert.assertEquals("end", c1.get())
		c2.invalidate()
		Assert.assertEquals(null, c2.safeGet())
		Assert.assertEquals("end", c1.get())
	}
}