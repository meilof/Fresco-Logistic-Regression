package fresco.dsl

import java.math.BigInteger

private typealias Bits = Int
private val precision: Bits = 16
private val multiplier = Math.pow(2.0, precision.toDouble())
private val knownMultiplier = KnownInt(multiplier.toInt())

interface FixedPointExpression : Expression {
    val underlyingInt: IntExpression

    operator fun plus(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt + other.underlyingInt)
    }

    operator fun minus(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt - other.underlyingInt)
    }

    operator fun times(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint((this.underlyingInt * other.underlyingInt) / knownMultiplier)
    }

    operator fun div(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint((this.underlyingInt / other.underlyingInt) * knownMultiplier)
    }
}

fun sqrt(value: FixedPointExpression): FixedPointExpression {
    return FixedPoint(sqrt(value.underlyingInt * knownMultiplier))
}

fun Double.toFixedPoint(): BigInteger {
    return BigInteger.valueOf((this * multiplier).toLong())
}

fun BigInteger.asFixedPoint(): Double {
    return this.toDouble() / multiplier
}
