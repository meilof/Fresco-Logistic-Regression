package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class FixedPointExpressionSpec : Spek({
    val k1 = KnownFixedPoint(1.11)
    val k2 = KnownFixedPoint(2.22)

    it("supports addition") {
        expect(evaluate(k1 + k2)).to.be.closeTo(3.33, delta = 0.01)
    }
})
