package fresco.dsl

import dk.alexandra.fresco.framework.*
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric
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
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticBuilderFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticFactory
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePool
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import java.util.logging.Level

private val mod = BigInteger("6703903964971298549787012499123814115273848577471136527425966013026501536706464354255445443244279389455058889493431223951165286470575994074291745908195329")
private val maxBitLength = 200

fun evaluate(i: SecureInt): Int {
    val configuration = DummySCEConfiguration()
    val suite = DummyProtocolSuiteConfiguration()
    val engine = SCEFactory.getSCEFromConfiguration(configuration, suite)
    val result = engine.runApplication(DummyApplication(i), DummyResourcePool())
    return result.toInt()
}

private class DummyApplication(val i: SecureInt): Application<BigInteger, ProtocolBuilderNumeric.SequentialNumericBuilder> {
    override fun prepareApplication(builder: ProtocolBuilderNumeric.SequentialNumericBuilder): Computation<BigInteger> {
        val computation = i.build(builder)
        val open = builder.numeric().open(computation)
        return open
    }
}


private class DummySCEConfiguration: SCEConfiguration<ResourcePool> {
    override fun getMyId(): Int {
        return 1
    }

    override fun getParties(): MutableMap<Int, Party> {
        return mutableMapOf(1 to Party(1, "localhost", 1234))
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

private class DummyProtocolSuiteConfiguration: ProtocolSuiteConfiguration<ResourcePool, ProtocolBuilderNumeric.SequentialNumericBuilder> {
    override fun createProtocolSuite(myPlayerId: Int): ProtocolSuite<ResourcePool, ProtocolBuilderNumeric.SequentialNumericBuilder> {
        return DummyProtocolSuite()
    }

    override fun createResourcePool(myId: Int, size: Int, network: Network?, rand: Random?, secRand: SecureRandom?): ResourcePool {
        return DummyResourcePool()
    }
}

private class DummyProtocolSuite : ProtocolSuite<ResourcePool, ProtocolBuilderNumeric.SequentialNumericBuilder> {
    override fun init(resourcePool: ResourcePool?): BuilderFactory<ProtocolBuilderNumeric.SequentialNumericBuilder> {
        val arithmeticFactory = DummyArithmeticFactory(mod, maxBitLength)
        return DummyArithmeticBuilderFactory(arithmeticFactory)
    }

    override fun createRoundSynchronization(): ProtocolSuite.RoundSynchronization<ResourcePool> {
        return DummyRoundSynchronization()
    }
}

private class DummyRoundSynchronization: ProtocolSuite.RoundSynchronization<ResourcePool> {
    override fun finishedBatch(gatesEvaluated: Int, resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
    }

    override fun finishedEval(resourcePool: ResourcePool?, sceNetwork: SCENetwork?) {
    }
}

private class DummyResourcePool: DummyArithmeticResourcePool {
    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getSerializer(): BigIntegerSerializer {
        return BigIntegerWithFixedLengthSerializer(maxBitLength)
    }

    override fun getMyId(): Int {
        return 1
    }

    override fun getNoOfParties(): Int {
        return 1
    }

    override fun getNetwork(): Network {
        return DummyNetwork()
    }

    override fun getRandom(): Random {
        return Random()
    }

    override fun getSecureRandom(): SecureRandom {
        return SecureRandom()
    }
}

private class DummyNetwork: Network {
    override fun init(conf: NetworkConfiguration?, channelAmount: Int) {
    }

    override fun connect(timeoutMillis: Int) {
    }

    override fun send(channelId: Int, partyId: Int, data: ByteArray?) {
    }

    override fun receive(channelId: Int, partyId: Int): ByteArray {
        return ByteArray(0)
    }

    override fun close() {
    }
}
