package com.easylink.cloud.control;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseActivity;
import com.easylink.cloud.control.fragment.EnjoyFragment;
import com.easylink.cloud.control.fragment.FileFragment;
import com.easylink.cloud.control.fragment.MeFragment;
import com.easylink.cloud.control.fragment.NewFragment;
import com.easylink.cloud.control.fragment.UploadFragment;


public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private Fragment[] fragments = new Fragment[5];
    private FrameLayout frameLayout;
    private Fragment currentFragment;
    private BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation);
        frameLayout = findViewById(R.id.content);
        navigationView.setOnNavigationItemSelectedListener(this);
        setCurrentFragment(0);

        Log.d(TAG, "onCreate: A");
        new Handler().post(()-> Log.d(TAG, "onCreate: B"));
        Log.d(TAG, "onCreate: C");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: D");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: E");
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


    private void setCurrentFragment(int index) {
        if (fragments[index] == null) {
            switch (index) {
                case 0:
                    fragments[index] = NewFragment.newInstance(this);
                    break;
                case 1:
                    fragments[index] = FileFragment.newInstance(this);
                    break;
                case 2:
                    fragments[index] = UploadFragment.newInstance(this);
                    break;
                case 3:
                    fragments[index] = EnjoyFragment.newInstance(this);
                    break;
                case 4:
                    fragments[index] = MeFragment.newInstance(this);
                    break;
            }
            if (currentFragment == null) {
                currentFragment = fragments[0];
                getSupportFragmentManager().beginTransaction().add(R.id.content, fragments[0]).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(currentFragment).add(R.id.content, fragments[index]).commit();

            }
        } else {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).show(fragments[index]).commit();
        }
        currentFragment = fragments[index];
    }


}
