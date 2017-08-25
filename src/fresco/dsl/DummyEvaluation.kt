package fresco.dsl

import dk.alexandra.fresco.framework.Application
import dk.alexandra.fresco.framework.BuilderFactory
import dk.alexandra.fresco.framework.Computation
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration
import dk.alexandra.fresco.framework.network.Network
import dk.alexandra.fresco.framework.network.SCENetwork
import dk.alexandra.fresco.framework.network.serializers.BigIntegerSerializer
import dk.alexandra.fresco.framework.sce.SCEFactory
import dk.alexandra.fresco.framework.sce.evaluator.SequentialEvaluator
import dk.alexandra.fresco.framework.sce.resources.ResourcePool
import dk.alexandra.fresco.suite.ProtocolSuite
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticBuilderFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePool
import fresco.dsl.matrices.MatrixType
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.TransferQueue

private val mod = BigInteger("6703903964971298549787012499123814115273848577471136527425966013026501536706464354255445443244279389455058889493431223951165286470575994074291745908195329")
private val maxBitLength = 200
private val theNetwork = DummyNetwork()

fun evaluate(expression: IntExpression): Int {
    return evaluate(expression as Expression).toInt()
}

fun evaluate(expression: FixedPointExpression): Double {
    return evaluate(expression as Expression).asFixedPoint()
}

fun evaluate(expression: fresco.dsl.matrices.Vector): plain.Vector {
    println("${ Date().time} --------- START EVALUATE ---------")
    val size = expression.size
    var elements = DoubleArray(size, { 0.0 })
    for (index in 0 until size) {
        val result = evaluate(expression[index])
        println("${ Date().time} ---------------------- result: ${result}")
        elements[index] = result
    }
    println("${ Date().time} --------- EVALUATE DONE ---------")
    return plain.Vector(*elements)
}

fun evaluate(expression: MatrixType): plain.MatrixType {
    println("${ Date().time} --------- START EVALUATE ---------")
    val rows = expression.numberOfRows
    val columns = expression.numberOfColumns
    var elements = Array(rows, { Array(columns, { 0.0 }) })
    for (row in 0 until rows) {
        for (col in 0 until columns) {
            println("${ Date().time} --------- evaluate row ${row} col ${col}")
            val result = evaluate(expression[row, col])
            elements[row][col] = result
            println("${ Date().time} ---------------------- result: ${result}")
        }
    }
    println("${ Date().time} --------- EVALUATE DONE ---------")
    return plain.Matrix(*elements)
}

private fun evaluate(expression: Expression): BigInteger {
    val suite = DummyProtocolSuite()
    val evaluator = SequentialEvaluator<ResourcePool>()
    val engine = SCEFactory.getSCEFromConfiguration(suite, evaluator)
    val result = engine.runApplication(DummyApplication(expression), DummyResourcePool())
    return result.toSigned()
}

public fun BigInteger.toSigned(): BigInteger {
    var actual = this.mod(mod)
    if (actual > mod.div(BigInteger.valueOf(2))) {
        actual = actual.subtract(mod)
    }
    return actual
}

class DummyApplication(val expression: Expression): Application<BigInteger, ProtocolBuilderNumeric.SequentialNumericBuilder> {
    override fun prepareApplication(builder: ProtocolBuilderNumeric.SequentialNumericBuilder): Computation<BigInteger> {
        val computation = expression.build(builder)
        val open = builder.numeric().open(computation)
        return open
    }
}

class DummyProtocolSuite : ProtocolSuite<ResourcePool, ProtocolBuilderNumeric.SequentialNumericBuilder> {
    override fun createResourcePool(myId: Int, size: Int, network: Network?, rand: Random?, secRand: SecureRandom?): ResourcePool {
        return DummyResourcePool()
    }

    override fun init(resourcePool: ResourcePool?): BuilderFactory<ProtocolBuilderNumeric.SequentialNumericBuilder> {
        val arithmeticFactory = DummyArithmeticFactory(mod, maxBitLength)
        return DummyArithmeticBuilderFactory(arithmeticFactory)
    }

    override fun createRoundSynchronization(): ProtocolSuite.RoundSynchronization<ResourcePool> {
        return DummyRoundSynchronization()
    }
}

class DummyRoundSynchronization: ProtocolSuite.RoundSynchronization<ResourcePool> {
    override fun finishedBatch(gatesEvaluated: Int, resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
    }

    override fun finishedEval(resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
    }
}

class DummyResourcePool: DummyArithmeticResourcePool {
    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getSerializer(): BigIntegerSerializer {
        return DummySerializer()
    }

    override fun getMyId(): Int {
        return 1
    }

    override fun getNoOfParties(): Int {
        return 1
    }

    override fun getNetwork(): Network {
        return theNetwork
    }

    override fun getRandom(): Random {
        return Random()
    }

    override fun getSecureRandom(): SecureRandom {
        return SecureRandom()
    }
}

class DummySerializer: BigIntegerSerializer {
    override fun toBytes(bigInteger: BigInteger): ByteArray {
        return bigInteger.toByteArray()
    }

    override fun toBigInteger(byteBuffer: ByteBuffer): BigInteger {
        return BigInteger(byteBuffer.array())
    }
}

class DummyNetwork : dk.alexandra.fresco.framework.network.Network {

    private val queues = HashMap<Int, MutableMap<Int, TransferQueue<ByteArray>>>()

    override fun init(conf: NetworkConfiguration?, channelAmount: Int) {
        println("init")
    }

    override fun connect(timeoutMillis: Int) {
        println("connect")
    }

    override fun send(channelId: Int, partyId: Int, data: ByteArray?) {
        if (!queues.contains(channelId)) {
            queues[channelId] = mutableMapOf()
        }
        if (!queues[channelId]!!.contains(partyId)) {
            queues[channelId]!![partyId] = LinkedTransferQueue()
        }
        queues[channelId]!![partyId]!!.put(data)
    }

    override fun receive(channelId: Int, partyId: Int): ByteArray {
        if (!queues.contains(channelId)) {
            queues[channelId] = mutableMapOf()
        }
        if (!queues[channelId]!!.contains(partyId)) {
            queues[channelId]!![partyId] = LinkedTransferQueue()
        }
        val result = queues[channelId]!![partyId]!!.take()
        return result
    }

    override fun close() {
        println("close")
    }
}

