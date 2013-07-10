package com.elgubbo.tastekid;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

public class SectionsPagerAdapter extends FragmentPagerAdapter{
	
	FragmentManager mFragmentManager;
	SparseArray<Fragment> fragmentHolder;
	SparseArray<CardListArrayAdapter> adapterHolder;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mFragmentManager = fm;
		this.fragmentHolder = new SparseArray<Fragment>();
		this.adapterHolder = new SparseArray<CardListArrayAdapter>();
	}

	@Override
	public Fragment getItem(int position) {
		if(fragmentHolder.get(position) != null)
			return fragmentHolder.get(position);
		else {
			Fragment fragment = SectionFragment.init(position, adapterHolder);
//			fragment.setRetainInstance(true);
			fragmentHolder.append(position, fragment);
			return fragment;
		}
	}

	@Override
	public int getCount() {
		return 6;
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
			return TasteKidActivity.getAppContext().getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return TasteKidActivity.getAppContext().getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return TasteKidActivity.getAppContext().getString(R.string.title_section3).toUpperCase(l);
		case 3:
			return TasteKidActivity.getAppContext().getString(R.string.title_section4).toUpperCase(l);
		case 4:
			return TasteKidActivity.getAppContext().getString(R.string.title_section5).toUpperCase(l);
		case 5:
			return TasteKidActivity.getAppContext().getString(R.string.title_section6).toUpperCase(l);
		}
		return null;
	}
}
