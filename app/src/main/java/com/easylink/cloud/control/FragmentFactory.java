package com.easylink.cloud.control;

import com.easylink.cloud.absolute.BaseFragment;

public class FragmentFactory {
    public static  <T extends BaseFragment> T create(Class<T> clz){
        T fragment;
        try {
            fragment = (T) Class.forName(clz.getName()).newInstance();
        } catch (Exception e){
            fragment = null;
        }
        return fragment;
    }
}
