package com.saumykukreti.learnforever.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by saumy on 12/21/2017.
 */

public class DateHandler {

    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YY");
        return dateFormat.format(date);
    }

    public static Date convertStringToDate(String date){
        if(date!=null && !date.isEmpty()) {
            return new Date(date);
        }
        else{
            //Return current date
            return new Date();
        }
    }
}
