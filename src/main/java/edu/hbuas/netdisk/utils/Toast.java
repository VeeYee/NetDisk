package edu.hbuas.netdisk.utils;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Window;

public class Toast
{
	// 警告级别
	public enum Level{ 	INFO, WARN, ERROR  	};
	
	// 背景色
	public Color[] bgColors = {
		Color.GRAY,
		Color.ORANGE,
		Color.RED
	};
	
	// 文字颜色
	public Color[] textColors = {
		Color.web("#ffffff"),
		Color.web("#ffffff"),
		Color.web("#ffffff")
	};
	
	// 初始透明度
	public double initialAlpha = 0.9;
	
	// 显示位置, 支持 TOP_CENTER, CENTER, BOTTOM_CENTER
	public Pos pos = Pos.CENTER;
	
	private Window owner;
	private Node node; // 可以从Node获取Window

	
	public Toast(Window owner)
	{
		this.owner = owner;
	}
	
	public Toast(Node node)
	{
		this.node = node;
	}
	
	/////////////
	
	public void show(String text)
	{
		show(Level.INFO, 1500, text);
	}
	
	public void show(Level level, String text)
	{
		show(level, 1500, text);
	}
	
	public void show(Level level, int timeout, String text)
	{
		ToastrWindow toastr = new ToastrWindow();
		
		// 警告级别与背景色
		if(level == Level.INFO) 
		{
			toastr.bgColor = bgColors[0]; //Color.CHARTREUSE,
			toastr.textColor = textColors[0];
		}
		else if(level == Level.WARN)
		{
			toastr.bgColor = bgColors[1]; //Color.CHARTREUSE,
			toastr.textColor = textColors[1];
		}
		else if(level == Level.ERROR)
		{
			toastr.bgColor = bgColors[2];; //Color.CHARTREUSE,
			toastr.textColor = textColors[2];
		}
		
		toastr.initialAlpha = this.initialAlpha;
		
		// 设置文本
		toastr.setText(text);
			
		
		// 如果指定的是Node，则只有Node被显示的时候才能得到Window
		if(owner == null) owner = node.getScene().getWindow();
		double x = owner.getX(), y = owner.getY();
		double w = owner.getWidth(), h = owner.getHeight();
		
		// 计算窗口显示的位置
		Bounds bounds = toastr.root.getBoundsInLocal();		
		if(pos == Pos.CENTER)
		{
			x += ( w - bounds.getWidth())/2;
			y += ( h - bounds.getHeight())/2;
		}
		else if(pos == Pos.TOP_CENTER)
		{
			x += ( w - bounds.getWidth())/2;
			y += 50;
		}
		else if(pos == Pos.BOTTOM_CENTER)
		{
			x += ( w - bounds.getWidth())/2;
			y += ( h - bounds.getHeight() - 50);
		}
				
		//System.out.println(bounds.toString());
		toastr.show(owner, x, y);
	}
}