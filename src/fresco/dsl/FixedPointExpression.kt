package fresco.dsl

import java.math.BigInteger

private typealias Bits = Int
private val precision: Bits = 16
private val precisionDiv2: Bits = 8
private val multiplier = Math.pow(2.0, precision.toDouble())
private val multiplierDiv2 = Math.pow(2.0, precisionDiv2.toDouble())
private val knownMultiplier = KnownInt(multiplier.toInt())
private val knownMultiplierDiv2 = KnownInt(multiplierDiv2.toInt())

interface FixedPointExpression : Expression {
    val underlyingInt: IntExpression

    operator fun plus(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt + other.underlyingInt)
    }

    operator fun minus(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt - other.underlyingInt)
    }

    operator fun times(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(truncate(this.underlyingInt * other.underlyingInt, precision))
    }

    operator fun div(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt * knownMultiplier / other.underlyingInt)
    }
}

fun sqrt(value: FixedPointExpression): FixedPointExpression {
    //return FixedPoint(sqrt(value.underlyingInt * knownMultiplier))
    return FixedPoint(sqrt(value.underlyingInt * knownMultiplier))
}

fun Double.toFixedPoint(): BigInteger {
    return BigInteger.valueOf((this * multiplier).toLong())
}

fun BigInteger.asFixedPoint(): Double {
    return this.toDouble() / multiplier
}
