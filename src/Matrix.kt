interface MatrixType {
    fun element(row: Int, column: Int): Double?
}

class Matrix(val elements: Array<Array<Double>>): MatrixType {
    val numberOfColumns: Int

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
        this.numberOfColumns = numberOfColumns ?: 0
    }

    override fun element(row: Int, column: Int): Double {
        return elements[row][column]
    }

    fun transpose(): MatrixType {
        return TransposedMatrix(this)
    }

    operator fun times(other: Matrix): Matrix {
        val rows = elements.size
        val columns = other.numberOfColumns
        val multiplied = Array<Array<Double>>(rows, {
            i -> Array<Double>(columns, { j -> 0.0 })
        })
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                for (i in 0 until numberOfColumns) {
                    multiplied[row][column] +=
                            this.element(row, i) * other.element(i, column)
                }
            }
        }

        return Matrix(multiplied)
    }
}

class TransposedMatrix(val matrix: MatrixType): MatrixType {
    override fun element(row: Int, column: Int): Double? {
        return matrix.element(row = column, column = row)
    }
}
