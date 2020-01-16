package com.hhp227.knu_minigroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.ReplyItem;

import java.util.List;

public class ReplyListAdapter extends BaseAdapter {
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<String> mReplyItemKeys;
    private List<ReplyItem> mReplyItemValues;

    public ReplyListAdapter(Activity activity, List<String> replyItemKeys, List<ReplyItem> replyItemValues) {
        this.mActivity = activity;
        this.mReplyItemKeys = replyItemKeys;
        this.mReplyItemValues = replyItemValues;
    }

    @Override
    public int getCount() {
        return mReplyItemValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mReplyItemValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (mInflater == null)
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reply_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        // 댓글 데이터 얻기
        ReplyItem replyItem = mReplyItemValues.get(position);

        Glide.with(mActivity)
                .load(replyItem.getUid() != null ? EndPoint.USER_IMAGE.replace("{UID}", replyItem.getUid()) : null)
                .apply(new RequestOptions().circleCrop().error(R.drawable.profile_img_circle))
                .into(viewHolder.profileImage);
        viewHolder.name.setText(replyItem.getName());
        viewHolder.reply.setText(replyItem.getReply());
        viewHolder.timeStamp.setText(replyItem.getDate());

        return convertView;
    }

    public String getKey(int position) {
        return mReplyItemKeys.get(position);
    }

    private static class ViewHolder {
        private ImageView profileImage;
        private TextView name, reply, timeStamp;

        public ViewHolder(View itemView) {
            profileImage = itemView.findViewById(R.id.iv_profile_image);
            name = itemView.findViewById(R.id.tv_name);
            reply = itemView.findViewById(R.id.tv_reply);
            timeStamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}
