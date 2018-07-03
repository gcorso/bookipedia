package com.gcappslab.bookipedia.Library.Browse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Book.BookActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.R;

import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.Colors;

/**
 * Adapter to manage and display the grid view used for books in the Genres activity
 */

public class BookTableGridBaseAdapter extends BaseAdapter {

    private Context context;
    private List<Book> books;

    public BookTableGridBaseAdapter(Context context, List<Book> books) {
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
        View view = inflater.inflate(R.layout.book_table_grid, parent, false);

        TextView tvBookTitle = (TextView) view.findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = (TextView) view.findViewById(R.id.tvBookAuthor);
        tvBookTitle.setText(books.get(position).getName());
        tvBookAuthor.setText(books.get(position).getAuthorName());

        int back = books.get(position).getBookId()%Colors.length;
        view.setBackgroundColor(Color.parseColor(Colors[back]));

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