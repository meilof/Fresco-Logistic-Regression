package plain

val numIterations = 40

fun exp(a: Double): Double {
    return exp(a, 1)
}

fun exp(a: Double, iteration: Int): Double {
    if (iteration > numIterations) { return a }

    return 1.0 + a / iteration * exp(a, iteration + 1)
    // optimization: precompute 1/2, 1/3, 1/4, ..., 1/15
}
