import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class LogisticRegressionSpec: Spek({

    val logistic = LogisticRegression()

    it("calculates an approximation of the Hessian matrix") {
        val matrix = Matrix(arrayOf(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0)
        ))
        val expected = Matrix(arrayOf(
                arrayOf(-8.75, -11.0),
                arrayOf(-11.0, -14.0)
        ))
        expect(logistic.hessian(matrix)).to.equal(expected)
    }

    it("calculates the Cholesky decomposition") {
        val matrix = Matrix(arrayOf(
                arrayOf(2.0, 1.0),
                arrayOf(1.0, 2.0)
        ))
        val expected = LowerTriangularMatrix(Matrix(arrayOf(
                arrayOf(Math.sqrt(2.0), 0.0),
                arrayOf(1.0/ Math.sqrt(2.0), Math.sqrt(3.0 / 2.0))
        )))
        expect(logistic.choleskyDecomposition(matrix)).to.equal(expected)
    }

    it("performs forward substitution") {
        val L = LowerTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, 0.0, 0.0),
                arrayOf(-2.0, 1.0, 0.0),
                arrayOf(1.0, 6.0, 1.0)
        )))
        val b = Vector(2.0, -1.0, 4.0).transpose()
        val expected = Vector(2.0, 3.0, -16.0).transpose()
        val x = logistic.forwardSubstitution(L, b)
        expect(x).to.equal(expected)
    }

    it("performs back substitution") {
        val U = UpperTriangularMatrix(Matrix(arrayOf(
                arrayOf(1.0, -2.0, 1.0),
                arrayOf(0.0, 1.0, 6.0),
                arrayOf(0.0, 0.0, 1.0)
        )))
        val b = Vector(4.0, -1.0, 2.0).transpose()
        val expected = Vector(-24.0, -13.0, 2.0).transpose()
        val x = logistic.backSubstitution(U, b)
        expect(x).to.equal(expected)
    }

    it("updates learned model using previous value and first derivative") {
        val X = Matrix(arrayOf(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0)
        ))
        val H = logistic.hessian(X)
        val l = Vector(7.0, 8.0).transpose()
        var beta = Vector(5.0, 6.0).transpose()

        beta = logistic.updateLearnedModel(H, beta, l)
        
        val expected = Vector(33.0, -12.0).transpose()
        expect(beta.isCloseTo(expected, 0.00001)).to.be.`true`
    }
})
