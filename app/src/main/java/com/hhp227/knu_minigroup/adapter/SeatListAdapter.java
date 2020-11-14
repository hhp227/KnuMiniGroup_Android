package com.hhp227.knu_minigroup.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.dto.SeatItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SeatListAdapter extends RecyclerView.Adapter<SeatListAdapter.SeatListHolder> {
    private final List<SeatItem> mSearItemList;

    public SeatListAdapter(List<SeatItem> mSearItemList) {
        this.mSearItemList = mSearItemList;
    }

    @NonNull
    @Override
    public SeatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_item, parent, false);
        return new SeatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatListHolder holder, int position) {
        holder.bind(mSearItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSearItemList.size();
    }

    public static class SeatListHolder extends RecyclerView.ViewHolder {
        private final TextView name, text, status;

        SeatListHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            text = itemView.findViewById(R.id.text);
            status = itemView.findViewById(R.id.seat);
        }

        private void bind(SeatItem seatItem) {
            name.setText(seatItem.name);
            text.setText(seatItem.disable == null ? "사용중 좌석" : seatItem.disable[0]);
            status.setText(seatItem.disable == null ?
                    "[" + seatItem.occupied + "/" + seatItem.activeTotal + "]" :
                    getPeriodTimeGenerator(itemView.getContext(), seatItem.disable[1]) + " ~ " + getPeriodTimeGenerator(itemView.getContext(), seatItem.disable[2]));

        }
    }

    // 타임 제네레이터
    private static String getPeriodTimeGenerator(Context context, String strDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.format_date2));
        Date date;

        df.setTimeZone(TimeZone.getDefault());
        if (TextUtils.isEmpty(strDate))
            return "";
        try {
            date = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        assert date != null;
        return sdf.format(date);
    }
}
