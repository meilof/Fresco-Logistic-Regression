package logisticRegression

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class MathSpec: Spek({
    it("can calculate exponential") {
        expect(exp(5.0)).to.be.closeTo(Math.exp(5.0), 0.1)
    }
})
