package dev.frozenmilk.util.test.cell

import dev.frozenmilk.util.cell.LateInitCell
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LateInitCellTest {
	private var cell = LateInitCell<String>()

	@Before
	fun setup() {
		cell.invalidate()
	}

	@Test
	fun safeInvoke() {
		cell.safeInvoke {
			Assert.fail()
		}
		cell.accept("content")
		cell.safeInvoke {
			Assert.assertEquals("content", cell.get())
		}
	}

	@Test
	fun string() {
		Assert.assertEquals("LateInitCell|null|", cell.toString())
		cell.accept("content")
		Assert.assertEquals("LateInitCell|content|", cell.toString())
	}

	@Test
	fun uninitialised() {
		Assert.assertEquals(null, cell.safeGet())
		Assert.assertEquals(false, cell.initialised())
		cell.accept("")
		Assert.assertEquals(true, cell.initialised())
		cell.invalidate()
		Assert.assertEquals(false, cell.initialised())
	}

	@Test
	fun safeGet() {
		Assert.assertEquals(null, cell.safeGet())
	}

	@Test(expected = IllegalStateException::class)
	fun unsafeGet() {
		Assert.assertEquals(null, cell.get())
	}

	@Test
	fun safeAfterInit() {
		cell.accept("content")
		Assert.assertEquals("content", cell.get())
	}

	@Test
	fun invalidation() {
		cell.accept("content")
		Assert.assertEquals("content", cell.get())
		cell.invalidate()
		Assert.assertEquals(null, cell.safeGet())
	}

	private var delegate by cell

	@Test(expected = IllegalStateException::class)
	fun delegationUnsafeGet() {
		Assert.assertEquals(null, delegate)
	}

	@Test
	fun delegationSetAndGet() {
		delegate = "content"
		Assert.assertEquals("content", delegate)
	}

	@Test
	fun delegateShared() {
		delegate = "content"
		Assert.assertEquals("content", cell.get())
		Assert.assertEquals(cell.get(), delegate)
	}
}