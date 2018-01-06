package zkl.scienceFX.nerve.fx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import zkl.scienceFX.nerve.fx.linePlot.LinePlotController

fun main(args:Array<String>){
	Application.launch(NerveApplication::class.java,*args)
}
class NerveApplication:Application(){
	override fun start(stage: Stage) {
		start_linePlot(stage)
		val args=parameters.raw.run { if (this.size == 0) arrayListOf("") else this }
		
		when(args[0]){
			"linePlot" -> start_linePlot(stage)
			else-> start_linePlot(stage)
		}
	}
	
	fun start_linePlot(stage: Stage){
		val resource = LinePlotController::class.java.getResource("LinePlot.fxml")
		val root = FXMLLoader.load<Pane>(resource)
		stage.title = "linePlot"
		stage.scene = Scene(root, root.prefWidth, root.prefHeight)
		stage.show()
	}
}
