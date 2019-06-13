package com.easylink.cloud;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.easylink.cloud.absolute.CommonActivity;
import com.easylink.cloud.control.FragmentFactory;
import com.easylink.cloud.control.fragment.EnjoyFragment;
import com.easylink.cloud.control.fragment.FileFragment;
import com.easylink.cloud.control.fragment.MeFragment;
import com.easylink.cloud.control.fragment.NewFragment;
import com.easylink.cloud.control.fragment.UploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;


public class MainActivity extends CommonActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static SparseArray<Class> clzs = new SparseArray<>(5);    //  /spɑːs/
    private Map<Class, Fragment> fragmentMap = new HashMap<>();

    static {
        clzs.put(R.id.nav_new, NewFragment.class);
        clzs.put(R.id.nav_file, FileFragment.class);
        clzs.put(R.id.nav_add, UploadFragment.class);
        clzs.put(R.id.nav_enjoy, EnjoyFragment.class);
        clzs.put(R.id.nav_me, MeFragment.class);
    }

    private Class lastClz = null;
    @BindView(R2.id.navigation)
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setFragment(NewFragment.class);
        }
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setFragment(clzs.get(menuItem.getItemId()));
        return true;
    }

    private void setFragment(Class clz) {
        if (!fragmentMap.containsKey(clz)) {
            fragmentMap.put(clz, FragmentFactory.create(clz));
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragmentMap.get(clz)).commit();
        }
        // 初始化
        if (lastClz == null) {

        } else if (lastClz != clz) {
            getSupportFragmentManager().beginTransaction().hide(fragmentMap.get(lastClz)).show(fragmentMap.get(clz)).commit();
        }
        lastClz = clz;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("FileFragment", "activity create");
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("FileFragment", "activity selected");
        return false;
    }

}
