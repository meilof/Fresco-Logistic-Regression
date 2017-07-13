package fresco.dsl

import com.winterbe.expekt.expect
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class SecureIntSpec : Spek({

    val s1 = KnownInt(1)
    val s2 = KnownInt(2)
    val s3 = KnownInt(3)

    it("can be evaluated as a Fresco computation") {
        expect(evaluate(s1)).to.equal(1)
    }

    it("creates a known Fresco SInt") {
        val builder = mock(ProtocolBuilderNumeric::class.java, RETURNS_DEEP_STUBS)
        s1.build(builder)
        verify(builder.numeric()).known(ArgumentMatchers.any())
    }

    it("can be added to another secure int") {
        expect(evaluate(s2 + s3)).to.equal(5)
    }

    it("can be subtracted from another secure int") {
        expect(evaluate(s3 - s2)).to.equal(1)
    }

    it("can chain expressions") {
        expect(evaluate(s3 + s2 - s1)).to.equal(4)
        expect(evaluate(s3 - s2 + s1)).to.equal(2)
    }
})

