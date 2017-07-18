package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

interface IntExpression : Expression {
    operator fun plus(other: IntExpression): IntExpression {
        return Add(this, other)
    }

    operator fun minus(other: IntExpression): IntExpression {
        return Subtract(this, other)
    }

    operator fun times(other: IntExpression): IntExpression {
        return Multiply(this, other)
    }

    operator fun div(other: IntExpression): IntExpression {
        return Divide(this, other)
    }
}

fun sqrt(expr: IntExpression): IntExpression {
    return SquareRoot(expr)
}

private class Add(val left: IntExpression, val right: IntExpression) : Cached(), IntExpression {
    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().add(left.build(builder), right.build(builder))
    }
}

class Subtract(val left: IntExpression, val right: IntExpression) : Cached(), IntExpression {
    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().sub(left.build(builder), right.build(builder))
    }
}

class Multiply(val left: IntExpression, val right: IntExpression) : Cached(), IntExpression {
    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().mult(left.build(builder), right.build(builder))
    }
}

class Divide(val left: IntExpression, val right: IntExpression) : Cached(), IntExpression {
    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.advancedNumeric().div(left.build(builder), right.build(builder))
    }
}

class SquareRoot(val expr: IntExpression) : Cached(), IntExpression {
    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.advancedNumeric().sqrt(expr.build(builder),
                builder.basicNumericFactory.maxBitLength)
    }
}
