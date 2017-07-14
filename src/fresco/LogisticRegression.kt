package fresco

import fresco.dsl.FixedPointExpression
import fresco.dsl.KnownFixedPoint
import fresco.dsl.evaluate
import fresco.dsl.sqrt

class LogisticRegression {
    fun hessian(matrix: MatrixType): MatrixType {
        return -0.25 * (matrix.transpose() * matrix)
    }

    fun choleskyDecomposition(matrix: MatrixType): LowerTriangularMatrix {
        val d = matrix.numberOfRows
        val a = Array(d, {
            row -> Array(d, { column -> matrix[row, column] })
        })
        for (j in 0 until d) {
            for (k in 0 until j) {
                for (i in j until d) {
                    a[i][j] -= a[i][k] * a[j][k]
                }
            }
            a[j][j] = sqrt(a[j][j])
            for (k in j + 1 until d) {
                a[k][j] /= a[j][j]
            }
        }
        return LowerTriangularMatrix(Matrix(*a))
    }

    /**
     * Calculates the solution for the equation Lx=b,
     * using forward substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun forwardSubstitution(L: LowerTriangularMatrix, b: Vector):
            Vector {
        val n = b.size
        val y = Array<FixedPointExpression>(n, { KnownFixedPoint(0.0) })
        for (i in 0 until n) {
            y[i] = b[i]
            for (j in 0 until i) {
                y[i] -= L[i, j] * y[j]
            }
            y[i] /= L[i, i]
        }
        return Vector(*y)
    }

    /**
     * Calculates the solution for the equation Ux=b,
     * using back substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun backSubstitution(U: UpperTriangularMatrix, b: Vector):
            Vector {
        val n = b.size
        val x = Array<FixedPointExpression>(n, { KnownFixedPoint(0.0) })
        for (i in (0 until n).reversed()) {
            x[i] = b[i]
            for (j in i+1 until n) {
                x[i] -= U[i, j] * x[j]
            }
            x[i] /= U[i, i]
        }
        return Vector(*x)
    }

    fun likelihood(v1: Vector, v2: Vector): FixedPointExpression {
        val minusv1v2 = (v1 * v2)[0]
        val exponential = Math.exp(-evaluate(minusv1v2))
        return KnownFixedPoint(1.0 / (1.0 + exponential))
    }

    fun logLikelihoodPrime(
            x: MatrixType, y: Vector, beta: Vector): Vector {
        val result = Array<FixedPointExpression>(beta.size, { KnownFixedPoint(0.0) })
        for (k in 0 until beta.size) {
            for (i in 0 until x.numberOfRows) {
                result[k] += (
                        y[i] - likelihood(x.row(i), beta)
                        ) * x[i,k]
            }
        }
        return Vector(*result)
    }

    fun updateLearnedModel(H: MatrixType, beta: Vector, l: Vector):
            Vector {
        val L = choleskyDecomposition(-1.0 * H)
        val y = forwardSubstitution(L, l)
        val r = backSubstitution(L.transpose(), y)
        return beta + r
    }

    fun fitLogisticModel(X: MatrixType, Y: Vector,
                         lambda: Double = 0.0,
                         numberOfIterations: Int = 10): Vector {
        val I = IdentityMatrix(X.numberOfColumns)
        val H = hessian(X) - lambda * I
        var beta = Vector(*DoubleArray(X.numberOfColumns, { 0.0 }))
        for (i in 0 until numberOfIterations) {
            var lprime = logLikelihoodPrime(X, Y, beta)
            lprime -= (lambda * beta)
            beta = updateLearnedModel(H, beta, lprime)
        }
        return beta
    }
}
