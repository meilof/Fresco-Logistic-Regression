package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

class KnownFixedPoint(val value: Double) : FixedPointExpression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().known(value.toFixedPoint())
    }
}

class ClosedFixedPoint(val value: Double, val party: Int) : FixedPointExpression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().input(value.toFixedPoint(), party)
    }
}
