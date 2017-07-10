import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
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

    it("knows its number of rows and columns") {
        expect(matrix.numberOfRows).to.equal(3)
        expect(matrix.numberOfColumns).to.equal(2)
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

    it("can be multiplied by a scalar postfix") {
        val multiplied = matrix * 3.0
        expect(multiplied[0, 0]).to.equal(3.0)
        expect(multiplied[2, 1]).to.equal(18.0)
    }

    it("can be multiplied by a scalar prefix") {
        val multiplied = 3.0 * matrix
        expect(multiplied[0, 0]).to.equal(3.0)
        expect(multiplied[2, 1]).to.equal(18.0)
    }

    it("can subtract matrices") {
        val toBeSubtracted = Matrix(arrayOf(
                arrayOf(2.0, 4.0),
                arrayOf(6.0, 8.0),
                arrayOf(10.0, 12.0)
        ))
        val expected = Matrix(arrayOf(
                arrayOf(-1.0, -2.0),
                arrayOf(-3.0, -4.0),
                arrayOf(-5.0, -6.0)
        ))
        expect(matrix - toBeSubtracted).to.equal(expected)
    }

    context("when transposing a lower triangular matrix") {
        val transposed = LowerTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, 0.0),
                arrayOf(2.0, 1.0)
        ))).transpose()

        it("returns an upper triangular matrix") {
            expect(transposed as? UpperTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = UpperTriangularMatrix(Matrix(arrayOf(
                    arrayOf(1.0, 2.0),
                    arrayOf(0.0, 1.0)
            )))
            expect(transposed).to.equal(expected)
        }
    }

    context("when transposing an upper triangular matrix") {
        val transposed = UpperTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, 2.0),
                arrayOf(0.0, 1.0)
        ))).transpose()

        it("returns a lower triangular matrix") {
            expect(transposed as? LowerTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = LowerTriangularMatrix(Matrix(arrayOf(
                    arrayOf(1.0, 0.0),
                    arrayOf(2.0, 1.0)
            )))
            expect(transposed).to.equal(expected)
        }
    }

    it("computes log likelyhood") {
        val xi =
                Matrix(arrayOf(arrayOf(1.0, 2.0))).transpose()
        val beta =
                Matrix(arrayOf(arrayOf(0.1, 0.2))).transpose()
        val probability = logLikelyhood(xi, beta)
        expect(probability).to.be.closeTo(0.6224593, 0.01)
    }

    context("when log likelyhood gets invalid input") {
        it("throws on different vector lengths") {
            val xi =
                    Matrix(arrayOf(arrayOf(1.0, 2.0, 3.0)))
            val beta =
                    Matrix(arrayOf(arrayOf(0.1, 0.2))).transpose()
            try {
                logLikelyhood(xi, beta)
                fail()
            } catch (exception: IllegalArgumentException) {
                // success
            }
        }

        it("throws on matrices") {
            val xi =
                    Matrix(arrayOf(
                            arrayOf(1.0, 2.0),
                            arrayOf(1.0, 2.0)
                    ))
            val beta =
                    Matrix(arrayOf(arrayOf(0.1, 0.2))).transpose()
            try {
                logLikelyhood(xi, beta)
                fail()
            } catch (exception: IllegalArgumentException) {
                // success
            }
        }
    }
})
