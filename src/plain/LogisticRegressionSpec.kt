package plain

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
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
                arrayOf(1.0 / Math.sqrt(2.0), Math.sqrt(3.0 / 2.0))
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

    it("computes best beta for logistic regression") {
        val intercept = 18.86630
        val beta_hp = 0.03626
        val beta_wt = -8.08348

        val ones = Vector(*DoubleArray(hp.size, { 1.0 }))
        val X = matrixFromVectors(hp, wt, ones).transpose()
        val Y = am
        val beta = logistic.fitLogisticModel(
                X, Y, numberOfIterations = 350
        )

        expect(beta.isCloseTo(
                Vector(beta_hp, beta_wt, intercept), 0.01
        )).to.be.`true`
    }

    it("computes best beta for logistic regression with non-zero lambda") {
        val intercept = 1.65707
        val beta_hp = 0.00968555
        val beta_wt = -1.17481

        val ones = Vector(*DoubleArray(hp.size, { 1.0 }))
        val X = matrixFromVectors(hp, wt, ones).transpose()
        val Y = am
        val beta = logistic.fitLogisticModel(
                X, Y, lambda = 1.0, numberOfIterations = 4
        )

        expect(beta.isCloseTo(
                Vector(beta_hp, beta_wt, intercept), 0.01
        )).to.be.`true`
    }
})

val hp = Vector(110.0, 110.0, 93.0, 110.0, 175.0, 105.0, 245.0, 62.0,
        95.0, 123.0, 123.0, 180.0, 180.0, 180.0, 205.0, 215.0, 230.0,
        66.0, 52.0, 65.0, 97.0, 150.0, 150.0, 245.0, 175.0, 66.0,
        91.0, 113.0, 264.0, 175.0, 335.0, 109.0)
val wt = Vector(2.62, 2.875, 2.32, 3.215, 3.44, 3.46, 3.57, 3.19, 3.15,
        3.44, 3.44, 4.07, 3.73, 3.78, 5.25, 5.424, 5.345, 2.2, 1.615,
        1.835, 2.465, 3.52, 3.435, 3.84, 3.845, 1.935, 2.14, 1.513,
        3.17, 2.77, 3.57, 2.78)
val am = Vector(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
