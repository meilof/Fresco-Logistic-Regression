package fresco.dsl.matrices

import com.winterbe.expekt.expect
import fresco.dsl.KnownFixedPoint
import fresco.dsl.evaluate
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.fail

class MatrixSpec : Spek({
    val matrix = Matrix(
            arrayOf(KnownFixedPoint(1.0), KnownFixedPoint(2.0)),
            arrayOf(KnownFixedPoint(3.0), KnownFixedPoint(4.0)),
            arrayOf(KnownFixedPoint(5.0), KnownFixedPoint(6.0))
    )

    it("can recall its elements") {
        expect(evaluate(matrix[0, 0])).to.equal(1.0)
        expect(evaluate(matrix[2, 1])).to.equal(6.0)
    }

    it("knows its number of rows and columns") {
        expect(matrix.numberOfRows).to.equal(3)
        expect(matrix.numberOfColumns).to.equal(2)
    }

    it("does not tolerate differing row sizes") {
        try {
            Matrix(
                    arrayOf(KnownFixedPoint(1.0), KnownFixedPoint(2.0)),
                    arrayOf(KnownFixedPoint(3.0), KnownFixedPoint(4.0), KnownFixedPoint(5.0))
            )
            fail()
        } catch (exception: IllegalArgumentException) {
            // success
        }
    }

    it("can be transposed") {
        val transposed = matrix.transpose()
        expect(evaluate(transposed[0, 0])).to.equal(evaluate(matrix[0, 0]))
        expect(evaluate(transposed[0, 1])).to.equal(evaluate(matrix[1, 0]))
        expect(evaluate(transposed[1, 2])).to.equal(evaluate(matrix[2, 1]))
    }

    it("can return a row") {
        val firstRow = matrix.row(0)
        val expected = Vector(1.0, 2.0)
        expect(evaluate(firstRow)).to.equal(evaluate(expected))
    }

    it("can be multiplied with another matrix") {
        val otherMatrix = Matrix(
                arrayOf(KnownFixedPoint(10.0), KnownFixedPoint(20.0), KnownFixedPoint(30.0)),
                arrayOf(KnownFixedPoint(40.0), KnownFixedPoint(50.0), KnownFixedPoint(60.0))
        )
        val multiplied = matrix * otherMatrix
        expect(evaluate(multiplied[0, 0])).to.be.closeTo(90.0, 0.01)
        expect(evaluate(multiplied[0, 1])).to.be.closeTo(120.0, 0.01)
        expect(evaluate(multiplied[0, 2])).to.be.closeTo(150.0, 0.01)
        expect(evaluate(multiplied[1, 1])).to.be.closeTo(260.0, 0.01)
        expect(evaluate(multiplied[2, 2])).to.be.closeTo(510.0, 0.01)
    }

    it("throws when attempting to multiply incompatible matrices") {
        val otherMatrix = Matrix(
                arrayOf(KnownFixedPoint(10.0), KnownFixedPoint(20.0), KnownFixedPoint(30.0)),
                arrayOf(KnownFixedPoint(40.0), KnownFixedPoint(50.0), KnownFixedPoint(60.0)),
                arrayOf(KnownFixedPoint(70.0), KnownFixedPoint(80.0), KnownFixedPoint(90.0))
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
        expect(evaluate(multiplied[0, 0])).to.be.closeTo(3.0, delta = 0.01)
        expect(evaluate(multiplied[2, 1])).to.be.closeTo(18.0, delta = 0.01)
    }

    it("can be multiplied by a scalar prefix") {
        val multiplied = 3.0 * matrix
        expect(evaluate(multiplied[0, 0])).to.be.closeTo(3.0, delta = 0.01)
        expect(evaluate(multiplied[2, 1])).to.be.closeTo(18.0, delta = 0.01)
    }

    it("can subtract matrices") {
        val toBeSubtracted = Matrix(
                arrayOf(KnownFixedPoint(2.0), KnownFixedPoint(4.0)),
                arrayOf(KnownFixedPoint(6.0), KnownFixedPoint(8.0)),
                arrayOf(KnownFixedPoint(10.0), KnownFixedPoint(12.0))
        )
        val expected = plain.Matrix(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        )
        expect(evaluate(toBeSubtracted - matrix)).to.equal(expected)
    }

    it("throws when subtracting incompatible matrices") {
        val toBeSubtracted = Matrix(
                arrayOf(KnownFixedPoint(2.0), KnownFixedPoint(4.0)),
                arrayOf(KnownFixedPoint(6.0), KnownFixedPoint(8.0))
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
                arrayOf(KnownFixedPoint(2.0), KnownFixedPoint(4.0)),
                arrayOf(KnownFixedPoint(6.0), KnownFixedPoint(8.0)),
                arrayOf(KnownFixedPoint(10.0), KnownFixedPoint(12.0))
        )
        val expected = plain.Matrix(
                arrayOf(3.0, 6.0),
                arrayOf(9.0, 12.0),
                arrayOf(15.0, 18.0)
        )
        expect(evaluate(matrix + toBeAdded)).to.equal(expected)
    }

    it("throws when adding incompatible matrices") {
        val toBeAdded= Matrix(
                arrayOf(KnownFixedPoint(2.0), KnownFixedPoint(4.0)),
                arrayOf(KnownFixedPoint(6.0), KnownFixedPoint(8.0))
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
                arrayOf(KnownFixedPoint(1.0), KnownFixedPoint(0.0)),
                arrayOf(KnownFixedPoint(2.0), KnownFixedPoint(1.0))
        )).transpose()

        it("returns an upper triangular matrix") {
            expect(transposed as? UpperTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = plain.UpperTriangularMatrix(plain.Matrix(
                    arrayOf(1.0, 2.0),
                    arrayOf(0.0, 1.0)
            ))
            expect(evaluate(transposed)).to.equal(expected)
        }
    }

    context("when transposing an upper triangular matrix") {
        val transposed = UpperTriangularMatrix(Matrix(
                arrayOf(KnownFixedPoint(1.0), KnownFixedPoint(2.0)),
                arrayOf(KnownFixedPoint(0.0), KnownFixedPoint(1.0))
        )).transpose()

        it("returns a lower triangular matrix") {
            expect(transposed as? LowerTriangularMatrix).to.not.be.`null`
        }

        it("returns the correct matrix") {
            val expected = plain.LowerTriangularMatrix(plain.Matrix(
                    arrayOf(1.0, 0.0),
                    arrayOf(2.0, 1.0)
            ))
            expect(evaluate(transposed)).to.equal(expected)
        }
    }

    it("can create a matrix from multiple vectors") {
        val v1 = Vector(1.0, 2.0, 3.0, 4.0)
        val v2 = Vector(11.0, 22.0, 33.0, 44.0)
        val m = matrixFromVectors(v1, v2)
        val expected = plain.Matrix(
                arrayOf(1.0, 2.0, 3.0, 4.0),
                arrayOf(11.0, 22.0, 33.0, 44.0)
        )
        expect(evaluate(m)).to.equal(expected)
    }

    it("can create an identity matrix") {
        val expected = plain.Matrix(
                arrayOf(1.0, 0.0, 0.0),
                arrayOf(0.0, 1.0, 0.0),
                arrayOf(0.0, 0.0, 1.0)
        )
        expect(evaluate(IdentityMatrix(3))).to.equal(expected)
    }

    it("can create a closed matrix") {
        val plainMatrix = plain.Matrix(
                arrayOf(1.1, 2.2),
                arrayOf(3.3, 4.4))
        val result = evaluate(closeMatrix(plainMatrix, 1))
        expect(result.isCloseTo(plainMatrix, 0.01)).to.be.`true`
    }

    it("can create a zero matrix") {
        val expected = plain.Matrix(
                arrayOf(0.0, 0.0, 0.0),
                arrayOf(0.0, 0.0, 0.0),
                arrayOf(0.0, 0.0, 0.0)
        )
        expect(evaluate(ZeroMatrix(3) as MatrixType)).to.equal(expected)
    }
})
