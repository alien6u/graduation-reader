package com.jayqqaa12.abase.core.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/***
 *
 */
public class AFragmentPagerAdapter extends FragmentPagerAdapter
{
	protected List<Fragment> list = new ArrayList<Fragment>(5);

	protected List<String> lables = new ArrayList<String>(5);

	public AFragmentPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	public AFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList)
	{
		super(fm);

		this.list = fragmentsList;
	}

	public AFragmentPagerAdapter(FragmentManager fm, Fragment... fragmentsList)
	{
		super(fm);

		for (Fragment frag : fragmentsList)
		{
			list.add(frag);
		}
	}

	public void setFragment(Class... fragments)
	{
		for (Class f : fragments)
			this.list.add(AFragment.newInstance(f));
	}

	public void setFragment(Fragment... fragments)
	{

		for (Fragment f : fragments)
			this.list.add(f);
	}

	public void setFragment(List<Fragment> fragments)
	{

		for (Fragment f : fragments)
			this.list.add(f);
	}

	public void setLable(String... lables)
	{

		for (String l : lables)
			this.lables.add(l);
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return lables.get(position);
	}

	@Override
	public Fragment getItem(int pos)
	{
		return list.get(pos);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

}
