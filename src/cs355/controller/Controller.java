package cs355.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.controller.IControllerState.stateType;
import cs355.model.drawing.*;
import cs355.solution.CS355;

public class Controller implements CS355Controller {

	private static Controller _instance;
	
	public static final double ZOOMIN  = 2.0;
	public static final double ZOOMOUT = 0.5;
	public static final double ZOOMMIN = 0.25;
	public static final double ZOOMMAX = 4.0;
	
	private double zoom;
	private double scrollerSize;
	private boolean updating;
	private Point2D.Double viewCenter;
	private IControllerState state;
	
	
	public static Controller instance() 
	{
		if (_instance == null) {
			_instance = new Controller();
		}
		return _instance;
	}

	private Controller() 
	{
		this.zoom = 1.0;
		this.scrollerSize = 512;
		this.updating = false;
		this.viewCenter = new Point2D.Double(0, 0);
		this.state = new ControllerNothingState();
	}
	
	
	//----------------------------MOUSE EVENTS-------------------------------------
	
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		// Do nothing
	}
	
	@Override
	public void mousePressed(MouseEvent arg0)
	{
		state.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		state.mouseReleased(arg0);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		state.mouseDragged(arg0);
	}
	
	
	//-------------------------------BUTTON PRESSED-------------------------------------
	
	@Override
	public void colorButtonHit(Color c) 
	{
		if(c == null)
		{
			return;
		}
		if(Model.instance().getCurShapeIndex() != -1)
		{
			Model.instance().setSelectedColor(c);
			Model.instance().updateColor(c);
			GUIFunctions.changeSelectedColor(c);
		}
		else
		{
			Model.instance().setSelectedColor(c);
			GUIFunctions.changeSelectedColor(c);
		}
		Model.instance().setCurShapeIndex(-1);
		GUIFunctions.refresh();
	}

	@Override
	public void lineButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.LINE);
		this.state = new ControllerDrawingState();
	}

	@Override
	public void squareButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.SQUARE);
		this.state = new ControllerDrawingState();
	}

	@Override
	public void rectangleButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.RECTANGLE);
		this.state = new ControllerDrawingState();
	}

	@Override
	public void circleButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.CIRCLE);
		this.state = new ControllerDrawingState();
	}

	@Override
	public void ellipseButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.ELLIPSE);
		this.state = new ControllerDrawingState();
	}

	public void triangleButtonHit() 
	{
		
		Model.instance().setCurrentShape(Shape.type.TRIANGLE);
		this.state = new ControllerDrawingState();
	}

	@Override
	public void selectButtonHit() 
	{
		this.state = new ControllerSelectState();
	}

	
	//---------------------------------ZOOM FUNCTIONS------------------------------
	
	@Override
	public void zoomInButtonHit() 
	{
		this.setZoom(ZOOMIN);
	}

	@Override
	public void zoomOutButtonHit() 
	{
		this.setZoom(ZOOMOUT);
	}

	public void setZoom(Double zoomAdjustment)
	{
		// Find screen width before zoom changes
		int prevWidth = (int) (CS355.SCROLLSTART/zoom);
		
		zoom = zoom * zoomAdjustment;
		// Checks for max and min
		if(zoom < ZOOMMIN) {
			zoom = ZOOMMIN;
		}
		if(zoom > ZOOMMAX) {
			zoom = ZOOMMAX;
		}
		
		scrollerSize = (CS355.SCROLLSTART/zoom);

		// Calculates the new top left of the view
		Point2D.Double newTopLeft = new Point2D.Double(this.viewCenter.x - this.scrollerSize/2, this.viewCenter.y - this.scrollerSize/2);
		if(newTopLeft.x < 0) newTopLeft.x = 0;
		if(newTopLeft.y < 0) newTopLeft.y = 0;
		if(newTopLeft.x + this.scrollerSize > CS355.SCREENSIZE) newTopLeft.x = CS355.SCREENSIZE - this.scrollerSize;
		if(newTopLeft.y + this.scrollerSize > CS355.SCREENSIZE) newTopLeft.y = CS355.SCREENSIZE - this.scrollerSize;
		
		this.updating = true;
		
		// Change scroll bar sizes and positions
		if(prevWidth == CS355.SCREENSIZE) 
		{
			GUIFunctions.setHScrollBarKnob((int) this.scrollerSize);
			GUIFunctions.setVScrollBarKnob((int) this.scrollerSize);
		}
		GUIFunctions.setHScrollBarPosit((int) newTopLeft.x);
		GUIFunctions.setVScrollBarPosit((int) newTopLeft.y);
		GUIFunctions.setHScrollBarKnob((int) this.scrollerSize);
		GUIFunctions.setVScrollBarKnob((int) this.scrollerSize);

		GUIFunctions.setZoomText(zoom);

		Model.instance().changeMade();
		GUIFunctions.refresh();
		this.updating = false;
		
	}
	
		
	//---------------------------SCROLL BAR-----------------------------------
	
	@Override
	public void hScrollbarChanged(int value) 
	{
		this.viewCenter.x = value + this.scrollerSize / 2.0;
		if(!this.updating)
		{
			Model.instance().changeMade();
			GUIFunctions.refresh();
		}
	}

	@Override
	public void vScrollbarChanged(int value) 
	{
		this.viewCenter.y = value + this.scrollerSize / 2.0;
		if(!this.updating)
		{
			Model.instance().changeMade();
			GUIFunctions.refresh();
		}
	}


	//---------------------------MENU OPTIONS-----------------------------
	
	@Override
	public void saveDrawing(File file) 
	{
		Model.instance().save(file);
		GUIFunctions.refresh();
	}

	@Override
	public void openDrawing(File file) 
	{
		Model.instance().open(file);
		GUIFunctions.refresh();
	}
	
	@Override
	public void doDeleteShape() 
	{
		if(state.getType() == stateType.SELECT)
		{
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1)
			{
				Model.instance().deleteShape(currentShapeIndex);
				currentShapeIndex = Model.instance().getCurShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doMoveForward() 
	{
		if(state.getType() == stateType.SELECT)
		{
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1)
			{
				Model.instance().moveForward(currentShapeIndex);
				currentShapeIndex = Model.instance().getCurShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doMoveBackward() 
	{
		if(state.getType() == stateType.SELECT)
		{
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1)
			{
				Model.instance().moveBackward(currentShapeIndex);
				currentShapeIndex = Model.instance().getCurShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doSendToFront() 
	{
		if(state.getType() == stateType.SELECT)
		{
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1)
			{
				Model.instance().moveToFront(currentShapeIndex);
				currentShapeIndex = Model.instance().getCurShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doSendtoBack() 
	{
		if(state.getType() == stateType.SELECT)
		{
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1)
			{
				Model.instance().movetoBack(currentShapeIndex);
				currentShapeIndex = Model.instance().getCurShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	
	//-----------------------------TRANSFORM FUNCTIONS----------------------------------------TODO
	
		// AffineTransform af = new AffineTransform(v1,v2,v3,v4,v5,v6)
		// |v1 v3 v5|
		// |v2 v4 v6|
		// |0  0  1 |
		
	public AffineTransform objectToWorld(Shape shape) 
	{
		AffineTransform transform = new AffineTransform();
		//Translation
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, shape.getCenter().getX(), shape.getCenter().getY()));
		//Rotation
		transform.concatenate(new AffineTransform(Math.cos(shape.getRotation()), Math.sin(shape.getRotation()), -Math.sin(shape.getRotation()), Math.cos(shape.getRotation()), 0, 0));
		return transform;
	}
	
	public AffineTransform worldToView() 
	{
		AffineTransform transform = new AffineTransform();
		//Scale
        transform.concatenate(new AffineTransform(zoom, 0, 0, zoom, 0, 0));
        //Translation
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -viewCenter.getX() + 256*(1/zoom), -viewCenter.getY() + 256*(1/zoom)));
		return transform;
	}

	public AffineTransform objectToView(Shape shape) 
	{
		AffineTransform transform = new AffineTransform();
		// World to View
        transform.concatenate(worldToView());
		// Object to World
		transform.concatenate(objectToWorld(shape));
		return transform;
	}
	
	public AffineTransform viewToWorld() 
	{
		AffineTransform transform = new AffineTransform();
		//Translation
        transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -(-viewCenter.getX() + 256*(1/zoom)), -(-viewCenter.getY() + 256*(1/zoom))));
        //Scale
        transform.concatenate(new AffineTransform(1/zoom, 0, 0, 1/zoom, 0, 0)); 
		return transform;
	}
	
	public AffineTransform worldToObject(Shape shape) 
	{
		AffineTransform transform = new AffineTransform();
		//Rotation
		transform.concatenate(new AffineTransform(Math.cos(shape.getRotation()), -Math.sin(shape.getRotation()), Math.sin(shape.getRotation()), Math.cos(shape.getRotation()), 0.0, 0.0));
		//Translation
		transform.concatenate(new AffineTransform(1.0, 0.0, 0.0, 1.0, -shape.getCenter().getX(), -shape.getCenter().getY()));
		return transform;
	}
	
	public AffineTransform viewToObject(Shape shape) 
	{
		AffineTransform transform = new AffineTransform();
		// World to object
		transform.concatenate(worldToObject(shape));
		// View to world
        transform.concatenate(viewToWorld());
		return transform;
	}
	
	public Point2D.Double viewPointToWorldPoint(MouseEvent arg0) 
	{
		int x = arg0.getX();
		int y = arg0.getY();
		Point2D.Double point = new Point2D.Double((double)x, (double)y);
		return viewPointToWorldPoint(point);
	}
	
	public Point2D.Double viewPointToWorldPoint(Point2D.Double point) {
		Point2D.Double pointCopy = new Point2D.Double(point.getX(), point.getY());
		AffineTransform transform = new AffineTransform();
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -(viewCenter.getX() + 256*(1/zoom)), -(viewCenter.getY() + 256*(1/zoom)))); //t
        transform.concatenate(new AffineTransform(1/zoom, 0, 0, 1/zoom, 0, 0));
        transform.transform(pointCopy, pointCopy);
        return pointCopy;
	}
	
	
	// TODO LATER ON
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	
	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void openScene(File file) {}

	@Override
	public void toggle3DModelDisplay() {}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {}

	@Override
	public void openImage(File file) {} 

	@Override
	public void saveImage(File file) {}

	@Override
	public void toggleBackgroundDisplay() {}
	
	@Override
	public void doEdgeDetection() {}

	@Override
	public void doSharpen() {}

	@Override
	public void doMedianBlur() {}

	@Override
	public void doUniformBlur() {}

	@Override
	public void doGrayscale() {}

	@Override
	public void doChangeContrast(int contrastAmountNum) {}
	
	@Override
	public void doChangeBrightness(int brightnessAmountNum) {}
	
	//------------------------GETTERS AND SETTERS---------------------------

	public double getZoom() {
		return zoom;
	}

	public static Controller get_instance() {
		return _instance;
	}

	public static void set_instance(Controller _instance) {
		Controller._instance = _instance;
	}

	
	
	
}