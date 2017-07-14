package fresco.logisticRegression

import fresco.dsl.ClosedFixedPoint
import fresco.dsl.FixedPointExpression
import fresco.dsl.KnownFixedPoint

class Vector(vararg val elements: FixedPointExpression): MatrixType() {
    override val numberOfRows = 1
    override val numberOfColumns = elements.size

    constructor(vararg elements: Double): this(
            *elements.toTypedArray().map {
                d -> ClosedFixedPoint(d, 1)
            }.toTypedArray())

    operator fun get(index: Int): FixedPointExpression {
        return elements[index]
    }
    override operator fun get(row: Int, column: Int): FixedPointExpression {
        return this[column]
    }
    operator fun plus(other: Vector): Vector {
        return super.plus(other).row(0)
    }
    operator fun minus(other: Vector): Vector {
        return super.minus(other).row(0)
    }
    override fun times(scalar: FixedPointExpression): Vector {
        return super.times(scalar).row(0)
    }
    override fun times(scalar: Double): Vector {
        return super.times(KnownFixedPoint(scalar)).row(0)
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
    return vector * KnownFixedPoint(this)
}
