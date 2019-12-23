package edu.hbuas.netdisk.utils;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class RectUtils
{
	public static Rectangle2D create(double w, double h)
	{
		return new Rectangle2D(0,0, w, h);
	}
	
	public static boolean contains(Rectangle2D r, double px, double py)
	{
		return px>= r.getMinX() && px<= r.getMaxX()
				&& py>= r.getMinY() && py>= r.getMaxY();
	}
	
	/////////////////////////////////////////////
	public static double centerX(Rectangle2D r)
	{
		return  r.getMinX() + r.getWidth()/2;
	}
	public static double centerY(Rectangle2D r)
	{
		return  r.getMinY() + r.getHeight()/2;
	}
	public static Point2D center(Rectangle2D r)
	{
		return new Point2D ( centerX(r),  centerY(r));
	}
	
	/////////////////////////////////////////////
	public static Rectangle2D moveBy(Rectangle2D r, double dx, double dy)
	{
		return new Rectangle2D(r.getMinX()+dx, r.getMinY()+dy, r.getWidth(),r.getHeight());
	}
	
	public static Rectangle2D moveTo(Rectangle2D r, double x, double y)
	{
		return new Rectangle2D(x, y, r.getWidth(),r.getHeight());
	}
	
	public static Rectangle2D moveCenter(Rectangle2D r, double x, double y)
	{
		return new Rectangle2D(x-r.getWidth()/2, y-r.getHeight()/2, r.getWidth(),r.getHeight());
	}
	
	/////////////////////////////////////////////
	
	public static Rectangle2D inflate(Rectangle2D r, double d)
	{
		double dx =d, dy=d;
		return inflate(r, dx, dy);
	}
	
	public static Rectangle2D inflate(Rectangle2D r, double dx, double dy)
	{
		return new Rectangle2D(r.getMinX()-dx, r.getMinY()-dy, r.getWidth()+dx+dx, r.getHeight()+dy+dy);
	}
	
	public static Rectangle2D deflate(Rectangle2D r, double d)
	{
		double dx =d, dy=d;
		return deflate(r, dx, dy);
	}
	
	public static Rectangle2D deflate(Rectangle2D r, double dx, double dy)
	{
		return new Rectangle2D(r.getMinX()+dx, r.getMinY()+dy, r.getWidth()-dx-dx, r.getHeight()-dy-dy);
	}
	
	public static Rectangle2D adjust(Rectangle2D r, double left, double top, double right, double bottom)
	{
		return new Rectangle2D(r.getMinX()+left, r.getMinY()+top, r.getWidth() -left + right, r.getHeight() -top + bottom);
	}
	
	public static Rectangle2D deflate(Rectangle2D r, Insets insets)
	{
		return adjust(r, insets.getLeft(), insets.getTop(), insets.getRight(),insets.getBottom());
	}

}
