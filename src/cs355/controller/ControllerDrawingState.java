package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import cs355.GUIFunctions;
import cs355.controller.IControllerState.stateType;
import cs355.model.drawing.Circle;
import cs355.model.drawing.Ellipse;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Square;
import cs355.model.drawing.Triangle;

public class ControllerDrawingState implements IControllerState
{
	private Point2D.Double mouseDragStart;
	private ArrayList<Point2D> trianglePoints;
	
	public ControllerDrawingState() 
	{
		this.mouseDragStart = null;
		this.trianglePoints = new ArrayList<>();
	}
	
	private double calcAvg(double num1, double num2, double num3) 
	{
		double avg = (num1 + num2 + num3) / 3;
		return avg;
	}
	
	// DONE
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		if (Model.instance().getCurrentShape() == Shape.type.TRIANGLE)
		{
			Point2D.Double newPoint = new Point2D.Double(arg0.getX(), arg0.getY());
			trianglePoints.add(newPoint);
			
			if (trianglePoints.size() == 3)
			{
				Point2D.Double p1 = new Point2D.Double(trianglePoints.get(0).getX(), trianglePoints.get(0).getY());
				Point2D.Double p2 = new Point2D.Double(trianglePoints.get(1).getX(), trianglePoints.get(1).getY());
				Point2D.Double p3 = new Point2D.Double(trianglePoints.get(2).getX(), trianglePoints.get(2).getY());
				
				double centerX = calcAvg(p1.getX(), p2.getX(), p3.getX());
				double centerY = calcAvg(p1.getY(), p2.getY(), p3.getY());
				
				Point2D.Double triCenter = new Point2D.Double(centerX, centerY);
				
				Triangle triangle = new Triangle(Model.instance().getSelectedColor(), triCenter, p1, p2, p3);
				Model.instance().addShape(triangle);
				//resetCurMode();;
				GUIFunctions.refresh();
			}
		}
		else
		{
			this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(this.mouseDragStart, this.mouseDragStart);
			
			switch(Model.instance().getCurrentShape())
			{
			case LINE:
				Point2D.Double start_line = new Point2D.Double(arg0.getX(), arg0.getY());		
				Point2D.Double end_line = new Point2D.Double(arg0.getX(), arg0.getY());
				Line line = new Line(Model.instance().getSelectedColor(), start_line, end_line);
				Model.instance().addShape(line);
				break;
			case CIRCLE:
				Point2D.Double origin_circle = new Point2D.Double(arg0.getX(), arg0.getY());
				Point2D.Double center_circle = new Point2D.Double(arg0.getX(), arg0.getY());
				Circle circle = new Circle(Model.instance().getSelectedColor(), center_circle, origin_circle, 0);
				Model.instance().addShape(circle);
				break;
			case ELLIPSE:
				Point2D.Double origin_ellipse = new Point2D.Double(arg0.getX(), arg0.getY());
				Point2D.Double center_ellipse = new Point2D.Double(arg0.getX(), arg0.getY());
				Ellipse ellipse = new Ellipse(Model.instance().getSelectedColor(), center_ellipse, origin_ellipse, 0, 0);
				Model.instance().addShape(ellipse);
				break;
			case RECTANGLE:
				Point2D.Double origin_rectangle = new Point2D.Double(arg0.getX(), arg0.getY());
				Point2D.Double center_rectangle = new Point2D.Double(arg0.getX(), arg0.getY());
				Rectangle rectangle = new Rectangle(Model.instance().getSelectedColor(), center_rectangle, origin_rectangle, 0, 0);
				Model.instance().addShape(rectangle);
				break;
			case SQUARE:
				Point2D.Double origin_square = new Point2D.Double(arg0.getX(), arg0.getY());
				Point2D.Double center_square = new Point2D.Double(arg0.getX(), arg0.getY());
				Square square = new Square(Model.instance().getSelectedColor(), center_square, origin_square, 0);
				Model.instance().addShape(square);
				break;
			default:
				break;
			}
		}
	}

	// DONE
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		//NOTHING
		
	}

	// DONE
	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		if(Model.instance().getCurrentShape() != Shape.type.TRIANGLE)
		{
			Shape currentShape = Model.instance().getLastShape();
			Point2D.Double movingPoint = new Point2D.Double(arg0.getX(), arg0.getY());
		
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(movingPoint, movingPoint);
			
			switch(currentShape.getShapeType())
			{
			case LINE:
				updateCurrentLine(currentShape, movingPoint);
				break;
			case CIRCLE:
				updateCurrentCircle(currentShape, movingPoint);		
				break;
			case ELLIPSE:
				updateCurrentEllipse(currentShape, movingPoint);
				break;
			case RECTANGLE:
				updateCurrentRectangle(currentShape, movingPoint);
				break;
			case SQUARE:
				updateCurrentSquare(currentShape, movingPoint);
				break;
			case TRIANGLE:	
				break;
			default:
				break;
			}
			GUIFunctions.refresh();
		}
	
	}

	// DONE
	@Override
	public stateType getType() 
	{
		return stateType.DRAWING;
	}

	//-----------------------------SHAPE HANDLERS--------------------------------
	
	private void updateCurrentLine(Shape currentShape, Point2D.Double pt) 
	{
		Line line = (Line) currentShape;
		Point2D.Double end_line = new Point2D.Double(pt.getX(), pt.getY());
		line.setEnd(end_line);
		
		Model.instance().updateLastShape(line);
	}

	private void updateCurrentCircle(Shape currentShape, Point2D.Double pt) 
	{
		Circle circle = (Circle) currentShape;
		
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}
		
		Model.instance().updateLastShape(circle);
	}
	
	private void updateCurrentEllipse(Shape currentShape, Point2D.Double pt) 
	{
		Ellipse ellipse = (Ellipse) currentShape;
		
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}
		
		Model.instance().updateLastShape(ellipse);
	}
		
	private void updateCurrentRectangle(Shape currentShape, Point2D.Double pt) 
	{
		Rectangle rectangle = (Rectangle) currentShape;
		
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}
		else if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}
		
		Model.instance().updateLastShape(rectangle);		
	}

	private void updateCurrentSquare(Shape currentShape, Point2D.Double pt) 
	{
		Square square = (Square) currentShape;
		
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				//change to set center of some sort 
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				square.setSize(newcorner);
			}
		}
		
		Model.instance().updateLastShape(square);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
