package zkl.scienceFX.nerve.neuron


/*
此处是神经网络最基础的部分的相关代码。
此处神经网络结构有着“以信息获取为首”的设计思想
神经元之间的连接以“信息源”为媒介，重视信息的获取
 */


/**
 * 数据源
 * 此处使用 Data 字眼表示神经网络中的正向流通信息
 */
interface DataSource { val data: Double }
class InstantDataSource(override var data: Double=0.0) : DataSource
/**
 * 反馈源
 * 此处使用 Feedback 字眼表示神经网络中的反馈信息
 * （为什么要把正反流通信息源分成两个类？因为反正它们也不怎么碰到一起 ，为了写起来比较直观就这样设计了）
 */
interface Feedback { val der: Double }
class InstantFeedback(override var der:Double=0.0): Feedback
/**
 * 神经纤维？？
 * 双向源的集合，可以方便管理
 * （其实就没啥用）
 */
interface Nerve : DataSource, Feedback
class InstantNerve(dataSource: DataSource?, feedback: Feedback?) : Nerve,
	DataSource by dataSource!!, Feedback by feedback!!

/**
 * 神经元
 * 一个神经元可以有多路输入和多路输出（还有相应的反馈），
 * [getDataSource]，[addInput]，[connectTo]这几个方法用来互相连接，
 * [process]和[feedback]用来处理正反流通数据
 */
interface Neuron {
	fun getDataSource(callback:(DataSource)-> Feedback): DataSource
	fun addInput(dataSource: DataSource): Feedback
	fun connectTo(nextNeuron: Neuron){
		getDataSource { dataSource-> nextNeuron.addInput(dataSource) }
	}
	
	fun process()
	fun feedback(learningRate: Double = 0.05)
}

