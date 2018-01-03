package com.saumykukreti.learnforever.util;

import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

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
}
