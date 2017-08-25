package fresco

import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.value.SInt
import fresco.dsl.*
import fresco.dsl.matrices.*
import fresco.dsl.matrices.Vector
import plain.LogisticRegression
import java.lang.ref.WeakReference
import java.util.*

class LogisticRegression {
    fun hessian(matrix: MatrixType): MatrixType {
        return -0.25 * (matrix.transpose() * matrix)
    }

    fun choleskyDecomposition(matrix: MatrixType): LowerTriangularMatrix {
        println("${Date().time} ------- cholesky")
        val d = matrix.numberOfRows
        val a = Array(d, {
            row -> Array(d, { column -> matrix[row, column] })
        })
        for (j in 0 until d) {
            for (k in 0 until j) {
                for (i in j until d) {
                    a[i][j] -= a[i][k] * a[j][k]
                }
            }
            a[j][j] = sqrt(a[j][j])
            for (k in j + 1 until d) {
                a[k][j] /= a[j][j]
            }
        }
        return LowerTriangularMatrix(Matrix(*a))
    }

    /**
     * Calculates the solution for the equation Lx=b,
     * using forward substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun forwardSubstitution(L: LowerTriangularMatrix, b: Vector):
            Vector {
        println("${Date().time} ------- forward substitution")
        val n = b.size
        val y = Array<FixedPointExpression>(n, { KnownFixedPoint(0.0) })
        for (i in 0 until n) {
            y[i] = b[i]
            for (j in 0 until i) {
                y[i] -= L[i, j] * y[j]
            }
            y[i] /= L[i, i]
        }
        return Vector(*y)
    }

    /**
     * Calculates the solution for the equation Ux=b,
     * using back substitution.
     *
     * See also https://en.wikipedia.org/wiki/Triangular_matrix#Algorithm
     */
    fun backSubstitution(U: UpperTriangularMatrix, b: Vector):
            Vector {
        println("${Date().time} ------- backward substitution")
        val n = b.size
        val x = Array<FixedPointExpression>(n, { KnownFixedPoint(0.0) })
        for (i in (0 until n).reversed()) {
            x[i] = b[i]
            for (j in i+1 until n) {
                x[i] -= U[i, j] * x[j]
            }
            x[i] /= U[i, i]
        }
        return Vector(*x)
    }

    fun likelihood(v1: Vector, v2: Vector): FixedPointExpression {
        println("${Date().time} ------- likelihood")
        val minusv1v2 = (v1 * v2)[0]
        val exponential = Math.exp(-evaluate(minusv1v2))
        return KnownFixedPoint(1.0 / (1.0 + exponential))
    }

    fun logLikelihoodPrime(
            x: MatrixType, y: Vector, beta: Vector): Vector {
        println("${Date().time} ------- log likelihood prime")
        val result = Array<FixedPointExpression>(beta.size, { KnownFixedPoint(0.0) })
        for (k in 0 until beta.size) {
            for (i in 0 until x.numberOfRows) {
                result[k] += (
                        y[i] - likelihood(x.row(i), beta)
                        ) * x[i,k]
            }
        }
        return Vector(*result)
    }

    fun updateLearnedModel(L: LowerTriangularMatrix, beta: Vector, l: Vector):
            Vector {
        println("${Date().time} ------- update learned model")
        val y = forwardSubstitution(L, l)
        val r = backSubstitution(L.transpose(), y)
        return beta + r
    }

