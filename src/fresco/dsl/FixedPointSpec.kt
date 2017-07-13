package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class FixedPointSpec : Spek({

    it("can be created") {
        FixedPoint(1.23)
    }

    it("can be evaluated") {
        expect(evaluate(FixedPoint(1.23))).to.be.closeTo(1.23, delta = 0.01)
    }
})
