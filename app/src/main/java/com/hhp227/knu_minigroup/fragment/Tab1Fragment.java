package com.hhp227.knu_minigroup.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.*;
import com.hhp227.knu_minigroup.ArticleActivity;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.WriteActivity;
import com.hhp227.knu_minigroup.adapter.ArticleListAdapter;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.ArticleItem;
import com.hhp227.knu_minigroup.ui.floatingactionbutton.FloatingActionButton;
import com.hhp227.knu_minigroup.ui.scrollable.BaseFragment;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab1Fragment extends BaseFragment {
    public static final int LIMIT = 10;
    public static final int UPDATE_ARTICLE = 20;

    public static boolean isAdmin;
    public static String groupId, groupName, key;
    private ArticleListAdapter articleListAdapter;
    private FloatingActionButton floatingActionButton;
    private List<String> articleItemKeys;
    private List<ArticleItem> articleItemValues;
    private ListView listView;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View footerLoading;

    private boolean hasRequestedMore; // 데이터 불러올때 중복안되게 하기위한 변수
    private int offSet;
    private long mLastClickTime; // 클릭시 걸리는 시간

    public Tab1Fragment() {
    }

    public static Tab1Fragment newInstance(boolean isAdmin, String grpId, String grpNm, String key) {
        Tab1Fragment fragment = new Tab1Fragment();
        Bundle args = new Bundle();
        args.putBoolean("admin", isAdmin);
        args.putString("grp_id", grpId);
        args.putString("grp_nm", grpNm);
        args.putString("key", key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("admin");
            groupId = getArguments().getString("grp_id");
            groupName = getArguments().getString("grp_nm");
            key = getArguments().getString("key");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        floatingActionButton = rootView.findViewById(R.id.fab_button);
        footerLoading = View.inflate(getContext(), R.layout.load_more, null);
        listView = rootView.findViewById(R.id.lv_article);
        relativeLayout = rootView.findViewById(R.id.rl_write);
        swipeRefreshLayout = rootView.findViewById(R.id.srl_article_list);
        articleItemKeys = new ArrayList<>();
        articleItemValues = new ArrayList<>();
        articleListAdapter = new ArticleListAdapter(getActivity(), articleItemKeys, articleItemValues);
        offSet = 1; // offSet 초기화
        listView.addFooterView(footerLoading);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                intent.putExtra(getString(R.string.extra_admin), isAdmin);
                intent.putExtra(getString(R.string.extra_group_id), groupId);
                intent.putExtra(getString(R.string.extra_group_name), groupName);
                intent.putExtra(getString(R.string.extra_key), key);
                startActivity(intent);
                return;
            }
        });
        listView.setAdapter(articleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 두번 클릭시 방지
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return;
                mLastClickTime = SystemClock.elapsedRealtime();

                ArticleItem articleItem = articleItemValues.get(position);
                Intent intent = new Intent(getContext(), ArticleActivity.class);
                intent.putExtra("admin", isAdmin);
                intent.putExtra("grp_id", groupId);
                intent.putExtra("grp_nm", groupName);
                intent.putExtra("artl_num", articleItem.getId());
                intent.putExtra("position", position + 1);
                intent.putExtra("auth", articleItem.isAuth() || app.AppController.getInstance().getPreferenceManager().getUser().getUid().equals(articleItem.getUid()));
                intent.putExtra("grp_key", key);
                intent.putExtra("artl_key", articleListAdapter.getKey(position));
                startActivityForResult(intent, UPDATE_ARTICLE);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean lastItemVisibleFlag;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag && !hasRequestedMore) {
                    // 화면이 바닦에 닿을때 처리
                    // 로딩중을 알리는 프로그레스바를 보인다.
                    footerLoading.setVisibility(View.VISIBLE);

                    // 다음 데이터를 불러온다.
                    offSet += LIMIT;
                    hasRequestedMore = true;
                    fetchArticleList();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount;
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return;
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                intent.putExtra(getString(R.string.extra_admin), isAdmin);
                intent.putExtra(getString(R.string.extra_group_id), groupId);
                intent.putExtra(getString(R.string.extra_group_name), groupName);
                intent.putExtra(getString(R.string.extra_key), key);
                startActivity(intent);
                return;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        offSet = 1;
                        articleItemKeys.clear();
                        articleItemValues.clear();
                        fetchArticleList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright);

        progressDialog = ProgressDialog.show(getActivity(), "", "불러오는중...");
        fetchArticleList();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_ARTICLE && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", 0) - 1;
            ArticleItem articleItem = articleItemValues.get(position);
            articleItem.setTitle(data.getStringExtra("sbjt"));
            articleItem.setContent(data.getStringExtra("txt"));
            articleItem.setImages(data.getStringArrayListExtra("img")); // firebase data
            articleItem.setReplyCount(data.getStringExtra("cmmt_cnt"));
            articleItemValues.set(position, articleItem);
            articleListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return listView != null && listView.canScrollVertically(direction);
    }

    private void fetchArticleList() {
        String params = "?CLUB_GRP_ID=" + groupId + "&startL=" + offSet + "&displayL=" + LIMIT;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoint.GROUP_ARTICLE_LIST + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Source source = new Source(response);
                try {
                    // 페이징 처리
                    String page = source.getFirstElementByClass("paging").getFirstElement("title", "현재 선택 목록", false).getTextExtractor().toString();
                    List<Element> list = source.getAllElementsByClass("listbox2");

                    for (Element element : list) {
                        Element viewArt = element.getFirstElementByClass("view_art");
                        Element commentWrap = element.getFirstElementByClass("comment_wrap");

                        boolean auth = viewArt.getAllElementsByClass("btn-small-gray").size() > 0;
                        String id = commentWrap.getAttributeValue("num");
                        String listTitle = viewArt.getFirstElementByClass("list_title").getTextExtractor().toString();
                        String title = listTitle.substring(0, listTitle.lastIndexOf("-"));
                        String name = listTitle.substring(listTitle.lastIndexOf("-") + 1);
                        String date = viewArt.getFirstElement(HTMLElementName.TD).getTextExtractor().toString();
                        List<Element> images = viewArt.getAllElements(HTMLElementName.IMG);
                        List<String> imageList = new ArrayList<>();
                        if (images.size() > 0) {
                            for (Element image : images) {
                                String imageUrl = !image.getAttributeValue("src").contains("http") ? EndPoint.BASE_URL + image.getAttributeValue("src") : image.getAttributeValue("src");
                                imageList.add(imageUrl);
                            }
                        }
                        StringBuilder content = new StringBuilder();
                        for (Element childElement : viewArt.getFirstElementByClass("list_cont").getChildElements())
                            content.append(childElement.getTextExtractor().toString().concat("\n"));

                        String replyCnt = commentWrap.getContent().getFirstElement(HTMLElementName.P).getTextExtractor().toString();

                        ArticleItem articleItem = new ArticleItem();
                        articleItem.setId(id);
                        articleItem.setTitle(title.trim());
                        articleItem.setName(name.trim());
                        articleItem.setDate(date);
                        articleItem.setContent(content.toString().trim());
                        articleItem.setImages(imageList);
                        articleItem.setReplyCount(replyCnt);
                        articleItem.setAuth(auth);

                        articleItemKeys.add(id);
                        articleItemValues.add(articleItem);
                    }
                    articleListAdapter.notifyDataSetChanged();
                    // 중복 로딩 체크하는 Lock을 했던 HasRequestedMore변수를 풀어준다.
                    hasRequestedMore = false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    hideProgressDialog();
                    relativeLayout.setVisibility(!articleItemValues.isEmpty() ? View.GONE : View.VISIBLE);
                    floatingActionButton.setVisibility(!articleItemValues.isEmpty() ? View.VISIBLE : View.GONE);
                    initFirebaseData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", app.AppController.getInstance().getPreferenceManager().getCookie());
                return headers;
            }
        };
        app.AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void initFirebaseData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Articles");
        fetchArticleListFromFirebase(databaseReference.child(key));
    }

    private void fetchArticleListFromFirebase(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    ArticleItem value = snapshot.getValue(ArticleItem.class);
                    int index = articleItemKeys.indexOf(value.getId());
                    if (index > -1) {
                        articleItemValues.set(index, value);
                        articleItemKeys.set(index, key);
                    }
                }
                articleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("파이어베이스", databaseError.getMessage());
            }
        });
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        footerLoading.setVisibility(View.GONE);
    }
}
