package com.jskaleel.fte.fragments.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jskaleel.fte.R;

import java.util.ArrayList;

/**
 * Created by Home on 12/6/2016.
 */

public class DownloadFragemntAdapter extends RecyclerView.Adapter<DownloadFragemntAdapter.CustomViewHolder> {
    Context context ;
    ArrayList<String> items ;

    public DownloadFragemntAdapter(Context downloadsFragment, ArrayList<String> item) {
        this.context = downloadsFragment;
        this.items = item;
    }

    @Override
    public DownloadFragemntAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_books_list_item, parent, false);

        return new CustomViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(DownloadFragemntAdapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtAuthor;
        public CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtAuthor = (TextView) itemView.findViewById(R.id.txt_author);

        }
    }
}
