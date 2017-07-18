package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt
import java.math.BigInteger

class ClosedInt(val value: BigInteger, val inputParty: Int) : Cached(), IntExpression {
    constructor(value: Int, inputParty: Int) :
            this(BigInteger.valueOf(value.toLong()), inputParty)

    override fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().input(BigInteger.valueOf(value.toLong()), inputParty)
    }
}
