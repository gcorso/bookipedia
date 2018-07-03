package com.gcappslab.bookipedia.Reading;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.R;

/**
 * Adapter to manage and display the list of notes of the Notebook
 */

public class NoteListAdapter extends BaseAdapter{

    private ReadingActivity activity;
    private NoteList notes;
    private LayoutInflater inflater;
    private int length;

    public NoteListAdapter(ReadingActivity activity, NoteList notes, int length){
        this.activity = activity;
        this.notes = notes;
        inflater = (LayoutInflater.from(this.activity));
        this.length = length;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.note_item, null);
        final Note note = notes.get(position);

        String location = Integer.toString(100*note.getPos()/length)+ " %";
        final String text = note.getText();
        String type = Character.toString(note.getType());

        TextView tvLocation = (TextView) view.findViewById(R.id.tvLocationNote);
        tvLocation.setText(location);

        TextView tvText = (TextView) view.findViewById(R.id.tvTextNote);
        tvText.setText(text);

        ImageView btDelete = (ImageView) view.findViewById(R.id.btDelete);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes.remove(position);
                notifyDataSetChanged();
            }
        });

        ImageView btCopy = (ImageView) view.findViewById(R.id.btCopy);
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(activity, "Note copied", Toast.LENGTH_SHORT).show();
            }
        });


        if (type.startsWith("N")){
            RelativeLayout rlType = (RelativeLayout) view.findViewById(R.id.rlTypeNote);
            rlType.setBackgroundColor(Color.parseColor("#c9c9ff"));
            TextView tvType = (TextView) view.findViewById(R.id.tvNoteType);
            tvType.setText("Note");
            ImageView imageType = (ImageView) view.findViewById(R.id.imageType);
            imageType.setImageResource(R.drawable.notew);
        } else if (type.startsWith("B")){
            RelativeLayout rlType = (RelativeLayout) view.findViewById(R.id.rlTypeNote);
            rlType.setBackgroundColor(Color.parseColor("#e1f7d5"));
            TextView tvType = (TextView) view.findViewById(R.id.tvNoteType);
            tvType.setText("Bookmark");
            ImageView imageType = (ImageView) view.findViewById(R.id.imageType);
            imageType.setImageResource(R.drawable.bookmark);
            btCopy.setVisibility(View.GONE);
        }

        RelativeLayout llTouchNote = (RelativeLayout) view.findViewById(R.id.llTouchNote);
        llTouchNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goToNotebook(note.getPos());
                Log.i("touch", "1");
            }
        });

        return view;
    }
}