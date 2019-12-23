package edu.hbuas.netdisk.utils;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

public class ToastrWindow extends Popup
{
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	
	public Color bgColor = Color.web("#0f0");
	public Color textColor = Color.web("444");
	public double initialAlpha = 1.0;
	
	//Group root = new Group();
	LayoutPane root = new LayoutPane();
	AnimationTimerUtils timer;
	
	public ToastrWindow()
	{	
		//root.textNode.setWrappingWidth(240);	
		//textNode.setFill( Color.web("#eee") );
		
		getScene().setRoot( root );
		//setAutoHide(true);
		setHideOnEscape(true);
		setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		
	}
	
	public void setText(String text)
	{
		root.setText(text);
	}
	
	@Override
	public void show(Window ownerWindow, double anchorX, double anchorY)
	{
		super.show(ownerWindow, anchorX, anchorY);
		startTimer();
	}
	
	public void startTimer()
	{
		root.setBgColor(bgColor, initialAlpha);
		root.setTextColor(textColor, initialAlpha);
		
		timer = new AnimationTimerUtils(100) {
			
			int count = 15; // timeout
			
			@Override
			public void updateAnimation()
			{				
				count --;
				
				if(count <0) 
				{
					timer.stop();
					timer = null;
					hide();	
					return;
				}	

				int NN = 7;
				if(count < NN)
				{
					double alpha = (double) count/ NN;
					if(alpha< initialAlpha)
					{
						root.setBgColor(bgColor, alpha);
						root.setTextColor(textColor, alpha);
					}	
				}			
			}			
		};
		timer.start();
	}
	
	////////////////////////////////////
	// 布局管理器
	public class LayoutPane extends Pane
	{
		Canvas bgNode = new Canvas(); // 背景
		Text textNode = new Text(); // 文字
		
		// 布局与绘制参数
		
		public LayoutPane()
		{
			// 注意: 按从底层到上层的顺序添加
			getChildren().addAll(bgNode ,textNode);
			setPadding ( new Insets(8,16,8,16));
			
			textNode.setFont( new Font(14.0));
		}
		
		@Override
		public void resize(double width, double height)
		{
			super.resize(width, height);
			bgNode.setWidth(width);
			bgNode.setHeight(height);
		}
		
		@Override
		protected void layoutChildren()
		{
			double w = getWidth(), h = getHeight();
			if(w<= 0 || h <= 0) return;
			
			Rectangle2D rect = new Rectangle2D(0, 0, w,h);
			
			LayoutUtils.resizeRelocate(bgNode, rect);
			
			Insets insets = getInsets();
			Rectangle2D r2 = RectUtils.deflate(rect, insets);
			LayoutUtils.resizeRelocate(textNode, r2);

		}

		
		@Override
		protected double computePrefWidth(double height)
		{
			Insets insets = getInsets();
			Bounds bounds = textNode.getBoundsInLocal();
			return bounds.getWidth() + insets.getLeft() + insets.getRight();
		}


		@Override
		protected double computePrefHeight(double width)
		{
			Insets insets = getInsets();
			Bounds bounds = textNode.getBoundsInLocal();
			return bounds.getHeight() + insets.getTop() + insets.getBottom();
		}

		public void setBgColor(Color color, double alpha)
		{
			Color c = new Color(color.getRed(), color.getGreen(),color.getBlue(), alpha);
			
			double w = getWidth(),h = getHeight();
			if(w<=0|| h <= 0) return;
			
			GraphicsContext gc = bgNode.getGraphicsContext2D();
			gc.clearRect(0, 0, w, h);
			gc.setFill(c);
			gc.fillRoundRect(0, 0, w, h, 8, 8);
			//System.out.println("draw: " + w + "," + h + "," + alpha);
		}
		
		public void setText(String text)
		{
			textNode.setWrappingWidth(0);
			textNode.setText(text);
			
			Bounds bounds = textNode.getBoundsInLocal();
			double wrapW = bounds.getWidth();
			if(wrapW > 240) 
			{
				textNode.setWrappingWidth(240);
			}				
		}
		
		public void setTextColor(Color color, double alpha)
		{
			Color c = new Color(color.getRed(), color.getGreen(),color.getBlue(), alpha);
			
			textNode.setFill( c );
		}	
	}
}
