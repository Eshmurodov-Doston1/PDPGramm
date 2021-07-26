package com.programmalar.pdpgramm.fragments


import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.FragmentViewPagerBinding
import com.programmalar.pdpgramm.models.ViewPager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPagerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: ViewPager? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as ViewPager
        }
    }
    lateinit var fragmentViewPagerBinding: FragmentViewPagerBinding
    lateinit var root:View
    lateinit var countDownTimer: CountDownTimer

    lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      fragmentViewPagerBinding = FragmentViewPagerBinding.inflate(inflater,container,false)
        root=fragmentViewPagerBinding.root
        handler = Handler(Looper.getMainLooper())

            fragmentViewPagerBinding.registration.setOnClickListener {
                var btn = fragmentViewPagerBinding.registration
                btn.startAnimation()
                handler.postDelayed({
                    var bundle = Bundle()
                    var navOptions = NavOptions.Builder()
                    navOptions.setEnterAnim(R.anim.enter)
                    navOptions.setExitAnim(R.anim.exite)
                    navOptions.setPopEnterAnim(R.anim.pop_enter)
                    navOptions.setPopExitAnim(R.anim.pop_exite)
                    findNavController().navigate(R.id.registrationFragment,bundle,navOptions.build())
                },2000)
            }
            when(param1!!.position){
                0->{
                    fragmentViewPagerBinding.image.setImageResource(param1!!.image!!)
                    fragmentViewPagerBinding.text.text = param1!!.text
                    fragmentViewPagerBinding.container.setBackgroundColor(Color.parseColor("#078E81"))
                }
                1->{
                    fragmentViewPagerBinding.image.setImageResource(param1!!.image!!)
                    fragmentViewPagerBinding.text.text = param1!!.text
                    fragmentViewPagerBinding.container.setBackgroundColor(Color.parseColor("#078E81"))
                    handler.postDelayed({
                        fragmentViewPagerBinding.registration.visibility = View.VISIBLE
                    },3500)

                }
        }
        return root
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewPagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: ViewPager) =
            ViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}