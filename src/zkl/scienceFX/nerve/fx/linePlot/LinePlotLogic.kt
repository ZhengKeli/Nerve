package zkl.scienceFX.nerve.fx.linePlot

import zkl.scienceFX.nerve.neuron.DataSource
import zkl.scienceFX.nerve.neuron.InstantDataSource
import zkl.scienceFX.nerve.neuron.InstantFeedback
import zkl.scienceFX.nerve.neuron.Neuron
import zkl.scienceFX.nerve.neuron.LinearNeuron
import zkl.scienceFX.nerve.neuron.TanhNeuron
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.Point2D

class LinePlotLogic {
	var expect: (x: Double) -> Double = { x ->
//		8* (x+0.5)/1.5 * (x+0.5)/1.5 * (x+0.5)/1.5 -8* (x+0.5)/1.5 * (x+0.5)/1.5 + (x+0.5)/1.5
//		-Math.abs(x)
		Math.floor(x / 0.5) / 2.8
//		Math.sin(x/0.1*Math.PI/5.0)
//		-x*x+0.5
//		MT.randomMirror(1.0)
	}
	var learningRate: Double = 0.1
	
	lateinit var network: Array<Array<out Neuron>>
	lateinit var mainInputs: Array<InstantDataSource>
	lateinit var mainOutput: DataSource
	lateinit var mainFeedback: InstantFeedback
	fun constructNetwork() {
		//组建神经网络
		network = arrayOf(
			Array<Neuron>(10) { TanhNeuron() },
			Array<Neuron>(5) { TanhNeuron() },
			Array<Neuron>(1) { TanhNeuron() },
			arrayOf(LinearNeuron(2.0 / 10))
		)
		for (i in 0..network.size - 2) {
			network[i].forEach { sourceNerve ->
				network[i + 1].forEach { feedbackNerve ->
					sourceNerve.connectTo(feedbackNerve)
				}
			}
		}
		
		//组建数据入口并连接到神经网络
		mainInputs = Array<InstantDataSource>(1) { InstantDataSource() }
		mainInputs.forEach { mainInput -> network[0].forEach { nerve -> nerve.addInput(mainInput) } }
		
		//组建数据出口
		mainFeedback = InstantFeedback()
		mainOutput = network.last().last().getDataSource { mainFeedback }
	}
	
	
	//compute
	var trainCount = 0
	
	fun compute(x: Double): Double {
		//forward
		mainInputs[0].data = x
		network.forEach { it.forEach(Neuron::process) }
		val computedY = mainOutput.data
		return computedY
	}
	
	fun train(xSet: Array<Double>) {
		var minDerX = xSet[0]
		var minDerAbs = 100000.0
		for (i in 0 until xSet.size) {
			val computedY = compute(xSet[i])
			val expectedY = expect(xSet[i])
			val der = expectedY - computedY
			val thisDerMax = Math.abs(der)
			
			//取">"表示投机型，取"<"表示求稳型
			if (minDerAbs < thisDerMax) {
				minDerAbs = thisDerMax
				minDerX = xSet[i]
			}
			
		}
		
		val computedY = compute(minDerX)
		val expectedY = expect(minDerX)
		val der = expectedY - computedY
		
		//backPro
		mainFeedback.der = der
		for (i in network.size - 1 downTo 0) {
			network[i].forEach { it.feedback(learningRate) }
		}
		
		trainCount++
	}
	
	
	//plot
	fun getExpectedPlot(from: Double = -1.0, to: Double = 1.0, step: Double = 0.01) = makePlot(from, to, step) { expect(it) }
	
	fun getComputedPlot(from: Double = -1.0, to: Double = 1.0, step: Double = 0.01) = makePlot(from, to, step) { compute(it) }
	inline fun makePlot(from: Double = -1.0, to: Double = 1.0, step: Double = 0.01,
	                    function: (Double) -> Double): List<Point2D> {
		val re = ArrayList<Point2D>(((to - from) / step).toInt() + 1)
		var x = from
		while (x <= to) {
			val y = function(x)
			re.add(InstantPoint2D(x, y))
			x += step
		}
		return re
	}
}

