package fresco.dsl

import com.winterbe.expekt.expect
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.mockito.Mockito.*
import java.math.BigInteger

class ClosedIntSpec : Spek({
    val party = 1
    val value = 3
    val s1 = ClosedInt(value, party)

    it("can be evaluated as a Fresco computation") {
        expect(evaluate(s1)).to.equal(value)
    }

    it("creates a closed Fresco SInt") {
        val builder = mock(ProtocolBuilderNumeric::class.java, RETURNS_DEEP_STUBS)
        s1.build(builder)
        verify(builder.numeric()).input(BigInteger.valueOf(value.toLong()), party)
    }
})
