package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

interface Expression {
    fun build(builder: ProtocolBuilderNumeric): Computation<SInt>

    operator fun plus(other: Expression): Expression {
        return Add(this, other)
    }

    operator fun minus(other: Expression): Expression {
        return Subtract(this, other)
    }
}

class Add(val left: Expression, val right: Expression): Expression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().add(left.build(builder), right.build(builder))
    }
}

class Subtract(val left: Expression, val right: Expression) : Expression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().sub(left.build(builder), right.build(builder))
    }
}
