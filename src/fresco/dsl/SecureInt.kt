package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.BuilderFactoryNumeric
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric.createApplicationRoot
import dk.alexandra.fresco.framework.value.SInt
import java.math.BigInteger

interface Expression {
    fun build(builder: ProtocolBuilderNumeric): Computation<SInt>
}

class SecureInt(val value: Int): Expression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().known(BigInteger.valueOf(value.toLong()))
    }

    operator fun plus(other: SecureInt): Expression {
        return Add(this, other)
    }
}

class Add(val left: SecureInt, val right: SecureInt): Expression {
    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        return builder.numeric().add(left.build(builder), right.build(builder))
    }
}
