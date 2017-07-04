class LogisticRegression {
    fun hessian(matrix: MatrixType): MatrixType {
        return -0.25 * (matrix.transpose() * matrix)
    }
}
