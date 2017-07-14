package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

open class FixedPoint(override val underlyingInt: IntExpression) : FixedPointExpression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return underlyingInt.build(builder)
    }
}

class KnownFixedPoint(val value: Double)
    : FixedPoint(KnownInt(value.toFixedPoint()))

class ClosedFixedPoint(val value: Double, inputParty: Int)
    : FixedPoint(ClosedInt(value.toFixedPoint(), inputParty))
