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
}
