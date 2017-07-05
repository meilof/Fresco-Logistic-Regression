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
        return LowerTriangularMatrix(Matrix(a))
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
        return Matrix(y)
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
        return Matrix(x)
    }
}
