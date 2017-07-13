package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class ExpressionSpec : Spek({

    val k1 = KnownInt(1)
    val k2 = KnownInt(2)
    val k3 = KnownInt(3)
    val c4 = ClosedInt(4, 1)

    it("supports addition") {
        expect(evaluate(k2 + k3)).to.equal(5)
    }

    it("supports subtraction") {
        expect(evaluate(k3 - k2)).to.equal(1)
    }

    it("can chain expressions") {
        expect(evaluate(k3 + k2 - k1)).to.equal(4)
        expect(evaluate(k3 - k2 + k1)).to.equal(2)
    }

    it("supports multiplication") {
        expect(evaluate(k2 * k3)).to.equal(6)
    }

    it("supports divisions") {
        expect(evaluate(c4 / k2)).to.equal(2)
    }

    it("supports adding a known and a closed integer") {
        expect(evaluate(k3 + c4)).to.equal(7)
    }
})

