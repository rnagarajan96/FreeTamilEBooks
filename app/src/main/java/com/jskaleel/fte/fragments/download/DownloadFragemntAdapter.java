package com.jskaleel.fte.fragments.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_list_item, parent, false);

        return new CustomViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(final DownloadFragemntAdapter.CustomViewHolder holder, final int position) {
        final DownloadedBooks singleBook = items.get(position);
        holder.txtAuthor.setText(singleBook.getAuthor());
        holder.txtTitle.setText(singleBook.getBookTitle());

        holder.ivFavourite.setVisibility(View.VISIBLE);
        holder.txtDownload.setVisibility(View.GONE);

        holder.ivFavourite.setImageResource(R.drawable.ic_close_black);
        holder.txtOpen.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(singleBook.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivBookImage);

        holder.rlItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadedItemClickedListner != null) {
                    downloadedItemClickedListner.openDownloaded(singleBook);
                }
            }
        });

        holder.txtOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadedItemClickedListner != null) {
                    downloadedItemClickedListner.openDownloaded(singleBook);
                }
            }
        });

        holder.ivFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadedItemClickedListner.deleteItem(singleBook, holder.getAdapterPosition());
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

    public void removeDeleteItem(List<DownloadedBooks> list, int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtAuthor, txtDownload, txtOpen;
        ImageView ivBookImage, ivFavourite;
        RelativeLayout rlItemView;

        CustomViewHolder(View itemView) {
            super(itemView);
            rlItemView = (RelativeLayout) itemView.findViewById(R.id.rl_item_view);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtAuthor = (TextView) itemView.findViewById(R.id.txt_author);
            ivBookImage = (ImageView) itemView.findViewById(R.id.iv_book_image);
            txtDownload = (TextView) itemView.findViewById(R.id.txt_download);
            txtOpen = (TextView) itemView.findViewById(R.id.txt_open);
            ivFavourite = (ImageView) itemView.findViewById(R.id.iv_favourite);
        }
    }

}
