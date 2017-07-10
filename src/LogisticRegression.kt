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
            a[j][j] = Math.sqrt(a[j][j])
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
    fun forwardSubstitution(L: LowerTriangularMatrix, b: MatrixType): MatrixType {
        val n = b.numberOfRows
        val y = Array(n, { row -> arrayOf(0.0) })
        for (i in 0 until n) {
            y[i][0] = b[i, 0]
            for (j in 0 until i) {
                y[i][0] -= L[i, j] * y[j][0]
            }
            y[i][0] /= L[i, i]
        }
        return Matrix(*y)
    }

    /**
     * Calculates the solution for the equation Ux=b,
     * using back substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun backSubstitution(U: UpperTriangularMatrix, b: MatrixType): MatrixType {
        val n = b.numberOfRows
        val x = Array(n, { row -> arrayOf(0.0) })
        for (i in (0 until n).reversed()) {
            x[i][0] = b[i, 0]
            for (j in i+1 until n) {
                x[i][0] -= U[i, j] * x[j][0]
            }
            x[i][0] /= U[i, i]
        }
        return Matrix(*x)
    }

    fun likelihood(v1: MatrixType, v2: MatrixType): Double {
        if (v1.numberOfRows != v2.numberOfRows) {
            throw IllegalArgumentException("vectors have different number" +
                    " of elements")
        }
        if (v1.numberOfColumns != 1 || v2.numberOfColumns != 1) {
            throw IllegalArgumentException("input must be vectors")
        }
        val exponential = exp(-v1.transpose().times(v2)[0, 0])
        return 1.0 / (1.0 + exponential)
    }

    fun logLikelihoodPrime(
            x: MatrixType, y: MatrixType, beta: MatrixType): MatrixType {
        val result = Array(beta.numberOfRows, { Array(1, { 0.0 }) })
        for (k in 0 until beta.numberOfRows) {
            for (i in 0 until x.numberOfRows) {
                result[k][0] += (
                        y[i,0] - likelihood(x.row(i), beta)
                        ) * x[i,k]
            }
        }
        return Matrix(*result)
    }

    fun updateLearnedModel(H: MatrixType, beta: MatrixType, l: MatrixType): MatrixType {
        val L = choleskyDecomposition(-1.0 * H)
        val y = forwardSubstitution(L, l)
        val r = backSubstitution(L.transpose(), y)
        return beta + r
    }
}
