package com.gcappslab.bookipedia.Library.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Book.BookActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Reading.ReadingActivity;

import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.Colors;

/**
 * Adapter to manage and display the horizontal scroll list view used for books in the Home activity and Browse activity
 */

public class BookHorizGridAdapter extends RecyclerView.Adapter<BookHorizGridAdapter.SimpleViewHolder>{

    private Context context;
    private List<Book> books;
    private boolean local;

    public BookHorizGridAdapter(Context context, List<Book> elements, boolean local){
        this.context = context;
        this.books = elements;
        this.local = local;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvBookTitle;
        public final TextView tvBookAuthor;
        public final LinearLayout allView;

        public SimpleViewHolder(View view) {
            super(view);
            tvBookTitle = (TextView) view.findViewById(R.id.tvBookTitle);
            tvBookAuthor = (TextView) view.findViewById(R.id.tvBookAuthor);
            allView = (LinearLayout) view.findViewById(R.id.allBookView);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.book_horizontal_grid, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.tvBookTitle.setText(books.get(position).getName());
        holder.tvBookAuthor.setText(books.get(position).getAuthorName());

        int back = books.get(position).getBookId()%Colors.length;
        holder.allView.setBackgroundColor(Color.parseColor(Colors[back]));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (local){
                    Intent intent = new Intent(context, ReadingActivity.class);
                    intent.putExtra("bookId", books.get(position).getBookId());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, BookActivity.class);
                    intent.putExtra("bookId", books.get(position).getBookId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }
}