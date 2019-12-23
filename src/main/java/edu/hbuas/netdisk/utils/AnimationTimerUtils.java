package edu.hbuas.netdisk.utils;

import javafx.animation.AnimationTimer;

public abstract class AnimationTimerUtils extends AnimationTimer
{
	private long interval = 0;
	private long lastTime = 0; 
		
	// interval 定时器间隔, 毫秒值
	// 间隔应大于40ms，不然可能达不到精度
	public AnimationTimerUtils(int interval)
	{
		this.interval = interval * 1000000L; // 转成纳秒
	}

	@Override
	public void handle(long now)
	{		
		if( now - lastTime >= interval)
		{
			// 注：消除累计误差
			if(lastTime == 0) 
				lastTime = now;
			else 
				lastTime += interval;
			
			// 更新动画显示
			updateAnimation();
		}		
	}
	
	// 调用者需实现这个方法
	public abstract void updateAnimation();

}
