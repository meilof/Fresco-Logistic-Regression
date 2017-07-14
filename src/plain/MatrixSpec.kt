package plain

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.fail

class MatrixSpec : Spek({
    val matrix = Matrix(
            arrayOf(1.0, 2.0),
            arrayOf(3.0, 4.0),
            arrayOf(5.0, 6.0)
    )

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
            Matrix(
                    arrayOf(1.0, 2.0),
                    arrayOf(3.0, 4.0, 5.0)
            )
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

    it("can return a row") {
        val matrix = Matrix(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0)
        )
        val firstRow = matrix.row(0)
        val expected = Vector(1.0, 2.0)
        expect(firstRow).to.equal(expected)
    }

    it("can be multiplied with another matrix") {
        val otherMatrix = Matrix(
                arrayOf(10.0, 20.0, 30.0),
                arrayOf(40.0, 50.0, 60.0)
        )
        val multiplied = matrix * otherMatrix
        expect(multiplied[0, 0]).to.equal(90.0)
        expect(multiplied[0, 1]).to.equal(120.0)
        expect(multiplied[0, 2]).to.equal(150.0)
        expect(multiplied[1, 1]).to.equal(260.0)
        expect(multiplied[2, 2]).to.equal(510.0)
    }

    it("throws when attempting to multiply incompatible matrices") {
        val otherMatrix = Matrix(
                arrayOf(10.0, 20.0, 30.0),
                arrayOf(40.0, 50.0, 60.0),
                arrayOf(70.0, 80.0, 90.0)
        )
        try {
            matrix * otherMatrix
            fail()
        } catch(exception: IllegalArgumentException) {
            // success
        }
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
        val toBeSubtracted = Matrix(
                arrayOf(2.0, 4.0),
                arrayOf(6.0, 8.0),
                arrayOf(10.0, 12.0)
        )
        val expected = Matrix(
                arrayOf(-1.0, -2.0),
                arrayOf(-3.0, -4.0),
                arrayOf(-5.0, -6.0)
        )
        expect(matrix - toBeSubtracted).to.equal(expected)
    }

    it("throws when subtracting incompatible matrices") {
        val toBeSubtracted = Matrix(
                arrayOf(2.0, 4.0),
                arrayOf(6.0, 8.0)
        )
        try {
            matrix - toBeSubtracted
            fail()
        } catch (exception: IllegalArgumentException) {
            // success
        }
    }

    it("can add matrices") {
        val toBeAdded= Matrix(
                arrayOf(2.0, 4.0),
                arrayOf(6.0, 8.0),
                arrayOf(10.0, 12.0)
        )
        val expected = Matrix(
                arrayOf(3.0, 6.0),
                arrayOf(9.0, 12.0),
                arrayOf(15.0, 18.0)
        )
        expect(matrix + toBeAdded).to.equal(expected)
    }

    it("throws when adding incompatible matrices") {
        val toBeAdded= Matrix(
                arrayOf(2.0, 4.0),
                arrayOf(6.0, 8.0)
        )
        try {
            matrix + toBeAdded
            fail()
        } catch (exception: IllegalArgumentException) {
            // success
        }
    }

    context("when transposing a lower triangular matrix") {
        val transposed = LowerTriangularMatrix(Matrix(
                arrayOf(1.0, 0.0),
                arrayOf(2.0, 1.0)
        )).transpose()

        it("returns an upper triangular matrix") {
            expect(transposed as? UpperTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = UpperTriangularMatrix(Matrix(
                    arrayOf(1.0, 2.0),
                    arrayOf(0.0, 1.0)
            ))
            expect(transposed).to.equal(expected)
        }
    }

    context("when transposing an upper triangular matrix") {
        val transposed = UpperTriangularMatrix(Matrix(
                arrayOf(1.0, 2.0),
                arrayOf(0.0, 1.0)
        )).transpose()

        it("returns a lower triangular matrix") {
            expect(transposed as? LowerTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = LowerTriangularMatrix(Matrix(
                    arrayOf(1.0, 0.0),
                    arrayOf(2.0, 1.0)
            ))
            expect(transposed).to.equal(expected)
        }
    }

    it("can determine whether it is close to another matrix") {
        val closeMatrix = Matrix(
                arrayOf(1.25, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        )
        val notCloseMatrix = Matrix(
                arrayOf(1.3, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        )
        expect(matrix.isCloseTo(closeMatrix, 0.25)).to.be.`true`
        expect(matrix.isCloseTo(notCloseMatrix, 0.25)).to.be.`false`
    }

    it("throws when comparing matrices of different shapes") {
        val closeMatrix = Matrix(
                arrayOf(1.25, 2.0),
                arrayOf(3.0, 4.0)
        )
        try {
            matrix.isCloseTo(closeMatrix, 0.25)
            fail()
        } catch (exception: IllegalArgumentException) {
            // success
        }
    }

    it("can create a matrix from multiple vectors") {
        val v1 = Vector(1.0, 2.0, 3.0, 4.0)
        val v2 = Vector(11.0, 22.0, 33.0, 44.0)
        val m = matrixFromVectors(v1, v2)
        val expected = Matrix(
                arrayOf(1.0, 2.0, 3.0, 4.0),
                arrayOf(11.0, 22.0, 33.0, 44.0)
        )
        expect(m).to.equal(expected)
    }

    it("can create an identity matrix") {
        val expected = Matrix(
                arrayOf(1.0, 0.0, 0.0),
                arrayOf(0.0, 1.0, 0.0),
                arrayOf(0.0, 0.0, 1.0)
        )
        expect(IdentityMatrix(3) as MatrixType).to.equal(expected)
    }
})

