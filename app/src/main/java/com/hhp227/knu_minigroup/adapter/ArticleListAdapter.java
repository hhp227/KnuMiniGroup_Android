package com.hhp227.knu_minigroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.hhp227.knu_minigroup.ArticleActivity;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.dto.ArticleItem;
import com.hhp227.knu_minigroup.fragment.Tab1Fragment;

import java.util.List;

import static com.hhp227.knu_minigroup.fragment.Tab1Fragment.UPDATE_ARTICLE;

public class ArticleListAdapter extends BaseAdapter {
    public static boolean LIKED;
    private static final int CONTENT_MAX_LINE = 4;
    private Activity activity;
    private ImageView articleImage;
    private LayoutInflater inflater;
    private LinearLayout replyButton, likeButton;
    private List<ArticleItem> articleItems;
    private TextView name, timestamp, content, contentMore, replyCount, likeCount;

    public ArticleListAdapter(Activity activity, List<ArticleItem> articleItems) {
        this.activity = activity;
        this.articleItems = articleItems;
    }

    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return articleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.article_item, null);

        name = convertView.findViewById(R.id.tv_name);
        timestamp = convertView.findViewById(R.id.tv_timestamp);
        content = convertView.findViewById(R.id.tv_content);
        contentMore = convertView.findViewById(R.id.tv_content_more);
        articleImage = convertView.findViewById(R.id.iv_article_image);
        replyCount = convertView.findViewById(R.id.tv_replycount);
        replyButton = convertView.findViewById(R.id.ll_reply);

        ArticleItem articleItem = articleItems.get(position);

        name.setText(articleItem.getName());
        timestamp.setText(articleItem.getTimeStamp());
        // 피드의 메시지가 비었는지 확인
        if (!TextUtils.isEmpty(articleItem.getContent())) {
            content.setText(articleItem.getContent());
            content.setMaxLines(CONTENT_MAX_LINE);
            content.setVisibility(View.VISIBLE);
        } else {
            // 피드 내용이 비었으면 화면에서 삭제
            content.setVisibility(View.GONE);
        }
        contentMore.setVisibility(!TextUtils.isEmpty(articleItem.getContent()) && content.getLineCount() > CONTENT_MAX_LINE ? View.VISIBLE : View.GONE);

        // 피드 이미지
        if (articleItem.getImage() != null) {
            articleImage.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(articleItem.getImage())
                    .apply(RequestOptions.errorOf(R.drawable.ic_launcher_background))
                    .transition(DrawableTransitionOptions.withCrossFade(150))
                    .into(articleImage);
        } else
            articleImage.setVisibility(View.GONE);
        replyCount.setText(articleItem.getReplyCount());

        // 댓글 버튼을 누르면 댓글쓰는곳으로 이동
        replyButton.setTag(position);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                ArticleItem articleItem = articleItems.get(position);

                Intent intent = new Intent(activity, ArticleActivity.class);
                intent.putExtra("grp_id", Tab1Fragment.groupId);
                intent.putExtra("grp_nm", Tab1Fragment.groupName);
                intent.putExtra("artl_num", articleItem.getId());
                intent.putExtra("position", position + 1);
                intent.putExtra("auth", articleItem.isAuth());
                intent.putExtra("isbottom", true);
                activity.startActivityForResult(intent, UPDATE_ARTICLE);
            }
        });

        return convertView;
    }
}
