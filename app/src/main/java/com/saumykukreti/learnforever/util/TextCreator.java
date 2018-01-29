package com.saumykukreti.learnforever.util;

import android.content.SharedPreferences;

import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;
import java.util.Random;

/**
 * Created by saumy on 12/16/2017.
 */

public class TextCreator {

    public static String getNoteText (NoteTable note){
        StringBuffer stringBuffer = new StringBuffer();

        if(!note.getTitle().isEmpty()){
            stringBuffer.append(note.getTitle());
            stringBuffer.append(Constants.PAUSE_FOR_THREE_HUNDERED_MILISECONDS);
        }

        if(!note.getContentInShort().isEmpty()){
            stringBuffer.append(note.getContentInShort());
            stringBuffer.append(Constants.PAUSE_FOR_THREE_HUNDERED_MILISECONDS);
        }

        //Note content is never empty
        stringBuffer.append(note.getContent());

        return stringBuffer.toString();
    }



    public static String getNoteText (List<NoteTable> notes){
        StringBuffer stringBuffer = new StringBuffer();

        for(int i =0;i<notes.size();i++){

            stringBuffer.append(getNoteText(notes.get(i)));

            if(i==notes.size()-1){
                //Last note
                stringBuffer.append(Constants.PAUSE_FOR_THREE_HUNDERED_MILISECONDS);
                stringBuffer.append("Reading finished");
            }
            else{
                stringBuffer.append(Constants.PAUSE_FOR_FOUR_HUNDERED_MILISECONDS);
                stringBuffer.append("Next note");
                stringBuffer.append(Constants.PAUSE_FOR_FOUR_HUNDERED_MILISECONDS);
            }
        }
        return stringBuffer.toString();
    }

    /**
     *  This method gives back a random tip
     * @return
     */
    public static String getRandomTip() {
        String[] arrayOfStrings = new String[] {
                "Tips 1 ",
                "Tips 2 ",
                "Tips 3 "
        };

        int index = new Random().nextInt(arrayOfStrings.length-1);
        if(index < arrayOfStrings.length){
            return arrayOfStrings[index];
        }
        else{
            return arrayOfStrings[0];
        }
    }

    public static String getIntervalText(int[] dayIntervalOne) {
        StringBuffer stringBuffer = new StringBuffer();

        for(int i : dayIntervalOne){
            stringBuffer.append(i).append(",");
        }

        //Removing last ,
        stringBuffer.substring(0,stringBuffer.length()-1);

        return stringBuffer.toString();
    }

    /**
     *  This method creates a string and returns it based on the options selected
     * @param title
     * @param cis
     * @param category
     */
    public static String getNoteSettingsText(boolean title, boolean cis, boolean category) {
        StringBuffer stringBuffer = new StringBuffer();
        if(title)
            stringBuffer.append("Title").append(", ");
        if(cis)
            stringBuffer.append("Content In Short").append(", ");
        if(category)
            stringBuffer.append("Category").append(", ");

        stringBuffer.append("Content");

        if(stringBuffer.length()>0){
            stringBuffer.substring(0,stringBuffer.length()-2);
        }else{
            stringBuffer.append("Only content");
        }

        return stringBuffer.toString();
    }
}
