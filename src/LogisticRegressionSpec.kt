import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.junit.Assert

class LogisticRegressionSpec: Spek({

    val logistic = LogisticRegression()

    it("calculates an approximation of the Hessian matrix") {
        val matrix = Matrix(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        )
        val expected = Matrix(
                arrayOf(-8.75, -11.0),
                arrayOf(-11.0, -14.0)
        )
        expect(logistic.hessian(matrix)).to.equal(expected)
    }

    it("calculates the Cholesky decomposition") {
        val matrix = Matrix(
                arrayOf(2.0, 1.0),
                arrayOf(1.0, 2.0)
        )
        val expected = LowerTriangularMatrix(Matrix(
                arrayOf(Math.sqrt(2.0), 0.0),
                arrayOf(1.0/ Math.sqrt(2.0), Math.sqrt(3.0 / 2.0))
        ))
        expect(logistic.choleskyDecomposition(matrix)).to.equal(expected)
    }

    it("performs forward substitution") {
        val L = LowerTriangularMatrix(Matrix(
                arrayOf(1.0, 0.0, 0.0),
                arrayOf(-2.0, 1.0, 0.0),
                arrayOf(1.0, 6.0, 1.0)
        ))
        val b = Vector(2.0, -1.0, 4.0)
        val expected = Vector(2.0, 3.0, -16.0)
        val x = logistic.forwardSubstitution(L, b)
        expect(x).to.equal(expected)
    }

    it("performs back substitution") {
        val U = UpperTriangularMatrix(Matrix(
                arrayOf(1.0, -2.0, 1.0),
                arrayOf(0.0, 1.0, 6.0),
                arrayOf(0.0, 0.0, 1.0)
        ))
        val b = Vector(4.0, -1.0, 2.0)
        val expected = Vector(-24.0, -13.0, 2.0)
        val x = logistic.backSubstitution(U, b)
        expect(x).to.equal(expected)
    }

    describe("log likelihood") {
        it("computes log likelihood") {
            val xi = Vector(1.0, 2.0)
            val beta = Vector(0.1, 0.2)
            val probability = logistic.likelihood(xi, beta)
            expect(probability).to.be.closeTo(0.6224593, 0.01)
        }

        context("when log likelihood gets invalid input") {
            it("throws on different vector lengths") {
                val xi = Vector(1.0, 2.0, 3.0)
                val beta = Vector(0.1, 0.2)
                try {
                    logistic.likelihood(xi, beta)
                    Assert.fail()
                } catch (exception: IllegalArgumentException) {
                    // success
                }
            }
        }

        it("computes first derivative of log likelihood") {
            val x = Matrix(
                    arrayOf(1.0, 2.0, 3.0, 4.0),
                    arrayOf(1.1, 2.2, 3.3, 4.4)
            )
            val y = Vector(0.0, 1.0)
            val beta = Vector(0.1, 0.2, 0.3, 0.4)
            val result = logistic.logLikelihoodPrime(x, y, beta)
            val expected = Vector(-0.9134458, -1.826892, -2.740337, -3.653783)
            expect(result.isCloseTo(expected, 0.001)).to.be.`true`
        }
    }

    it("updates learned model using previous value and first derivative") {
        val X = Matrix(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0)
        )
        val H = logistic.hessian(X)
        val l = Vector(7.0, 8.0)
        var beta = Vector(5.0, 6.0)

        beta = logistic.updateLearnedModel(H, beta, l)
        
        val expected = Vector(33.0, -12.0)
        expect(beta.isCloseTo(expected, 0.00001)).to.be.`true`
    }
})
