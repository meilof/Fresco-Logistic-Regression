package fresco.dsl

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.xit

class MultiPartyEvaluationSpec : Spek({
    xit("can do a multiparty evaluation") {
        val sum1 = ClosedInt(2, 1) + ClosedInt(0, 2)
        val sum2 = ClosedInt(0, 1) + ClosedInt(40, 2)
        expect(evaluate(sum1, sum2)).to.equal(listOf(42, 42))
    }
})
