package logisticRegression

class Vector(vararg val elements: Double): MatrixType() {
    override val numberOfRows = 1
    override val numberOfColumns = elements.size
    operator fun get(index: Int): Double {
        return elements[index]
    }
    override operator fun get(row: Int, column: Int): Double {
        return this[column]
    }
    operator fun plus(other: Vector): Vector {
        return super.plus(other).row(0)
    }
    operator fun minus(other: Vector): Vector {
        return super.minus(other).row(0)
    }
    override fun times(scalar: Double): Vector {
        return super.times(scalar).row(0)
    }
    var size: Int = elements.size
    override fun toString(): String {
        var string = ""
        for (i in 0 until size) {
            if (string != "") { string += ", " }
            string += "${elements[i]}"
        }
        return "[${string}]"
    }
}

operator fun Double.times(vector: Vector): Vector {
    return vector * this
}
