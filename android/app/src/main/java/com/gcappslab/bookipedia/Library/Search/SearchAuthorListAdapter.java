package com.gcappslab.bookipedia.Library.Search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Author.AuthorActivity;
import com.gcappslab.bookipedia.Library.Objects.Author;
import com.gcappslab.bookipedia.R;

import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.Colors;

/**
 * Adapter to manage and display the list view used for searched authors in the Search activity
 */

public class SearchAuthorListAdapter extends BaseAdapter {

    private Context context;
    private List<Author> authors;

    public SearchAuthorListAdapter(Context context, List<Author> authors) {
        this.context = context;
        this.authors = authors;
    }

    @Override
    public int getCount() {
        return authors.size();
    }

    @Override
    public Object getItem(int position) {
        return authors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_author_item, parent, false);

        TextView tvAuthorName = (TextView) view.findViewById(R.id.tvName);
        tvAuthorName.setText(authors.get(position).getAuthorName());

        ImageView imagePoint = (ImageView) view.findViewById(R.id.point);
        int back = authors.get(position).getAuthorId()%Colors.length;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor(Colors[back]));
        imagePoint.setBackground(shape);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AuthorActivity.class);
                intent.putExtra("authorId", authors.get(position).getAuthorId());
                context.startActivity(intent);
            }
        });

        return view;
    }

}