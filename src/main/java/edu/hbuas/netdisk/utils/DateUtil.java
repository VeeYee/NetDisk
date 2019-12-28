package edu.hbuas.netdisk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理日期的类，用于传输记录
 * @author
 *
 */
public class DateUtil {

	public String getDate(String date){
		String []stDates=date.split(" ");
		return getDate1(stDates[0]);
	}
	//调用这个方法就可以得到最后客户那边显示时间
	private String getDate1(String date){
		 String text = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()); //获取当前时间
		 String dbDates[]=date.split("-");
		 String nowDates[]=text.split("-");
		 if (dbDates[0].equals(nowDates[0])&&dbDates[1].equals(nowDates[1])&&dbDates[2].equals(nowDates[2])) {//当前的进行的操作
			//这里有问题，应该返回数据库的时分秒
			 return new SimpleDateFormat("HH:mm:ss").format(new Date());
		//年月都相等的时候
		 }else if (dbDates[0].equals(nowDates[0])&&dbDates[1].equals(nowDates[1])) {
			if (Integer.parseInt(dbDates[2])-Integer.parseInt(nowDates[2])==-1) {
				return "昨天";
			}else{
				//返回星期，并且只会返回这个星期的
				int dbAllDays=allDays(Integer.parseInt(dbDates[0]), Integer.parseInt(dbDates[1]), Integer.parseInt(dbDates[2]));
				int nowAllDays=allDays(Integer.parseInt(nowDates[0]), Integer.parseInt(nowDates[1]), Integer.parseInt(nowDates[2]));
				if (nowAllDays-dbAllDays>=6) {
					String stdbyear=dbDates[0].substring(2,4);
					//同年同月不同星期
					return stdbyear+"-"+dbDates[1]+"-"+dbDates[2];
				}else {
					int dbWeek=CaculateWeekDay(dbDates);
					int nowWeek=CaculateWeekDay(nowDates);
					if (dbWeek>nowWeek) {
						String stdbyear=dbDates[0].substring(2,4);
						return stdbyear+"-"+dbDates[1]+"-"+dbDates[2];
					} else {
						String [] weeks=new String[]{"日期错误","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};		
						return weeks[dbWeek];
					}
				}
				
				
			}
		}else {
			return "日期错误";
		}
	}
	
	
	 private  int CaculateWeekDay(String [] Date)
     {
		 int y=Integer.parseInt(Date[0]);
		 int m=Integer.parseInt(Date[1]);
		 int d=Integer.parseInt(Date[2]);
         if (m == 1) { m = 13; y--; }
         if (m == 2) { m = 14; y--; }
         int week = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7 + 1;
         return week;
     }

	
	//计算天数的方法
	private int allDays(int year,int month,int day ) {
	        int count = 0;
	        int days = 0;
	        if (year > 0 && month > 0 && month < 13 && day > 0 && day < 32) {
	            for (int i = 1; i < month; i++) {
	                switch (i) {
	                case 1:
	                case 3:
	                case 5:
	                case 7:
	                case 8:
	                case 10:
	                case 12:
	                    days = 31;
	                    break;
	                case 4:
	                case 6:
	                case 9:
	                case 11:
	                    days = 30;
	                    break;
	                case 2: {
	                    if ((year % 4 == 0 && year % 1 != 0) || (year % 400 == 0)) {
	                        days = 29;
	                    } else {
	                        days = 28;
	                    }
	                    break;
	                }
	                }
	                count = count + days;
	            }
	            count = count + day;
	        }
	        return count;
	    }
	
	
}
