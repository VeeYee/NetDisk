package edu.hbuas.netdisk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {

    public String dateFormatUtil(String time){
        String result = "";

        //记录上传的时间
        String recordDate = time.split(" ")[0];  //年月日
        String recordTime = time.split(" ")[1];  //时分秒
        String recordDay = recordDate.split("-")[2]; //获取记录上传的日子

        //获取当前时间
        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());  //本地时间
        String nowDay = nowDate.split("-")[2];

        //如果是当天的记录，显示具体时间
        if(recordDate.equals(nowDate)){
            result = recordTime;
        }else if((Integer.parseInt(nowDay)-Integer.parseInt(recordDay))==1){
            result = "昨天";
        }else{
            result = recordDate;
        }



//        //处理本周内的时间，获取当前的年月
//        String year_month = nowDate.split("-")[0]+"-"+nowDate.split("-")[1]+"-";
//        switch (Integer.parseInt(nowDay)-Integer.parseInt(recordDay)){
//            //当天记录，只显示上传具体时间
//            case 0:{
//                result =  recordTime;
//                break;
//            }
//            //本周内的时间，显示星期
//            case 1:{
//                result = "昨天";
//                break;
//            }
//            case 2:{
//                String day = (Integer.parseInt(nowDate.split("-")[2])-2)+"";
//                result = dateToWeek(year_month+day);
//                break;
//            }
//            case 3:{
//                String day = (Integer.parseInt(nowDate.split("-")[2])-3)+"";
//                result = dateToWeek(year_month+day);
//                break;
//            }
//            case 4:{
//                String day = (Integer.parseInt(nowDate.split("-")[2])-4)+"";
//                result = dateToWeek(year_month+day);
//                break;
//            }
//            case 5:{
//                String day = (Integer.parseInt(nowDate.split("-")[2])-5)+"";
//                result = dateToWeek(year_month+day);
//                break;
//            }
//            case 6:{
//                String day = (Integer.parseInt(nowDate.split("-")[2])-6)+"";
//                result = dateToWeek(year_month+day);
//                break;
//            }
//        }
        return result;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param datetime  yyyy-MM-dd
     * @return 当前日期是星期几
     */
    public String dateToWeek(String datetime){
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


//    public static void main(String[] args) {
//        DateFormatUtil d = new DateFormatUtil();
//        System.out.println(d.dateToWeek("2019-6-25"));
//    }
}
