package com.easylink.cloud;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.easylink.cloud.absolute.CommonActivity;
import com.easylink.cloud.control.FragmentFactory;
import com.easylink.cloud.control.fragment.EnjoyFragment;
import com.easylink.cloud.control.fragment.FileFragment;
import com.easylink.cloud.control.fragment.MeFragment;
import com.easylink.cloud.control.fragment.NewFragment;
import com.easylink.cloud.control.fragment.UploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;


public class MainActivity extends CommonActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    static Class[] clzs;
    static {
        clzs = new Class[5];
        clzs[0] = NewFragment.class;
        clzs[1] = FileFragment.class;
        clzs[2] = UploadFragment.class;
        clzs[3] = EnjoyFragment.class;
        clzs[4] = MeFragment.class;
    }
    private Fragment[] fragments = new Fragment[5];
    private int currentFragmentIndex = -1;
    //private Fragment currentFragment;

    @BindView(R2.id.navigation)
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            setCurrentFragment(0);
        }
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("FileFragment","activity create");
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("FileFragment","activity selected");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_new:
                setCurrentFragment(0);
                return true;
            case R.id.nav_file:
                setCurrentFragment(1);
                return true;
            case R.id.nav_add:
                setCurrentFragment(2);
                return true;
            case R.id.nav_enjoy:
                setCurrentFragment(3);
                return true;
            case R.id.nav_me:
                setCurrentFragment(4);
                return true;
        }
        return false;
    }

    /**
     * @param index 被点击的Fragment索引
     *              index
     */
    private void setCurrentFragment(int index) {
        Log.d(TAG, "setCurrentFragment: " + index);
        if (fragments[index] == null) {
            fragments[index] = FragmentFactory.create(clzs[index]);
            getSupportFragmentManager().beginTransaction().add(R.id.content,fragments[index]).commit();
        }
        // 初始化
        if(currentFragmentIndex == -1) {
            currentFragmentIndex = index;
            return;
        }
        // 没有重复点击
        if(currentFragmentIndex != index){
            getSupportFragmentManager().beginTransaction().hide(fragments[currentFragmentIndex]).show(fragments[index]).commit();
            return;
        }
        currentFragmentIndex = index;
    }

}
