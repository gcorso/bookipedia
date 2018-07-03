package com.gcappslab.bookipedia.Library.Browse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Objects.Constants;
import com.gcappslab.bookipedia.R;

/**
 * Adapter used to manage and display the grid of genres in the Genres activity
 */

public class GenresTableGridAdapter extends BaseAdapter{

    private Context context;
    public GenresTableGridAdapter(Context context) {
        super();

        this.context = context;
    }

    @Override
    public int getCount() {
        return Constants.Genres.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.genre_item, parent,false);

        TextView tvThemeName = (TextView) view.findViewById(R.id.tvGenreName);
        tvThemeName.setText(Constants.Genres[position]);
        ImageView imageTheme = (ImageView) view.findViewById(R.id.imageGenre);
        imageTheme.setImageResource(Constants.GenresImage[position]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GenresActivity.class);
                intent.putExtra("genre", Constants.Genres[position]);
                context.startActivity(intent);
            }
        });

        return view;
    }



}