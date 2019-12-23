package edu.hbuas.netdisk.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LayoutUtils
{
	public static void resizeRelocate(Node node ,Rectangle2D r)
	{
		node.resizeRelocate(r.getMinX(),r.getMinY(),r.getWidth(),r.getHeight());
	}
	
	public static double[] layout (double size, String[] sss)
	{
		ArrayList<String> a = new ArrayList<String>();
		for(String s: sss) a.add( s);
		return layout(size, a);
	}
	
	public static double[] layout (double size, List<String> sss)
	{
		// 去除空项
		Iterator<String> iter = sss.iterator();
		while(iter.hasNext())
		{
			String s = iter.next();
			if(s.trim().length() == 0) iter.remove();
		}
		
		int count = sss.size();
		double used = 0;
		double weight = 0;
		
		double[] _aaa = new double[count];
		int[] _ppp = new int[count];
		
		for(int i = 0; i < count; i++)
		{
			String s = sss.get(i);
			if(used >= size) continue;

			double aa = 0;
			int pp = 0;
			
			if(s.endsWith("px")) {
				aa = Integer.valueOf( s.substring(0, s.length()-2));
			} 
			else if(s.endsWith("%")) {
				int percent = Integer.valueOf( s.substring(0, s.length()-1));
				aa = size * percent / 100;
			}
			else {
				pp = Integer.valueOf( s);
			}
			
			// 不能超出
			if(used + aa > size) aa = size - used;

			_aaa[i] = aa;
			_ppp[i] = pp;
			used += aa;
			weight += pp;
		}
		
		// 按权重分配剩余空间
		double remaining = size - used;
		if(remaining > 0 & weight > 0)
		{
			double unit = remaining / weight;
			for(int i = 0; i < count; i++)
			{
				double pp = _ppp[i];
				if(pp > 0) 
				{
					_aaa[i] = unit * pp;
				}
			}
		}

		return _aaa;
	}
		
	public static Rectangle2D[] layoutCols(Rectangle2D rect,String sss)
	{
		double[] aaa = layout(rect.getWidth(), sss.split(" "));
		double x = rect.getMinX();
		double y = rect.getMinX();
		
		Rectangle2D[] results = new Rectangle2D[aaa.length];
		for(int i=0; i<aaa.length; i++)
		{			
			results[i] = new Rectangle2D(x, y, aaa[i], rect.getHeight());
			x += aaa[i];				
		}
		
		return results;	
	}
}
