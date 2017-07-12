package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.BuilderFactoryNumeric
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric.createApplicationRoot
import dk.alexandra.fresco.framework.value.SInt
import java.math.BigInteger

class SecureInt(val value: Int) {
    fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().known(BigInteger.valueOf(value.toLong()))
    }
}
