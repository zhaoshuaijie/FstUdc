package com.lcsd.fstudc.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.lcsd.fstudc.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jie on 2018/5/21.
 */
public class MaterialFragment extends BaseFragment {

    @BindView(R.id.material_tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.material_viewpager)
    ViewPager mViewPager;

    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {"图片", "视频", "音频"};


    public static MaterialFragment newInstance() {
        return new MaterialFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_material;
    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        list.add(ImageFragment.newInstance());
        list.add(VideoFragment.newInstance());
        list.add(AudioFragment.newInstance());
        //ViewPager的适配器
        adapter = new MyAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        //绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        //重写这个方法，将设置每个Tab的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
