package zkl.scienceFX.nerve.neuron

import java.util.*

/**
 * 用于描述各路输出一致的一种神经元
 */
abstract class DispatcherNeuron : Neuron, DataSource {
	override fun getDataSource(callback: (DataSource) -> Feedback): DataSource {
		return this.apply { feedbacks.add(callback(this)) }
	}
	
	override var data:Double = 0.0
	override fun process() {
		data = onProcess()
	}
	abstract fun onProcess():Double
	
	val feedbacks = ArrayList<Feedback>()
	override fun feedback(learningRate: Double) {
		onFeedback(feedbacks.sumByDouble { it.der } / feedbacks.size, learningRate)
	}
	abstract fun onFeedback(feedback: Double, learningRate: Double)
}