package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Add your triangle code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Triangle extends Shape 
{

	// The three points of the triangle.
	private Point2D.Double a;
	private Point2D.Double b;
	private Point2D.Double c;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param center the center of the new shape.
	 * @param a the first point, relative to the center.
	 * @param b the second point, relative to the center.
	 * @param c the third point, relative to the center.
	 */
	public Triangle(Color color, Point2D.Double center, 
					Point2D.Double a, Point2D.Double b, Point2D.Double c)
	{

		// Initialize the superclass.
		super(color, center);
		this.setShapeType(Shape.type.TRIANGLE);
		// Set fields.
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * Getter for the first point.
	 * @return the first point as a Java point.
	 */
	public Point2D.Double getA() {
		return a;
	}

	/**
	 * Setter for the first point.
	 * @param a the new first point.
	 */
	public void setA(Point2D.Double a) {
		this.a = a;
	}

	/**
	 * Getter for the second point.
	 * @return the second point as a Java point.
	 */
	public Point2D.Double getB() {
		return b;
	}

	/**
	 * Setter for the second point.
	 * @param b the new second point.
	 */
	public void setB(Point2D.Double b) {
		this.b = b;
	}

	/**
	 * Getter for the third point.
	 * @return the third point as a Java point.
	 */
	public Point2D.Double getC() {
		return c;
	}

	/**
	 * Setter for the third point.
	 * @param c the new third point.
	 */
	public void setC(Point2D.Double c) {
		this.c = c;
	}

	/**
	 * Add your code to do an intersection test
	 * here. You shouldn't need the tolerance.
	 * @param pt = the point to test against.
	 * @param tolerance = the allowable tolerance.
	 * @return true if pt is in the shape,
	 *		   false otherwise.
	 */
	@Override
	public boolean pointInShape(Point2D.Double pt, double tolerance) 
	{
		boolean result = false;
		
		double ax = a.getX() - center.getX();
		double bx = b.getX() - center.getX();
		double cx = c.getX() - center.getX();
		double x = pt.getX();
		
		double ay = a.getY() - center.getY();
		double by = b.getY() - center.getY();
		double cy = c.getY() - center.getY();
		double y = pt.getY();
		
		double area = Math.abs((ax*(by-cy) + bx*(cy-ay) + cx*(ay-by))/2);
		
		double area1 = Math.abs((x*(by-cy) + bx*(cy-y) + cx*(y-by))/2);
		double area2 = Math.abs((ax*(y-cy) + x*(cy-ay) + cx*(ay-y))/2);
		double area3 = Math.abs((ax*(by-y) + bx*(y-ay) + x*(ay-by))/2);
		
		if ((area1+area2+area3) == area)
		{
			result = true;
		}
		return result;
	}

}







