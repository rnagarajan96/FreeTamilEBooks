package com.jskaleel.fte.fragments.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jskaleel.fte.R;
import com.jskaleel.fte.booksdb.DownloadedBooks;

import java.util.List;

/**
 * Created by Home on 12/6/2016.
 */

public class DownloadFragemntAdapter extends RecyclerView.Adapter<DownloadFragemntAdapter.CustomViewHolder> {
    private Context context ;
    private List<DownloadedBooks> items ;
    private DownloadedItemClicked downloadedItemClickedListner;

    public DownloadFragemntAdapter(Context downloadsFragment, List<DownloadedBooks> item) {
        this.context = downloadsFragment;
        this.items = item;
    }

    @Override
    public DownloadFragemntAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_books_list_item, parent, false);

        return new CustomViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(DownloadFragemntAdapter.CustomViewHolder holder, final int position) {
        holder.txtAuthor.setText(items.get(position).getAuthor());
        holder.txtTitle.setText(items.get(position).getBookTitle());
        Glide.with(context)
                .load(items.get(position).getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgBook);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadedItemClickedListner != null) {
                    downloadedItemClickedListner.openDownloaded(items.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setListener(DownloadedItemClicked downloadedItemClicked) {
        this.downloadedItemClickedListner = downloadedItemClicked;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtAuthor;
        ImageView imgBook;
        CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtAuthor = (TextView) itemView.findViewById(R.id.txt_author);
            imgBook = (ImageView) itemView.findViewById(R.id.iv_book_image);

        }
    }

}
