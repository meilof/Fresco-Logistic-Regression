package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class SecureIntSpec : Spek({

    it("can be created") {
        expect(SecureInt(42)).to.not.be.`null`
    }

    it("can be evaluated as a Fresco computation") {
        val i = SecureInt(42)
        expect(evaluate(i)).to.equal(42)
    }

    it("can be added to another secure int") {
        val a = SecureInt(2)
        val b = SecureInt(3)
        expect(evaluate(a + b)).to.equal(5)
    }
})

