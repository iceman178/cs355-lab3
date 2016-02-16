package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs355.GUIFunctions;
import cs355.controller.Controller;

public class Model extends CS355Drawing {

	//Use a singleton so that the model can be accessed by the view when repainting
	private static Model _instance;
	
	private Shape.type currentMode;
	private Color selectedColor;
	private ArrayList<Shape> shapes;
	private int curShapeIndex;
	
	//If the model had not been initialized, it will be.
	public static Model instance() 
	{
		if (_instance == null) 
		{
			_instance = new Model();
		}
		return _instance;
	}
	
	public Model() 
	{
		selectedColor = Color.WHITE;
		shapes = new ArrayList<Shape>();
		currentMode = Shape.type.NONE;
		curShapeIndex = -1;
	}
	
	public Shape getLastShape() 
	{
		return shapes.get(shapes.size() - 1);
	}
	
	public void updateLastShape(Shape newShape) 
	{
		shapes.remove(shapes.size() - 1);
		shapes.add(newShape);
	}
	
	public void updateColor(Color c)
	{
		shapes.get(curShapeIndex).setColor(c);		
	}
	
	public void updateShapeByIndex(int index, Shape newShape) 
	{
		shapes.remove(index);
		shapes.add(index, newShape);
	}

	@Override
	public Shape getShape(int index) 
	{
		return shapes.get(index);
	}

	@Override
	public int addShape(Shape s) 
	{
		shapes.add(s);
		return shapes.size();
	}

	@Override
	public void deleteShape(int index) 
	{
		if (index >= shapes.size() || index < 0) 
		{
			return;
		}
		shapes.remove(index);
		curShapeIndex = -1;
	}
	
	// -----------------------Moving---------------------------
	
	@Override
	public void moveToFront(int index) {
		if(index >= shapes.size() || index < 0) 
		{
			return;
		}
		
		Shape curShape = shapes.get(index);
		shapes.remove(index);
		shapes.add(curShape);
		curShapeIndex = shapes.size() - 1;
	}

	@Override
	public void movetoBack(int index) {
		if(index >= shapes.size() || index < 0) 
		{
			return;
		}
		
		Shape curShape = shapes.get(index);
		shapes.remove(index);
		shapes.add(0, curShape);
		curShapeIndex = 0;
	}

	@Override
	public void moveForward(int index) {
		if(index >= shapes.size() || index < 0) 
		{
			return;
		}
		
		Shape curShape = shapes.get(index);
		shapes.remove(index);
		shapes.add(index + 1, curShape);
		curShapeIndex = index + 1;
	}

	@Override
	public void moveBackward(int index) {
		if(index >= shapes.size() || index < 0) 
		{
			return;
		}
		
		Shape curShape = shapes.get(index);
		shapes.remove(index);
		shapes.add(index - 1, curShape);
		curShapeIndex = index - 1;
	}

	public int checkIfSelectedShape(Point2D.Double curClick)
	{
		boolean result = false;
		curShapeIndex = -1;
		double tolerance = 0;
		
		for(int a = shapes.size() - 1; a >= 0; a--)
		{
			Shape s = shapes.get(a);
			Point2D.Double ptCopy = new Point2D.Double(curClick.getX(), curClick.getY());
			
			if (s.getShapeType() != Shape.type.LINE)
			{
				// Changes the coordinates from view->world->object
				AffineTransform viewToObject = Controller.instance().viewToObject(s);
				viewToObject.transform(ptCopy, ptCopy);
			}
			else
			{
				// Changes the coordinates from view->world
				AffineTransform viewToWorld = Controller.instance().viewToWorld();
				viewToWorld.transform(ptCopy, ptCopy);
			}
			
			if (s.pointInShape(ptCopy, tolerance))
			{
				curShapeIndex = a;
				selectedColor = s.getColor();
				GUIFunctions.changeSelectedColor(selectedColor);
				changeMade();
				return curShapeIndex;
			}
		}
		changeMade();
		return curShapeIndex;
	}
	
	public boolean mousePressedInRotHandle(Point2D.Double pt, double tolerance)
	{
		if(curShapeIndex == -1) 
		{
			return false;
		}
		
		// Gets currently selected shape
		Shape shape = shapes.get(curShapeIndex);
		double height = -1;
		switch(shape.getShapeType())
		{
			case ELLIPSE:
				height = ((Ellipse)shape).getHeight();
				break;
			case RECTANGLE:
				height = ((Rectangle)shape).getHeight();
				break;
			case CIRCLE:
				height = 2*((Circle)shape).getRadius();
				break;
			case SQUARE:
				height = ((Square)shape).getSize();
				break;
			default:
				break;
		}
		if(height != -1)
		{
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());
			// changes the coordinates from view->world->object
			AffineTransform viewToObj = Controller.instance().viewToObject(shape);
			viewToObj.transform(ptCopy, ptCopy);
			double yDiff = ptCopy.getY()+((height/2) + 9);
			
			double distance = Math.sqrt(Math.pow(ptCopy.getX(), 2) + Math.pow(yDiff, 2));
			return (6>=distance);
		}
		if(shape.getShapeType().equals(Shape.type.TRIANGLE))
		{
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());
			// changes the coordinates from view->world->object
			AffineTransform viewToObj = Controller.instance().viewToObject(shape);
			viewToObj.transform(ptCopy, ptCopy); //transform pt to object coordinates
			
			Triangle triangle = (Triangle)shape;
			double ax = triangle.getA().getX()-triangle.getCenter().getX();
			double bx = triangle.getB().getX()-triangle.getCenter().getX();
			double cx = triangle.getC().getX()-triangle.getCenter().getX();
			
			double ay = triangle.getA().getY()-triangle.getCenter().getY();
			double by = triangle.getB().getY()-triangle.getCenter().getY();
			double cy = triangle.getC().getY()-triangle.getCenter().getY();
			
			double distance = 7;
			if(ay <= by && ay <= cy)
			{
				distance = Math.sqrt(Math.pow(ax-ptCopy.getX(), 2) + Math.pow(ay-ptCopy.getY()-9, 2));
			}
			else if(by <= ay && by <= cy)
			{
				distance = Math.sqrt(Math.pow(bx-ptCopy.getX(), 2) + Math.pow(by-ptCopy.getY()-9, 2));
			}
			else if(cy <= by && cy <= ay)
			{
				distance = Math.sqrt(Math.pow(cx-ptCopy.getX(), 2) + Math.pow(cy-ptCopy.getY()-9, 2));
			}
			return (6>=distance); 
		}
		return false;
	}

	public void changeMade()
	{
		setChanged();
		notifyObservers();
	}
	
	
	
	//------------------GETTERS AND SETTERS---------------------------

	@Override
	public List<Shape> getShapesReversed() {
		ArrayList<Shape> backwards = new ArrayList<Shape>(shapes);
		Collections.reverse(backwards);
		return backwards;
	}
	
	@Override
	public List<Shape> getShapes() {
		return shapes;
	}
	@Override
	public void setShapes(List<Shape> shapes) {
		this.shapes = (ArrayList<Shape>) shapes;
	}

	public static Model get_instance() {
		return _instance;
	}

	public static void set_instance(Model _instance) {
		Model._instance = _instance;
	}

	public Shape.type getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(Shape.type currentMode) {
		this.currentMode = currentMode;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}
	
	public void setShapes(ArrayList<Shape> shapes) {
		this.shapes = shapes;
	}

	public int getCurShapeIndex() {
		return curShapeIndex;
	}

	public void setCurShapeIndex(int curShapeIndex) {
		this.curShapeIndex = curShapeIndex;
	}
	
	
	
	
	
}