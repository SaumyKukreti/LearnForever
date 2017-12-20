package com.saumykukreti.learnforever.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by saumy on 12/21/2017.
 */

public class DateHandler {

    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");
        return dateFormat.format(date);
    }
}
