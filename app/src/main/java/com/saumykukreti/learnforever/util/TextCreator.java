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
        stringBuffer.append("Reading current note");
        stringBuffer.append("\n");
        stringBuffer.append("Title    ");
        stringBuffer.append(note.getTitle());
        stringBuffer.append(note.getContentInShort());
        return stringBuffer.toString();
    }

    public static String getNoteText (List<NoteTable> notes){
        StringBuffer stringBuffer = new StringBuffer();

        for(int i =0;i<notes.size();i++){
            NoteTable note = notes.get(i);
            stringBuffer.append("Current note");
            stringBuffer.append("\n");
            stringBuffer.append("Title");
            stringBuffer.append(note.getTitle());
            stringBuffer.append(note.getContentInShort());

            if(i==notes.size()-1){
                //Last note
                stringBuffer.append("Reading finished");
            }
            else{
                stringBuffer.append(Constants.PAUSE_LINE_SEPARATOR);
                stringBuffer.append("Starting next note");
            }
        }
        return stringBuffer.toString();
    }
}
