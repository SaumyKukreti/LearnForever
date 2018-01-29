package com.saumykukreti.learnforever.util;

import com.saumykukreti.learnforever.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by saumy on 12/21/2017.
 */

public class Converter {

    /**
     *  This method converts a string containing values separated with "," to a list
     * @param string
     * @return
     */
    public static List<String> convertStringToList(String string){
        if(string == null){
            //Return empty list
            return new ArrayList<>();
        }else if(string.isEmpty()){
            return new ArrayList<>();
        }else{
            //Convert string to array
            String[] arrayOfValues = string.split(",");

            //Convert to list
            List<String> listOfValues = new LinkedList<String>(Arrays.asList(arrayOfValues));

            return listOfValues;
        }
    }

    public static String convertListToString(List<String> list){
        if(list==null){
            return "";
        }
        else if (list.isEmpty()){
            return "";
        }
        else{
            StringBuffer str = new StringBuffer();
            for(int i=0 ;i<list.size();i++){
                if(i == list.size()-1){
                    //Last element
                    str.append(list.get(i));
                }
                else{
                    str.append(list.get(i)).append(",");
                }
            }

            return str.toString();
        }
    }

    public static int[] convertStringToIntArray(String str){
        try {

            if(str.isEmpty()){
                return Constants.DAY_INTERVAL_ONE;
            }
            String[] strSplitted = str.split(",");
            int intArray[] = new int[strSplitted.length];
            for (int i = 0; i < strSplitted.length; i++) {
                intArray[i] = Integer.parseInt(strSplitted[i]);
            }
            return intArray;
        }
        catch (Exception e){
            return Constants.DAY_INTERVAL_ONE;
        }
    }
}
