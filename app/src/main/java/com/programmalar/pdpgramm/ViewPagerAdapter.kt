package com.programmalar.pdpgramm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.programmalar.pdpgramm.fragments.ViewPagerFragment
import com.programmalar.pdpgramm.models.ViewPager

class ViewPagerAdapter(var list: List<ViewPager>,fragmentManager: FragmentManager):FragmentStatePagerAdapter(fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getItem(position: Int): Fragment {
        return ViewPagerFragment.newInstance(list[position])
    }

    override fun getCount(): Int {
        return 2
    }

}