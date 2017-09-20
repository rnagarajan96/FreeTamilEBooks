package com.jskaleel.fte.fragments.home;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jskaleel.fte.HomeActivity;
import com.jskaleel.fte.R;
import com.jskaleel.fte.utils.FTELog;

import java.util.ArrayList;

class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {
    private Context context;
    private ArrayList<BookListParser.Books.Book> bookList, filterList;
    private ItemClickListener itemClickListener;
    private EmptyViewListener emptyViewListener;

    BookListAdapter(Context context, ArrayList<BookListParser.Books.Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.filterList = new ArrayList<>();

        this.filterList.addAll(this.bookList);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_list_item, parent, false);
        return new BookViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        final BookListParser.Books.Book singleItem = filterList.get(position);

        holder.txtBookTitle.setText(singleItem.title);
        holder.txtAuthorName.setText(singleItem.author);
        if (!TextUtils.isEmpty(singleItem.image)) {
            Glide.with(context).load(singleItem.image)
                    .centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.ivBookImage);
        }

        holder.txtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.downloadPressed(singleItem);
                }
            }
        });

        holder.txtOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.openPressed(singleItem);
                }
            }
        });
    }

    public void setEmptyViewListener(EmptyViewListener emptyViewListener) {
        this.emptyViewListener = emptyViewListener;
    }

    void filter(final String searchText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filterList.clear();
                if (TextUtils.isEmpty(searchText)) {
                    filterList.addAll(bookList);
                } else {
                    for (BookListParser.Books.Book item : bookList) {
                        if (!TextUtils.isEmpty(item.getTitle()) && !TextUtils.isEmpty(item.getAuthor())) {
                            if (item.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                    item.getAuthor().toLowerCase().contains(searchText.toLowerCase())) {
                                filterList.add(item);
                            }
                        }
                    }
                }

                ((HomeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(emptyViewListener != null) {
                            if (filterList.size() == 0) {
                                emptyViewListener.setEmptyViewOnUi(0);
                            } else {
                                emptyViewListener.setEmptyViewOnUi(1);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return (filterList != null ? filterList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    void setListItemListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    void updateList(ArrayList<BookListParser.Books.Book> book) {
        this.bookList = book;
        this.filterList.addAll(bookList);
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView txtBookTitle, txtAuthorName, txtDownload, txtOpen;
        ImageView ivBookImage, ivFavourite;

        BookViewHolder(View itemView) {
            super(itemView);
            txtBookTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtAuthorName = (TextView) itemView.findViewById(R.id.txt_author);
            ivBookImage = (ImageView) itemView.findViewById(R.id.iv_book_image);
            txtDownload = (TextView) itemView.findViewById(R.id.txt_download);
            txtOpen = (TextView) itemView.findViewById(R.id.txt_open);
            ivFavourite = (ImageView) itemView.findViewById(R.id.iv_favourite);
        }
    }
}
