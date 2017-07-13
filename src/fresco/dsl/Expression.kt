package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt

interface Expression {
    fun build(builder: ProtocolBuilderNumeric): Computation<SInt>
}