    fun fitLogisticModel(Xs: Array<plain.MatrixType>,
                         Ys: Array<plain.Vector>,
                         lambda: Double = 0.0,
                         numberOfIterations: Int = 10): Vector {
        var H: MatrixType? = null
        var H0: MatrixType? = null
        for (party in 1 .. Xs.size) {
            val localH = plain.LogisticRegression().hessian(Xs[party - 1])
            if (H == null) {
                println("Inputting " + localH)
                H = closeMatrix(localH, 1)
                H0 = H
            } else {
                H += closeMatrix(localH, 1)
            }
        }

        if (H == null) {
            throw IllegalArgumentException("input must not be empty")
        }

        val I = IdentityMatrix(H.numberOfColumns)
        H -= lambda * I
        var L: fresco.dsl.matrices.MatrixType = choleskyDecomposition(-1.0 * H)
        val plainL = evaluate(H0!!)
        L = LowerTriangularMatrix(closeMatrix(plainL, 1))

        var beta = Vector(*DoubleArray(H.numberOfColumns, { 0.0 }))
        for (i in 0 until numberOfIterations) {
            println("${Date().time} ------- ITERATION: ${i} --------")
            val openBeta = evaluate(beta)
            beta = closeVector(openBeta, 1)
            var lprime: Vector? = null
            for (party in 1 .. Xs.size) {
                val X = Xs[party - 1]
                val Y = Ys[party - 1]
                val localLPrime = plain.LogisticRegression().logLikelihoodPrime(X, Y, openBeta)
                if (lprime == null) {
                    lprime = closeVector(localLPrime, 1)
                } else {
                    lprime += closeVector(localLPrime, 1)
                }
            }

            if (lprime == null) {
                throw IllegalArgumentException("does not happen")
            }

            lprime -= (lambda * beta)
            beta = updateLearnedModel(L, beta, lprime)
        }
        println("${Date().time} ------- DONE")
        return beta
    }

    fun fitLogisticModelCoroutine(Xs: Array<plain.MatrixType>,
                         Ys: Array<plain.Vector>,
                         lambda: Double = 0.0,
                         numberOfIterations: Int = 10) = generate<Any, Any> {

        println("Starting logistic regression")

        var H: MatrixType? = null
        for (party in 1 .. Xs.size) {
            val localH = LogisticRegression().hessian(Xs[party - 1])
            if (H == null) {
                H = closeMatrix(localH, 1)
            } else {
                H += closeMatrix(localH, 1)
            }
        }

        if (H == null) {
            throw IllegalArgumentException("input must not be empty")
        }

        val I = IdentityMatrix(H.numberOfColumns)
        H -= lambda * I
        var L: MatrixType = choleskyDecomposition(-1.0 * H)

        //// TODO: should not actually be opened
        //val plainL = yield(L) as plain.MatrixType
        //L = LowerTriangularMatrix(closeMatrix(plainL, 1))

        // TODO: this is slow
        L = LowerTriangularMatrix(L)

        //L = LowerTriangularMatrix(H)

        var beta = Vector(*DoubleArray(H.numberOfColumns, { 0.0 }))
        for (i in 0 until numberOfIterations) {
            println("${Date().time} ------- ITERATION: ${i} --------")

            val openBeta = yield(beta) as plain.Vector
            //val openBeta = evaluate(beta)

            beta = closeVector(openBeta, 1)
            var lprime: Vector? = null
            for (party in 1 .. Xs.size) {
                val X = Xs[party - 1]
                val Y = Ys[party - 1]
                val localLPrime = LogisticRegression().logLikelihoodPrime(X, Y, openBeta)
                if (lprime == null) {
                    lprime = closeVector(localLPrime, 1)
                } else {
                    lprime += closeVector(localLPrime, 1)
                }
            }

            if (lprime == null) {
                throw IllegalArgumentException("does not happen")
            }

//            for (i in 0 until 3) {
//                var fp = beta[i] as FixedPoint
//                var lij = fp.underlyingInt as Cached
//                println("Using beta[" + i  + "] " + beta[i] + " " + lij.latestValue)
//
//                for (j in 0 until 3) {
//                        fp = L[i,j] as FixedPoint
//                        lij = fp.underlyingInt as Cached
//                        println("Using L[" + i + "," + j + "] " + L[i,j] + " " + lij.latestValue)
//
//                        fp = lprime[i,j] as FixedPoint
//                        lij = fp.underlyingInt as Cached
//                        println("Using lprint[" + i + "," + j + "] " + L[i,j] + " " + lij.latestValue)
//                }
//
//            }

            lprime -= (lambda * beta)
            beta = updateLearnedModel(L, beta, lprime)
        }
        println("${Date().time} ------- DONE")

        yield(beta)
    }

}
