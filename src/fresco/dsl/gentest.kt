package fresco.dsl

import java.math.BigInteger

fun generator() = generate<Int, String> {
    var c = 1
    while (true) {
        val op = yield(c)
        when (op) {
            "inc" -> c += 1
            "mult" -> c *= 2
        }
    }
}

fun yielder() = generate<Any, Any> {
    var opened = yield(ClosedInt(5, 1)) as BigInteger // receive opened value

    var sq = opened*opened // compute in the plain

    var closed = ClosedInt(sq, 1); // now close again

    yield(closed);
}

fun main(args: Array<String>) {

    val proto = yielder()
    val ex = proto.next(Integer(0)) // send ignored value, get request to compute
    val eval = evaluate(ex as IntExpression)
    val ex2 = proto.next(eval as Integer) // next step

    println("Got " + evaluate(ex2 as IntExpression))


    println(evaluate(ClosedInt(5, 1)))
    println(evaluate(ClosedInt(5, 1)))


    val g = generator()
    val a = g.next("") // start
    val b = g.next("inc")
    val c = g.next("mult")
    val d = g.next("inc")
    println("$a $b $c $d") // 1, 2, 4, 5
}