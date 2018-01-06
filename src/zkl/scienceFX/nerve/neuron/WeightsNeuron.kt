package zkl.scienceFX.nerve.neuron

import zkl.tools.math.MT
import java.util.*


/**
 * 使用加权求和再经过[activate]算出结果的神经元
 */
abstract class WeightsNeuron : ActivationNeuron() {
	class WeightNerve(val weightsNerve: WeightsNeuron, val dataSource: DataSource, var weight: Double = 0.0): Nerve {
		override val data: Double get() = dataSource.data
		/**
		 * 下行的 der
		 */
		override val der: Double get() = weightsNerve.der * weight
	}
	override fun addInput(dataSource: DataSource): Feedback {
		val fiber = WeightNerve(this, dataSource, MT.randomMirror(1.0))
		inputs.add(fiber)
		return fiber
	}
	
	protected var bias:Double = 0.0
	protected val inputs = ArrayList<WeightNerve>()
	override var data:Double = 0.0
	override fun onComputeX() = inputs.sumByDouble { it.weight * it.dataSource.data } + bias
	
	internal var der: Double = 0.0
	override fun onFeedbackDerX(derX: Double, learningRate: Double) {
		der = derX
		
		//调整 bias 和 weights
		val learningDer = der * learningRate
		bias += learningDer * 1.0
		inputs.forEach { weightFiber-> weightFiber.weight += learningDer * weightFiber.data }
	}
}

/**
 * [activate] 是 [Math.tanh] 的神经元
 */
class TanhNeuron : WeightsNeuron(), TanhActivation
/**
 * [activate] 是 Sigmoid 函数的神经元
 */
class SigmoidNeuron : WeightsNeuron(), SigmoidActivation

/**
 * [activate] 是 RELU 的神经元
 */
class RELUNeuron : WeightsNeuron(), RELUActivation
/**
 * [activate] 是 RELU2 的神经元
 */
class RELU2Neuron(override val k1: Double=0.1, override val k2: Double=1.0) : WeightsNeuron(), RELU2Activation
/**
 * [activate] 是 LTH 的神经元
 */
class LTHNeron(
	override val kr1: Double = 1.0,
	override val kr2: Double = 0.5,
	override val kb: Double = 0.1,
	override val r: Double = 1.0
) : WeightsNeuron(), LTHActivation

class LinearNeuron(override var k: Double = 1.0) : WeightsNeuron(), LinearActivation
class AverageNeuron : WeightsNeuron(), LinearActivation {
	override val k: Double get() = 1.0/inputs.size
}


