package fresco.dsl

import com.winterbe.expekt.expect
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class KnownIntSpec : Spek({

    val s1 = KnownInt(1)

    it("can be evaluated as a Fresco computation") {
        expect(evaluate(s1)).to.equal(1)
    }

    it("creates a known Fresco SInt") {
        val builder = mock(ProtocolBuilderNumeric::class.java, RETURNS_DEEP_STUBS)
        s1.build(builder)
        verify(builder.numeric()).known(ArgumentMatchers.any())
    }
})

