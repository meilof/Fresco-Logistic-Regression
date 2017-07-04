import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.fail

class MatrixSpec : Spek({
    val matrix = Matrix(arrayOf(
            arrayOf(1.0, 2.0),
            arrayOf(3.0, 4.0),
            arrayOf(5.0, 6.0)
    ))

    it("can recall its elements") {
        expect(matrix.element(row = 0, column = 0)).to.equal(1.0)
        expect(matrix.element(row = 2, column = 1)).to.equal(6.0)
    }

    it("does not tolerate differing row sizes") {
        try {
            Matrix(arrayOf(
                    arrayOf(1.0, 2.0),
                    arrayOf(3.0, 4.0, 5.0)
            ))
            fail()
        } catch (exception: IllegalArgumentException) {
            // success
        }
    }

    it("can be transposed") {
        val transposed = matrix.transpose()
        expect(transposed.element(0, 0)).to.equal(matrix.element(0, 0))
        expect(transposed.element(0, 1)).to.equal(matrix.element(1, 0))
        expect(transposed.element(1, 2)).to.equal(matrix.element(2, 1))
    }
})
