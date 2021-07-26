package com.programmalar.pdpgramm.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.ViewPagerAdapter
import com.programmalar.pdpgramm.databinding.FragmentOpenBinding
import com.programmalar.pdpgramm.models.ViewPager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OpenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OpenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var fragmentOpenBinding: FragmentOpenBinding
    lateinit var root:View
    var sharedPreferences: SharedPreferences?=null
    lateinit var handler: Handler
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var listView:ArrayList<ViewPager>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOpenBinding = FragmentOpenBinding.inflate(inflater,container,false)
        root = fragmentOpenBinding.root
        sharedPreferences = requireActivity().getSharedPreferences("viewpager",0)
        val boolean = sharedPreferences!!.getBoolean("open", false)

        var editor = sharedPreferences!!.edit()
        editor.putBoolean("open", true)
        editor.commit()
        handler = Handler(Looper.getMainLooper())
        if (boolean){
            handler.postDelayed({
                var bundle = Bundle()
                var navOptions = NavOptions.Builder()
                navOptions.setEnterAnim(R.anim.enter)
                navOptions.setExitAnim(R.anim.exite)
                navOptions.setPopEnterAnim(R.anim.pop_enter)
                navOptions.setPopExitAnim(R.anim.pop_exite)
                findNavController().navigate(R.id.registrationFragment,bundle,navOptions.build())
            },2000)
            fragmentOpenBinding.viewPager.visibility = View.INVISIBLE
            fragmentOpenBinding.tablayout.visibility = View.INVISIBLE
            fragmentOpenBinding.textAppName.visibility = View.VISIBLE
        }else{
            loadView()
            viewPagerAdapter = ViewPagerAdapter(listView,childFragmentManager)
            fragmentOpenBinding.viewPager.adapter = viewPagerAdapter
            fragmentOpenBinding.tablayout.setupWithViewPager(fragmentOpenBinding.viewPager)
            handler.postDelayed({
                fragmentOpenBinding.viewPager.currentItem++
            },2000)
        }
        return root
    }

    private fun loadView() {
        listView = ArrayList()
        listView.add(ViewPager(R.drawable.ic_chat__1_,"Bunda siz xoxlagan kishingiz bilan gaplashishingiz mumkin faqat ro`yxatdan o`tgan xolda",0))
        listView.add(ViewPager(R.drawable.ic_add,"Iltimos ro`yxatdan o`ting va bizga qo`shiling",1))
    }


        override fun onDestroy() {
            super.onDestroy()
            var editor = sharedPreferences!!.edit()
            editor.putBoolean("night_mode", false)
            editor.commit()
        }

//    override fun onStop() {
//        super.onStop()
//        var editor = sharedPreferences!!.edit()
//        editor.putBoolean("night_mode", false)
//        editor.commit()
//    }

    override fun onResume() {
        super.onResume()
        var editor = sharedPreferences!!.edit()
        editor.putBoolean("night_mode", false)
        editor.commit()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OpenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OpenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}