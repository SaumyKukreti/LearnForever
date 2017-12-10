package com.saumykukreti.learnforever.temporary;

/**
 * Created by saumy on 12/9/2017.
 */

public class TempClassDeleteThis {
    /**
     *
     final TextView textview = view.findViewById(R.id.currentdatabase);


     Button insert = view.findViewById(R.id.buttton_insert_note);
     Button showall = view.findViewById(R.id.buttton_showall);
     Button update = view.findViewById(R.id.buttton_update);
     Button delete = view.findViewById(R.id.buttton_delete);
     Button categories = view.findViewById(R.id.buttton_categories);

     final EditText noteToUpdate = view.findViewById(R.id.noteToUpdate);

     final EditText categoryedit = view.findViewById(R.id.categoryedit);
     final EditText titleedit = view.findViewById(R.id.titleedit);
     final EditText contentinshort = view.findViewById(R.id.cisedit);
     final EditText content = view.findViewById(R.id.contentedit);

     insert.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (!datacontroller.newNote(categoryedit.getText().toString(), titleedit.getText().toString(),
    contentinshort.getText().toString(),
    content.getText().toString(),
    "timestamp",
    true)) {

    Toast.makeText(getContext(), "Note not Added", Toast.LENGTH_SHORT).show();
    } else {
    Toast.makeText(getContext(), "Note Added", Toast.LENGTH_SHORT).show();
    }
    }
    });


     showall.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    List<NoteTable> a = datacontroller.getAllNotes();
    String s = "";
    for (NoteTable x : a) {
    s = s + x + "\n\n";

    }
    textview.setText(s);
    }
    });

     update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    if (datacontroller.updateNote(Long.parseLong(noteToUpdate.getText().toString()),
    categoryedit.getText().toString(),
    titleedit.getText().toString(),
    contentinshort.getText().toString(),
    content.getText().toString(),
    "timestamp",
    true)) {
    Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();

    } else {
    Toast.makeText(getContext(), "Note note updated", Toast.LENGTH_SHORT).show();

    }
    }
    });

     delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (datacontroller.deleteNote(Long.parseLong(noteToUpdate.getText().toString()))) {
    Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
    }
    else{
    Toast.makeText(getContext(), "Note not deleted", Toast.LENGTH_SHORT).show();

    }
    }
    });

     categories.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    List<String> listOfCategories = datacontroller.getListOfCategories();

    String srrr = "";
    for (String str : listOfCategories){
    srrr= srrr+ str;
    }

    textview.setText(srrr);
    }

    });


     */
}
