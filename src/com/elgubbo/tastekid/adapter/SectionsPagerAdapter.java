package com.elgubbo.tastekid.adapter;

import java.util.Locale;

import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.SectionFragment;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.TasteKidApp;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

// TODO: Auto-generated Javadoc
/**
 * The Class SectionsPagerAdapter.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter  {

	/** The m fragment manager. */
	FragmentManager mFragmentManager;

	/** The fragment holder. */
	SparseArray<Fragment> fragmentHolder;

	/** The adapter holder. */
	// SparseArray<CardListArrayAdapter> adapterHolder;

	/**
	 * Instantiates a new sections pager adapter.
	 * 
	 * @param fm
	 *            the fragmentManager
	 * @param context
	 *            the context
	 */
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mFragmentManager = fm;
		this.fragmentHolder = new SparseArray<Fragment>();
	}

	/**
	 * Gets the active fragment.
	 * 
	 * @param container
	 *            the container
	 * @param position
	 *            the position
	 * @return the active fragment
	 */
	public Fragment getActiveFragment(ViewPager container, int position) {
		String name = makeFragmentName(container.getId(), position);
		return mFragmentManager.findFragmentByTag(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return TasteKidApp.TYPE_ARRAY.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {
		return SectionFragment.init(position);
		// if (fragmentHolder.get(position) != null)
		// return fragmentHolder.get(position);
		// else {
		// if(Configuration.DEVMODE)
		// Log.d("TasteKid", "Creating new fragment on position: "+position);
		// Fragment fragment = SectionFragment.init(position);
		// fragmentHolder.append(position, fragment);
		// return fragment;
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section3).toUpperCase(l);
		case 3:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section4).toUpperCase(l);
		case 4:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section5).toUpperCase(l);
		case 5:
			return TasteKidActivity.getAppContext()
					.getString(R.string.title_section6).toUpperCase(l);
		}
		return null;
	}

	/**
	 * Make fragment name.
	 * 
	 * @param viewId
	 *            the view id
	 * @param index
	 *            the index
	 * @return the string
	 */
	private String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}

}
