package com.hhp227.knu_minigroup.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.WebViewActivity;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.BbsItem;

import java.util.List;

public class BbsListAdapter extends RecyclerView.Adapter<BbsListAdapter.BbsListHolder> {
    private final List<BbsItem> mBbsItemList;

    public BbsListAdapter(List<BbsItem> bbsItemList) {
        this.mBbsItemList = bbsItemList;
    }

    @NonNull
    @Override
    public BbsListAdapter.BbsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bbs_item, parent, false);
        return new BbsListHolder(view);
    }

    @Override
    public void onBindViewHolder(BbsListAdapter.BbsListHolder holder, int position) {
        holder.bind(mBbsItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mBbsItemList.size();
    }

    public static class BbsListHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        private final TextView title, writer, date;

        public BbsListHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            title = itemView.findViewById(R.id.item_title);
            writer = itemView.findViewById(R.id.item_writer);
            date = itemView.findViewById(R.id.item_date);
        }

        public void bind(final BbsItem bbsItem) {
            title.setText(bbsItem.getTitle());
            writer.setText(bbsItem.getWriter());
            date.setText(bbsItem.getDate());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.URL, EndPoint.URL_KNU + bbsItem.getUrl());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
