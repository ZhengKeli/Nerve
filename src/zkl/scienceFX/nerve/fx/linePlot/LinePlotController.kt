package zkl.scienceFX.nerve.fx.linePlot

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.stage.Window
import zkl.scienceFX.nerve.fx.Painter
import zkl.tools.math.MT
import kotlin.concurrent.thread

class LinePlotController {
	//initialization
	@FXML fun initialize(){
		painter = LinePlotPainter()
		logic = LinePlotLogic()
		painter.initialize(canvas,logic)
		Platform.runLater { paint() }
	}
	
	//stage
	private val stage: Window? get() = root.scene.window
	@FXML private lateinit var root: Pane
	
	//controls
	@FXML lateinit var b_start: Button
	val playing: Boolean
		get() = playThread != null
	var playThread: Thread? = null
		@Synchronized get
		@Synchronized set
	@FXML @Synchronized fun onStartButtonClicked(){
		if (playing) stopPlay()
		else startPlay()
	}
	@Synchronized fun startPlay(){
		playThread = thread {
			Platform.runLater { b_start.text = "Pause" }
			while (stage?.isShowing == true && playing) {
				process()
				Platform.runLater { paint() }
				Thread.sleep(50)
			}
		}
	}
	@Synchronized fun stopPlay(){
		val theThread=playThread
		playThread = null
		theThread?.join()
		b_start.text = "Start"
	}
	
	//paint
	lateinit var canvas: Canvas
	lateinit var painter: Painter<Canvas, LinePlotLogic>
	fun paint(){
		painter.paint()
	}
	
	//logic
	lateinit var logic: LinePlotLogic
	fun process(){
		for (i in 1..800) {
			logic.train(Array(3) { MT.randomMirror(0.9) })
		}
	}
	
}