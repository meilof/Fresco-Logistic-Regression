package fresco

import fresco.dsl.FixedPointExpression
import fresco.dsl.KnownFixedPoint

abstract class MatrixType {
    abstract val numberOfColumns: Int
    abstract val numberOfRows: Int

    abstract operator fun get(row: Int, column: Int): FixedPointExpression

    fun row(index: Int): Vector {
        val row = Array<FixedPointExpression>(numberOfColumns, {
            KnownFixedPoint(0.0) })
        for (column in 0 until numberOfColumns) {
            row[column] = get(index, column)
        }
        return Vector(*row)
    }

    open fun transpose(): MatrixType {
        return TransposedMatrix(this)
    }

    operator fun times(other: MatrixType): MatrixType {
        assertMultiplicationCompatibility(this, other)
        val rows = numberOfRows
        val columns = other.numberOfColumns
        val multiplied = Array(rows, {
            Array<FixedPointExpression>(columns, { KnownFixedPoint(0.0) })
        })
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                for (i in 0 until numberOfColumns) {
                    multiplied[row][column] +=
                            this[row, i] * other[i, column]
                }
            }
        }

        return Matrix(*multiplied)
    }

    private fun assertMultiplicationCompatibility(a: MatrixType, b: MatrixType) {
        val compatible = a.numberOfColumns == b.numberOfRows
        if (!compatible) {
            throw IllegalArgumentException("these matrices cannot be multiplied")
        }
    }

    open operator fun times(vector: Vector): Vector {
        return (this * vector.transpose()).transpose().row(0)
    }

    open operator fun times(scalar: FixedPointExpression): MatrixType {
        return MultipliedMatrix(this, scalar)
    }

    open operator fun times(scalar: Double): MatrixType {
        return MultipliedMatrix(this, KnownFixedPoint(scalar))
    }

    operator fun minus(other: MatrixType): MatrixType {
        assertOfSameShape(this, other)
        return SubtractedMatrix(this, other)
    }

    operator fun plus(other: MatrixType): MatrixType {
        assertOfSameShape(this, other)
        return AddedMatrix(this, other)
    }

    private fun assertOfSameShape(a: MatrixType, b: MatrixType) {
        val sameShape = a.numberOfRows == b.numberOfRows &&
                a.numberOfColumns == b.numberOfColumns
        if (!sameShape) {
            throw IllegalArgumentException("these matrices are not of the same shape")
        }
    }
}

class Matrix(vararg val elements: Array<FixedPointExpression>): MatrixType() {
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

    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return elements[row][column]
    }
}

class TransposedMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfColumns
    override val numberOfColumns: Int
        get() = matrix.numberOfRows
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return matrix[column, row]
    }
}

class MultipliedMatrix(val matrix: MatrixType, val scalar: FixedPointExpression):
        MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return matrix[row, column] * scalar
    }
}

class LowerTriangularMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        if (column > row) {
            return KnownFixedPoint(0.0)
        } else {
            return matrix[row, column]
        }
    }
    override fun transpose(): UpperTriangularMatrix {
        return UpperTriangularMatrix(super.transpose())
    }
}

class UpperTriangularMatrix(val matrix: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = matrix.numberOfRows
    override val numberOfColumns: Int
        get() = matrix.numberOfColumns
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        if (column < row) {
            return KnownFixedPoint(0.0)
        } else {
            return matrix[row, column]
        }
    }

    override fun transpose(): LowerTriangularMatrix {
        return LowerTriangularMatrix(super.transpose())
    }
}

class SubtractedMatrix(val left: MatrixType, val right: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = left.numberOfRows
    override val numberOfColumns: Int
        get() = left.numberOfColumns
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return left[row, column] - right[row, column]
    }
}

class AddedMatrix(val left: MatrixType, val right: MatrixType): MatrixType() {
    override val numberOfRows: Int
        get() = left.numberOfRows
    override val numberOfColumns: Int
        get() = left.numberOfColumns
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return left[row, column] + right[row, column]
    }
}

class IdentityMatrix(size: Int): MatrixType() {
    override val numberOfColumns: Int = size
    override val numberOfRows: Int = size
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        if (row == column) {
            return KnownFixedPoint(1.0)
        } else {
            return KnownFixedPoint(0.0)
        }
    }
}

operator fun Double.times(matrix: MatrixType): MatrixType {
    return matrix * KnownFixedPoint(this)
}

fun matrixFromVectors(vararg vectors: Vector): MatrixType {
    val rows = vectors.size
    val columns = vectors[0].size
    val result = Array(rows, { Array<FixedPointExpression>(columns, {
        KnownFixedPoint(0.0) })
    })
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            result[row][column] = vectors[row].get(column)
        }
    }
    return Matrix(*result)
}
