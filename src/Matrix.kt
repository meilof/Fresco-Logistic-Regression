abstract class MatrixType {
    abstract val numberOfColumns: Int
    abstract val numberOfRows: Int

    abstract operator fun get(row: Int, column: Int): Double

    fun transpose(): MatrixType {
        return TransposedMatrix(this)
    }

    operator fun times(other: MatrixType): MatrixType {
        val rows = numberOfRows
        val columns = other.numberOfColumns
        val multiplied = Array(rows, { Array(columns, { 0.0 }) })
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                for (i in 0 until numberOfColumns) {
                    multiplied[row][column] +=
                            this[row, i] * other[i, column]
                }
            }
        }

        return Matrix(multiplied)
    }

    operator fun times(scalar: Double): MatrixType {
        return MultipliedMatrix(this, scalar)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MatrixType) return false
        if (numberOfRows != other.numberOfRows) return false
        if (numberOfColumns != other.numberOfColumns) return false
        for (row in 0 until numberOfRows) {
            @Suppress("LoopToCallChain")
            for (column in 0 until numberOfColumns) {
                if (this[row, column] != other[row, column]) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = numberOfColumns
        result = 31 * result + numberOfRows
        return result
    }
}

class Matrix(val elements: Array<Array<Double>>): MatrixType() {
    override val numberOfColumns: Int
    override val numberOfRows: Int

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
        this.numberOfRows = elements.size
    }

    override operator fun get(row: Int, column: Int): Double {
        return elements[row][column]
    }
}

class TransposedMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfColumns
    override val numberOfColumns: Int
        get() = matrix.numberOfRows
    override operator fun get(row: Int, column: Int): Double {
        return matrix[column, row]
    }
}

class MultipliedMatrix(val matrix: MatrixType, val scalar: Double): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): Double {
        return matrix[row, column] * scalar
    }
}

operator fun Double.times(matrix: MatrixType): MatrixType {
    return matrix * this
}
