package zkl.scienceFX.nerve.neuron

/**
 * 可以理解为一个普通的数学函数，
 * 但在神经网络中，为了处理反馈数据，还需要它的导函数
 */
interface Activation {
	fun activate(x: Double): Double
	fun derivative(x: Double): Double
}

/**
 * 双曲正切的[Activation]
 * 不要问我为什么用它，因为playground是这么用的
 */
interface TanhActivation : Activation {
	override fun activate(x: Double) = Math.tanh(x)
	override fun derivative(x: Double) = activate(x).let { y -> 1.0 - y * y }
}

/**
 * Sigmoid函数的[Activation]
 */
interface SigmoidActivation : Activation {
	override fun activate(x: Double) = 1.0 / (1.0 + Math.exp(-x))
	override fun derivative(x: Double) = Math.exp(-x) / Math.pow((Math.exp(-x) + 1.0), 2.0)
}

/**
 * 线性变换的[Activation]
 * 就是乘个数
 */
interface LinearActivation : Activation {
	val k: Double get() = 1.0
	override fun activate(x: Double) = k * x
	override fun derivative(x: Double) = k
}

/**
 * RELU 的 [Activation]
 */
interface RELUActivation : Activation {
	override fun activate(x: Double) = if (x > 0) x else 0.0
	override fun derivative(x: Double) = if (x > 0) 1.0 else 0.0
}

/**
 * RELU2 的 [Activation]
 */
interface RELU2Activation : Activation {
	val k1:Double get() = 0.1
	val k2: Double get() = 1.0
	override fun activate(x: Double) = if (x > 0) k2 * x else k1 * x
	override fun derivative(x: Double) = if (x > 0) k2 else k1
}

/**
 * LinearTanh 的 [Activation]
 */
interface LTHActivation : Activation {
	val kr1:Double
	val kr2:Double
	val kb:Double
	val r:Double
	override fun activate(x: Double) = when {
		x > r -> r * kr1 + kb * (x - r)
		x < -r -> -r * kr2 + kb * (x + r)
		x in 0.0..r -> kr1 * x
		x in -r..0.0 -> kr2 * x
		else -> 0.0
	}
	override fun derivative(x: Double) = when {
		x in 0.0..r -> kr1
		x in -r..0.0 -> kr2
		else -> kb
	}
}

/**
 * 一种输出结果会经过[Activation]函数处理后再输出的 [DispatcherNeuron]，
 * 这个[Activation]可以用来做标准化、异变等，
 * 经验证明有个这东西挺好的喔
 */
abstract class ActivationNeuron : DispatcherNeuron(), Activation {
	var x: Double = 0.0
	override fun onProcess(): Double {
		x = onComputeX()
		return activate(x)
	}
	
	abstract fun onComputeX(): Double
	
	override fun onFeedback(feedback: Double, learningRate: Double) {
		onFeedbackDerX(feedback * derivative(x), learningRate)
	}
	
	abstract fun onFeedbackDerX(derX: Double, learningRate: Double)
}

