package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class ExpressionSpec : Spek({

    val s1 = KnownInt(1)
    val s2 = KnownInt(2)
    val s3 = KnownInt(3)

    it("supports addition") {
        expect(evaluate(s2 + s3)).to.equal(5)
    }

    it("supports subtraction") {
        expect(evaluate(s3 - s2)).to.equal(1)
    }

    it("can chain expressions") {
        expect(evaluate(s3 + s2 - s1)).to.equal(4)
        expect(evaluate(s3 - s2 + s1)).to.equal(2)
    }
})

