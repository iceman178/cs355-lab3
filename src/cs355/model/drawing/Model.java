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
	
	private Shape.type currentShape;
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
		currentShape = Shape.type.NONE;
		curShapeIndex = -1;
	}
	
	public void notifyObservers() 
	{
		super.notifyObservers();
	}
	
	
	//----------------------------SHAPE FUNCTIONS---------------------------
	
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
	
	
	//------------------------ADD AND DELETE-----------------------------

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
	
	//------------------------------MOVING-------------------------------
	
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
			
			if(s.getShapeType() != Shape.type.LINE) {
				// changes the coordinates from view->world->object
				AffineTransform viewToObject = Controller.instance().viewToObject(s);
				viewToObject.transform(ptCopy, ptCopy);
			}
//			else {
//				// changes the coordinates from view->world
//				AffineTransform viewToWorld = Controller.instance().viewToWorld();
//				viewToWorld.transform(ptCopy, ptCopy);
//			}
			if(s.pointInShape(ptCopy, tolerance)) {
				curShapeIndex = a;
				selectedColor = s.getColor();
				GUIFunctions.changeSelectedColor(selectedColor);
				changeMade();
				return curShapeIndex;
			}
		}
		curShapeIndex = -1;
		changeMade();
		return curShapeIndex;
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

	public Shape.type getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(Shape.type currentShape) {
		this.currentShape = currentShape;
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