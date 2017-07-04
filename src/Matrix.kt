interface MatrixType<T> {
    fun element(row: Int, column: Int): T?
}

class Matrix<T>(val elements: Array<Array<T>>): MatrixType<T> {
    init {
        var numberOfColumns: Int? = null
        elements.forEach { element ->
            if (numberOfColumns == null) {
                numberOfColumns = element.size
            }
            if (numberOfColumns != element.size) {
                throw IllegalArgumentException("Rows are not of equal size")
            }
        }
    }

    override fun element(row: Int, column: Int): T? {
        return elements[row][column]
    }

    fun transpose(): MatrixType<T> {
        return TransposedMatrix(this)
    }
}

class TransposedMatrix<T>(val matrix: MatrixType<T>): MatrixType<T> {
    override fun element(row: Int, column: Int): T? {
        return matrix.element(row = column, column = row)
    }
}
