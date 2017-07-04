import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.*

class MatrixSpec : Spek({
    val matrix: Matrix<Double> = Matrix(arrayOf(
            arrayOf(1.0, 2.0),
            arrayOf(3.0, 4.0),
            arrayOf(5.0, 6.0)
    ))

    it("can recall its elements") {
        assertEquals(1.0, matrix.element(row = 0, column = 0))
        assertEquals(6.0, matrix.element(row = 2, column = 1))
    }
})
