package plain

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class VectorSpec : Spek({
    val matrix = Matrix(
            arrayOf(1.0, 2.0),
            arrayOf(3.0, 4.0),
            arrayOf(5.0, 6.0)
    )

    val v1 = Vector(1.0, 2.0)
    val v2 = Vector(3.0, 5.0)

    it("can create vectors") {
        expect(v1.numberOfRows).to.equal(1)
        expect(v1.numberOfColumns).to.equal(2)
        expect(v1[0]).to.equal(1.0)
        expect(v1[1]).to.equal(2.0)
    }

    it("can add vectors") {
        expect(v1 + v2 is Vector).to.be.`true`
    }

    it("can subtract vectors") {
        expect(v1 - v2 is Vector).to.be.`true`
    }

    it("can multiply a matrix and a vector") {
        expect(matrix * v1 is Vector).to.be.`true`
    }

    it("can multiply a vector by a scalar prefix") {
        expect(2.0 * v1 is Vector).to.be.`true`
    }

    it("can multiply a vector by a scalar postfix") {
        expect(v1 * 2.0 is Vector).to.be.`true`
    }

})
