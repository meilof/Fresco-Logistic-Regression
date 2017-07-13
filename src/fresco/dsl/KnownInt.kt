package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt
import java.math.BigInteger

class KnownInt(val value: BigInteger): IntExpression {
    constructor(value: Int) : this(BigInteger.valueOf(value.toLong()))

    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().known(value)
    }
}
