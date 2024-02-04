import dev.frozenmilk.util.cell.RefCell
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CellTest {
	private var cell = RefCell("content")

	@Before
	fun setup() {
		cell.accept("content")
	}

	@Test
	fun get() {
		Assert.assertEquals("content", cell.get())
	}

	@Test
	fun set() {
		cell.accept("not")
		Assert.assertEquals("not", cell.get())
	}

	private var delegate by cell

	@Test
	fun delegate() {
		Assert.assertEquals(delegate, cell.get())
	}

	@Test
	fun delegateSet() {
		Assert.assertEquals(delegate, cell.get())
		delegate = "delegate"
		Assert.assertEquals("delegate", cell.get())
		Assert.assertEquals(delegate, cell.get())
	}
}