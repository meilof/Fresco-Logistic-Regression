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
        expect(matrix[0, 0]).to.equal(1.0)
        expect(matrix[2, 1]).to.equal(6.0)
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
        expect(transposed[0, 0]).to.equal(matrix[0, 0])
        expect(transposed[0, 1]).to.equal(matrix[1, 0])
        expect(transposed[1, 2]).to.equal(matrix[2, 1])
    }

    it("can be multiplied with another matrix") {
        val otherMatrix = Matrix(arrayOf(
                arrayOf(10.0, 20.0, 30.0),
                arrayOf(40.0, 50.0, 60.0)
        ))
        val multiplied = matrix * otherMatrix
        expect(multiplied[0, 0]).to.equal(90.0)
        expect(multiplied[0, 1]).to.equal(120.0)
        expect(multiplied[0, 2]).to.equal(150.0)
        expect(multiplied[1, 1]).to.equal(260.0)
        expect(multiplied[2, 2]).to.equal(510.0)

    }
})
