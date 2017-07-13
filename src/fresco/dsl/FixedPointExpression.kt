package fresco.dsl

import java.math.BigInteger

private typealias Bits = Int
private val fixedPointPrecision: Bits = 8
private val fixedPointMultiplier = Math.pow(2.0, fixedPointPrecision.toDouble())

interface FixedPointExpression : Expression {
    val underlyingInt: IntExpression

    operator fun plus(other: FixedPointExpression): FixedPointExpression {
        return FixedPoint(this.underlyingInt + other.underlyingInt)
    }
}

fun Double.toFixedPoint(): BigInteger {
    return BigInteger.valueOf((this * fixedPointMultiplier).toLong())
}

fun BigInteger.asFixedPoint(): Double {
    return this.toDouble() / fixedPointMultiplier
}
