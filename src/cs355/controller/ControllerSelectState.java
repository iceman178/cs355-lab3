package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cs355.GUIFunctions;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Triangle;

public class ControllerSelectState implements IControllerState
{

	private boolean rotating;
	private int currentShapeIndex;
	private Point2D.Double mouseDragStart;
	
	

	public ControllerSelectState()
	{
		this.rotating = false;
		this.currentShapeIndex = -1;
		this.mouseDragStart = null;
	}
	
	// DONE
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		boolean result = false;
		result = Model.instance().mousePressedInRotHandle(new Point2D.Double(arg0.getX(), arg0.getY()), 5);
		if (result)
		{
			rotating = true;
		}
		else
		{
			currentShapeIndex = Model.instance().checkIfSelectedShape(new Point2D.Double(arg0.getX(), arg0.getY()));
			if (currentShapeIndex != -1)
			{
				mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
				AffineTransform viewToWorld = Controller.instance().viewToWorld();
				viewToWorld.transform(this.mouseDragStart, this.mouseDragStart);
			}
		}
	}

	// DONE
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		if(currentShapeIndex != -1)
		{
			rotating = false;
			this.mouseDragStart = null;
		}
	}

	// DONE
	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		if(currentShapeIndex != -1)
		{
			Point2D.Double movingPoint = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(movingPoint, movingPoint);
			
			if(rotating) 
			{
				rotateShape(currentShapeIndex, arg0);
			}
			else 
			{
				Shape.type shapeType = Model.instance().getShape(currentShapeIndex).getShapeType();
				
				switch(shapeType) 
				{
				case LINE:
					this.handleLineTransformation(arg0);
					break;
				case SQUARE:
				case RECTANGLE:
				case CIRCLE:
				case ELLIPSE:
					this.handleShapeTransformation(arg0);
					break;
				case TRIANGLE:
					this.handleTriangleTransformation(arg0);
					break;
				case NONE:
					break;
				default:
					break;
				}
			}
			GUIFunctions.refresh();
		}
	}

	@Override
	public stateType getType()
	{
		return stateType.SELECT;
	}
	
	
	//-----------------------HANDLER FUNCTIONS------------------------------------------
	
	private double calcAvg(double num1, double num2, double num3) 
	{
		double avg = (num1 + num2 + num3) / 3;
		return avg;
	}
	
	public void handleLineTransformation(MouseEvent arg0) 
	{
		Line line = (Line) Model.instance().getShape(currentShapeIndex);
		
		if(line.pointNearCenter(new Point2D.Double(arg0.getX(), arg0.getY()), 10)) 
		{
			line.setCenter(new Point2D.Double(arg0.getX(), arg0.getY()));
		}
		else if(line.pointNearEnd(new Point2D.Double(arg0.getX(), arg0.getY()), 10)) 
		{
			line.setEnd(new Point2D.Double(arg0.getX(), arg0.getY()));
		}
		else 
		{
			double changeX = arg0.getX() - mouseDragStart.getX();
			double changeY = arg0.getY() - mouseDragStart.getY();
			
			Point2D.Double center = line.getCenter();
			Point2D.Double end = line.getEnd();

			double trueCenterX = (center.x + end.x) / 2;
			double trueCenterY = (center.y + end.y) / 2;
			
			double centerXdelta = line.getCenter().getX() - trueCenterX;
			double endXdelta = line.getEnd().getX() - trueCenterX;
			double centerYdelta = line.getCenter().getY() - trueCenterY;
			double endYdelta = line.getEnd().getY() - trueCenterY;
			
			
			line.setCenter(new Point2D.Double(mouseDragStart.x + changeX + centerXdelta, mouseDragStart.y + changeY + centerYdelta));
			line.setEnd(new Point2D.Double(mouseDragStart.x + changeX + endXdelta, mouseDragStart.y + changeY + endYdelta));
			Model.instance().updateShapeByIndex(currentShapeIndex, line);
		}
	}

	public void handleShapeTransformation(MouseEvent arg0) 
	{
		Shape shape = Model.instance().getShape(currentShapeIndex);
		double changeX = arg0.getX() - mouseDragStart.getX();
		double changeY = arg0.getY() - mouseDragStart.getY();
		shape.setCenter(new Point2D.Double(mouseDragStart.x + changeX, mouseDragStart.y + changeY));
		Model.instance().updateShapeByIndex(currentShapeIndex, shape);
	}
	 
	public void handleTriangleTransformation(MouseEvent arg0) 
	{
		Triangle triangle = (Triangle) Model.instance().getShape(currentShapeIndex);
		double changeX = arg0.getX() - mouseDragStart.getX();
		double changeY = arg0.getY() - mouseDragStart.getY();
		
		double aXdelta = triangle.getA().x - triangle.getCenter().x;
		double bXdelta = triangle.getB().x - triangle.getCenter().x;
		double cXdelta = triangle.getC().x - triangle.getCenter().x;
		double aYdelta = triangle.getA().y - triangle.getCenter().y;
		double bYdelta = triangle.getB().y - triangle.getCenter().y;
		double cYdelta = triangle.getC().y - triangle.getCenter().y;
		
		Point2D.Double updatedA = new Point2D.Double(mouseDragStart.x + changeX + aXdelta, mouseDragStart.y + changeY + aYdelta);
		Point2D.Double updatedB = new Point2D.Double(mouseDragStart.x + changeX + bXdelta, mouseDragStart.y + changeY + bYdelta);
		Point2D.Double updatedC = new Point2D.Double(mouseDragStart.x + changeX + cXdelta, mouseDragStart.y + changeY + cYdelta);

		triangle.setA(updatedA);
		triangle.setB(updatedB);
		triangle.setC(updatedC);
		
		double centerX = calcAvg(triangle.getA().getX(), triangle.getB().getX(), triangle.getC().getX());
		double centerY = calcAvg(triangle.getA().getY(), triangle.getB().getY(), triangle.getC().getY());
		
		Point2D.Double triCenter = new Point2D.Double(centerX, centerY);
		triangle.setCenter(triCenter);
		
		Model.instance().updateShapeByIndex(currentShapeIndex, triangle);
	}

	public void rotateShape(int shapeIndex, MouseEvent arg0)
	{
		Shape shape = Model.instance().getShape(shapeIndex);
		double deltaX = (shape.getCenter().getX() - arg0.getX())/Controller.instance().getZoom();
		double deltaY = (shape.getCenter().getY() - arg0.getY())/Controller.instance().getZoom();
		double angle = Math.atan2(deltaY, deltaX) - Math.PI / 2;
		shape.setRotation(angle % (2*Math.PI));
		GUIFunctions.refresh();
	}
	
	
	//-------------------------GETTERS AND SETTERS------------------------
	
	public boolean isRotating() {
		return rotating;
	}

	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	public int getCurrentShapeIndex() {
		return currentShapeIndex;
	}

	public void setCurrentShapeIndex(int currentShapeIndex) {
		this.currentShapeIndex = currentShapeIndex;
	}

	public Point2D.Double getMouseDragStart() {
		return mouseDragStart;
	}

	public void setMouseDragStart(Point2D.Double mouseDragStart) {
		this.mouseDragStart = mouseDragStart;
	}
	
	
}
