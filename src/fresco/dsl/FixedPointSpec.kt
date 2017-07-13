package fresco.dsl

import com.winterbe.expekt.expect
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.mockito.Mockito

class FixedPointSpec : Spek({

    val value = 1.23
    val party = 1

    it("can create known fixed point number") {
        val builder = Mockito.mock(ProtocolBuilderNumeric::class.java, Mockito.RETURNS_DEEP_STUBS)
        KnownFixedPoint(value).build(builder)
        Mockito.verify(builder.numeric()).known(value.toFixedPoint())
    }

    it("can create closed fixed point number") {
        val builder = Mockito.mock(ProtocolBuilderNumeric::class.java, Mockito.RETURNS_DEEP_STUBS)
        ClosedFixedPoint(value, party).build(builder)
        Mockito.verify(builder.numeric()).input(value.toFixedPoint(), party)
    }

    it("can be evaluated") {
        expect(evaluate(KnownFixedPoint(value))).to.be.closeTo(value, delta = 0.01)
    }
})
