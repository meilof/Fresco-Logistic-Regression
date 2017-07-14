package fresco

import com.winterbe.expekt.expect
import fresco.dsl.evaluate
import fresco.dsl.matrices.Vector
import fresco.dsl.matrices.matrixFromVectors
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class LogisticRegressionSpec: Spek({
    val logistic = LogisticRegression()

    it("computes best beta for logistic regression with non-zero lambda") {
        val intercept = 1.65707
        val beta_hp = 0.00968555
        val beta_wt = -1.17481

        val ones = Vector(*DoubleArray(hp.size, { 1.0 }))
        val X = matrixFromVectors(hp, wt, ones).transpose()
        val Y = am
        val beta = logistic.fitLogisticModel(
                X, Y, lambda = 1.0, numberOfIterations = 4
        )

        expect(evaluate(beta).isCloseTo(
                plain.Vector(beta_hp, beta_wt, intercept), 0.01
        )).to.be.`true`
    }

})

val hp = Vector(110.0, 110.0, 93.0, 110.0, 175.0, 105.0, 245.0, 62.0,
        95.0, 123.0, 123.0, 180.0, 180.0, 180.0, 205.0, 215.0, 230.0,
        66.0, 52.0, 65.0, 97.0, 150.0, 150.0, 245.0, 175.0, 66.0,
        91.0, 113.0, 264.0, 175.0, 335.0, 109.0)
val wt = Vector(2.62, 2.875, 2.32, 3.215, 3.44, 3.46, 3.57, 3.19, 3.15,
        3.44, 3.44, 4.07, 3.73, 3.78, 5.25, 5.424, 5.345, 2.2, 1.615,
        1.835, 2.465, 3.52, 3.435, 3.84, 3.845, 1.935, 2.14, 1.513,
        3.17, 2.77, 3.57, 2.78)
val am = Vector(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
