package com.hhp227.knu_minigroup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.hhp227.knu_minigroup.fragment.TabHostLayoutFragment;
import com.hhp227.knu_minigroup.ui.navigationdrawer.DrawerArrowDrawable;

public class GroupActivity extends FragmentActivity {
    private boolean mIsAdmin;
    private int mPosition;
    private String mGroupId, mGroupName, mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ActionBar actionBar = getActionBar();
        Intent intent = getIntent();
        mIsAdmin = intent.getBooleanExtra(getString(R.string.extra_admin), false);
        mGroupId = intent.getStringExtra(getString(R.string.extra_group_id));
        mGroupName = intent.getStringExtra(getString(R.string.extra_group_name));
        mPosition = intent.getIntExtra("position", 0);
        mKey = intent.getStringExtra(getString(R.string.extra_key));
        TabHostLayoutFragment fragMain = TabHostLayoutFragment.newInstance(mIsAdmin, mGroupId, mGroupName, mPosition, mKey);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(new DrawerArrowDrawable(this) {
                @Override
                public boolean isLayoutRtl() {
                    return false;
                }
            });
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(mGroupName);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragMain).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        else if (item.getItemId() == R.id.action_chat) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("grp_chat", true);
            intent.putExtra("chat_nm", mGroupName);
            intent.putExtra("uid", mGroupId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
