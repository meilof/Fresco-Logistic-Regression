import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class MatrixSpec : Spek({
    val matrix: Matrix<Double> = Matrix(arrayOf(
            arrayOf(1.0, 2.0),
            arrayOf(3.0, 4.0),
            arrayOf(5.0, 6.0)
    ))

    it("can recall its elements") {
        expect(matrix.element(row = 0, column = 0)).to.equal(1.0)
        expect(matrix.element(row = 2, column = 1)).to.equal(6.0)
    }
})
