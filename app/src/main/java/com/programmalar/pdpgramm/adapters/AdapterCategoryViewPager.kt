package com.programmalar.pdpgramm.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.programmalar.pdpgramm.fragments.ChatsAndGroupFragment
import com.programmalar.pdpgramm.models.Category

class AdapterCategoryViewPager(var listCategory:ArrayList<Category>,fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return listCategory.size
    }

    override fun createFragment(position: Int): Fragment {
        return ChatsAndGroupFragment.newInstance(listCategory[position].nameCategory!!,
            listCategory[position].positionCategory!!
        )
    }
}