package fresco.dsl

import dk.alexandra.fresco.framework.*
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric.SequentialNumericBuilder
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration
import dk.alexandra.fresco.framework.network.Network
import dk.alexandra.fresco.framework.network.NetworkingStrategy
import dk.alexandra.fresco.framework.network.SCENetwork
import dk.alexandra.fresco.framework.network.serializers.BigIntegerSerializer
import dk.alexandra.fresco.framework.network.serializers.BigIntegerWithFixedLengthSerializer
import dk.alexandra.fresco.framework.sce.SCEFactory
import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration
import dk.alexandra.fresco.framework.sce.configuration.SCEConfiguration
import dk.alexandra.fresco.framework.sce.evaluator.SequentialEvaluator
import dk.alexandra.fresco.framework.sce.resources.ResourcePool
import dk.alexandra.fresco.framework.sce.resources.storage.StreamedStorage
import dk.alexandra.fresco.suite.ProtocolSuite
import dk.alexandra.fresco.suite.ProtocolSuite.RoundSynchronization
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticBuilderFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePool
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.*
import java.util.logging.Level

private val mod = BigInteger("6703903964971298549787012499123814115273848577471136527425966013026501536706464354255445443244279389455058889493431223951165286470575994074291745908195329")
private val maxBitLength = 200
private val myNetwork = MyNetwork()

fun evaluate(vararg expressions: IntExpression): List<Int> {

    val numberOfParties = expressions.size
    val threads = mutableListOf<Thread>()
    val results = CopyOnWriteArrayList<Int>()

    repeat(numberOfParties) { party ->
        val partyId = party + 1
        val configuration = MyEngineConfiguration(partyId, numberOfParties)
        val suiteConfiguration = MySuiteConfiguration()
        val engine = SCEFactory.getSCEFromConfiguration(configuration, suiteConfiguration)
        val expression = expressions[party]
        val resourcePool = MyResourcePool(partyId, numberOfParties)
        val application = MyApplication(expression)
        threads.add(Thread({
            println("[${partyId}] starting thread")
            val result = engine.runApplication(application, resourcePool)
            results.add(result.toInt())
            println("[${partyId}] done (result: ${result.toInt()})")
        }))
    }

    for (thread in threads) {
        thread.start()
    }

    for (thread in threads) {
        thread.join()
    }

    return results
}

private class MyEngineConfiguration(val partyId: Int, val numberOfParties: Int)
    : SCEConfiguration<ResourcePool> {
    override fun getMyId(): Int {
        return partyId
    }

    override fun getParties(): MutableMap<Int, Party> {
        val result = mutableMapOf<Int, Party>()
        repeat(numberOfParties) { party ->
            result[party] = Party(party, "", 0)
        }
        return result
    }

    override fun getLogLevel(): Level {
        return Level.OFF
    }

    override fun getNetworkStrategy(): NetworkingStrategy {
        return NetworkingStrategy.SCAPI
    }

    override fun getEvaluator(): ProtocolEvaluator<ResourcePool> {
        return SequentialEvaluator()
    }

    override fun getStreamedStorage(): StreamedStorage? {
        return null
    }
}

private class MySuiteConfiguration
    : ProtocolSuiteConfiguration<ResourcePool, SequentialNumericBuilder> {
    override fun createProtocolSuite(myPlayerId: Int): ProtocolSuite<ResourcePool, SequentialNumericBuilder> {
        return MySuite()
    }

    override fun createResourcePool(myId: Int, size: Int, network: Network?, rand: Random?, secRand: SecureRandom?): ResourcePool {
        return MyResourcePool(myId, size)
    }
}

private class MySuite : ProtocolSuite<ResourcePool, SequentialNumericBuilder> {
    override fun init(resourcePool: ResourcePool?): BuilderFactory<SequentialNumericBuilder> {
        val arithmeticFactory = DummyArithmeticFactory(mod, maxBitLength)
        return DummyArithmeticBuilderFactory(arithmeticFactory)
    }

    override fun createRoundSynchronization(): RoundSynchronization<ResourcePool> {
        return MyRoundSynchronization()
    }
}

private class MyResourcePool(val partyId: Int, val numberOfParties: Int)
    : DummyArithmeticResourcePool {
    override fun getNetwork(): Network {
        return myNetwork
    }

    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getSerializer(): BigIntegerSerializer {
        return BigIntegerWithFixedLengthSerializer(mod.toByteArray().size)
    }

    override fun getMyId(): Int {
        return partyId
    }

    override fun getNoOfParties(): Int {
        return numberOfParties
    }

    override fun getRandom(): Random {
        return Random()
    }

    override fun getSecureRandom(): SecureRandom {
        return SecureRandom()
    }
}

private class MyRoundSynchronization
    : RoundSynchronization<ResourcePool>
{
    var batch = 0

    override fun finishedBatch(gatesEvaluated: Int, resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
        println("===== Finished Batch $batch =====")
        batch += 1
    }

    override fun finishedEval(resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
    }
}

private class MyNetwork : dk.alexandra.fresco.framework.network.Network {

    private val queues = HashMap<Int, MutableMap<Int, TransferQueue<ByteArray>>>()

    override fun init(conf: NetworkConfiguration?, channelAmount: Int) {
        println("--- init")
    }

    override fun connect(timeoutMillis: Int) {
        println("--- connect")
    }

    override fun send(channelId: Int, partyId: Int, data: ByteArray?) {
        println("--- send ${channelId} ${partyId} ${data}")
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
        println("--- receive ${channelId} ${partyId} ${result}")
        return result
    }

    override fun close() {
        println("--- close")
    }
}

private class MyApplication(val expression: Expression): Application<BigInteger, SequentialNumericBuilder> {
    override fun prepareApplication(builder: ProtocolBuilderNumeric.SequentialNumericBuilder): Computation<BigInteger> {
        val computation = expression.build(builder)
        val open = builder.numeric().open(computation)
        return open
    }
}

