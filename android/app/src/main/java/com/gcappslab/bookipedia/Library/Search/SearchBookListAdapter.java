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

import com.gcappslab.bookipedia.Library.Book.BookActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.R;

import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.Colors;

/**
 * Adapter to manage and display the list view used for searched books in the Search activity
 */

public class SearchBookListAdapter extends BaseAdapter {

    private Context context;
    private List<Book> books;

    public SearchBookListAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_book_item, parent, false);

        TextView tvBookTitle = (TextView) view.findViewById(R.id.tvName);
        TextView tvBookAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        tvBookTitle.setText(books.get(position).getName());
        tvBookAuthor.setText(books.get(position).getAuthorName());

        ImageView imagePoint = (ImageView) view.findViewById(R.id.point);
        int back = books.get(position).getBookId()%Colors.length;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor(Colors[back]));
        imagePoint.setBackground(shape);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookActivity.class);
                intent.putExtra("bookId", books.get(position).getBookId());
                context.startActivity(intent);
            }
        });

        return view;
    }
}