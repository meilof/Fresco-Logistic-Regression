package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

class FixedPoint(val value: Double) : FixedPointExpression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().known(value.toFixedPoint())
    }
}
