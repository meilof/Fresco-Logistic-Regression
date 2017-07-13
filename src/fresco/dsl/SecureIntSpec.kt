package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class SecureIntSpec : Spek({

    val s1 = SecureInt(1)
    val s2 = SecureInt(2)
    val s3 = SecureInt(3)

    it("can be evaluated as a Fresco computation") {
        expect(evaluate(s1)).to.equal(1)
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

