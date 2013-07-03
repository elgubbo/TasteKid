package com.elgubbo.tastekid;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class SectionsPagerAdapter extends FragmentPagerAdapter{
	
	FragmentManager mFragmentManager;
	Context appContext;
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.appContext = context;
		this.mFragmentManager = fm;
	}

	@Override
	public Fragment getItem(int position) {
		SectionFragment fragment = SectionFragment.init(position, appContext);
		return fragment;
	}

	@Override
	public int getCount() {
		return 5;
	}
	
	public Fragment getActiveFragment(ViewPager container, int position) {
		String name = makeFragmentName(container.getId(), position);
		return  mFragmentManager.findFragmentByTag(name);
	}

	private String makeFragmentName(int viewId, int index) {
		    return "android:switcher:" + viewId + ":" + index;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return appContext.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return appContext.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return appContext.getString(R.string.title_section3).toUpperCase(l);
		case 3:
			return appContext.getString(R.string.title_section4).toUpperCase(l);
		case 4:
			return appContext.getString(R.string.title_section5).toUpperCase(l);
		}
		return null;
	}
}
