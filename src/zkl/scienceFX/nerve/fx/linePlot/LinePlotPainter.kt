package zkl.scienceFX.nerve.fx.linePlot

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import zkl.scienceFX.nerve.fx.Painter
import zkl.tools.math.Point2D

class LinePlotPainter: Painter<Canvas, LinePlotLogic>(){
	override fun onInitialized() {
		source.constructNetwork()
	}
	override fun paint() {
		canvas.graphicsContext2D.run {
			paintBackground()
			
			save()
			val squareWidth = Math.min(canvas.width, canvas.height) * 0.8 / 2.0
			translate(canvas.width / 2.0, canvas.height / 2.0)
			
			paintPlot(squareWidth)
			restore()
		}
	}
	
	fun GraphicsContext.paintBackground() {
		fill = Color.LIGHTGRAY
		fillRect(0.0, 0.0, canvas.width, canvas.height)
	}
	fun GraphicsContext.paintPlot(squareScale: Double) {
		lineWidth = 2.0
		
		stroke= Color.BLACK
		strokeRect(-squareScale, -squareScale, squareScale*2, squareScale*2)
		
		stroke= Color.DARKGRAY
		drawLine(source.getExpectedPlot(), squareScale)
		
		lineWidth = 3.0
		stroke= Color.DODGERBLUE
		drawLine(source.getComputedPlot(), squareScale)
	}
	fun GraphicsContext.drawLine(points: Iterable<Point2D>, scale:Double ) {
		beginPath()
		points.iterator().run {
			if(!hasNext()) return
			next().run { moveTo(x*scale,y*scale) }
			this.forEach { lineTo(it.x*scale,it.y*scale) }
		}
		stroke()
	}
	
	override fun onRelease() {}
}