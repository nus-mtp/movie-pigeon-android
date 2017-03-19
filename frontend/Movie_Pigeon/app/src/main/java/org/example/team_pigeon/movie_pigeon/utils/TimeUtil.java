package org.example.team_pigeon.movie_pigeon.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHENGX on 2017/3/18.
 */

public class TimeUtil {

    public List<Date> getDateList(){
        List<Date> week = new ArrayList<>();
        Calendar timeToAdd = Calendar.getInstance();
        Date date;
        for(int i=0;i<7;i++){
            date = timeToAdd.getTime();
            week.add(date);
            timeToAdd.add(Calendar.DATE,1);
        }
        return week;
    }

    //Used by cinema page
    public List<String> getDateListToString_MMDDE(List<Date> week){
        List<String> stringList = new ArrayList<>();
        stringList.add("Please Choose Date");
        for(Date date:week){
            stringList.add(new SimpleDateFormat("MM-dd E", Locale.ENGLISH).format(date));
        }
        return stringList;
    }

    public List<String> getDateListToString_YYYYMMDD(List<Date> week){
        List<String> stringList = new ArrayList<>();
        for(Date date:week){
            stringList.add(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date));
        }
        return stringList;
    }
}
