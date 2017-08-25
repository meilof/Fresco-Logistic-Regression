package fresco.dsl

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt
import java.lang.ref.WeakReference

interface Expression {
    fun build(builder: ProtocolBuilderNumeric): Computation<SInt>
}

abstract class Cached : Expression {
    var latestVal : Computation<SInt>? = null //WeakReference<Computation<SInt>>? = null
    //var latestBuilder : WeakReference<ProtocolBuilderNumeric>? = null

    override fun build(builder: ProtocolBuilderNumeric): Computation<SInt> {
        //val possibleResult = latestValue?.get()
        if (/*latestBuilder?.get() === builder &&*/ latestVal != null) {
            return latestVal!!
        } else {
            val result = buildThis(builder)
            latestVal = result
            //latestBuilder = WeakReference(builder)
            return result
        }
    }

    abstract fun buildThis(builder: ProtocolBuilderNumeric): Computation<SInt>
}
