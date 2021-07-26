package com.programmalar.pdpgramm.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.adapters.AdapterCategoryViewPager
import com.programmalar.pdpgramm.databinding.FragmentChatsBinding
import com.programmalar.pdpgramm.databinding.ItemAlertLogutBinding
import com.programmalar.pdpgramm.databinding.ItemCategoryBinding
import com.programmalar.pdpgramm.models.Category
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : Fragment() {
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
    lateinit var fragmentChatsBinding: FragmentChatsBinding
    lateinit var root:View
    lateinit var adapterCategoryViewPager: AdapterCategoryViewPager
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firbaseDatabse:FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth:FirebaseAuth
    lateinit var listUser:ArrayList<User>
    private val referenceOnline = FirebaseDatabase.getInstance().getReference("isOnline")
    private val currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var listCategory:ArrayList<Category>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentChatsBinding = FragmentChatsBinding.inflate(inflater,container,false)
        root = fragmentChatsBinding.root
        auth = FirebaseAuth.getInstance()
        loadCategory()
        adapterCategoryViewPager = AdapterCategoryViewPager(listCategory,requireActivity())
        fragmentChatsBinding.viewPager.adapter = adapterCategoryViewPager
        listUser = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        firbaseDatabse = FirebaseDatabase.getInstance()
        reference = firbaseDatabse.getReference("users")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        fragmentChatsBinding.logout.setOnClickListener {
            var alertDialog = AlertDialog.Builder(root.context,R.style.BottomSheetDialogThem)
            val create = alertDialog.create()
            var itemAlertLogoutBinding = ItemAlertLogutBinding.inflate(LayoutInflater.from(root.context),null,false)
            create.setView(itemAlertLogoutBinding.root)

            itemAlertLogoutBinding.okBtn.setOnClickListener {
                googleSignInClient.signOut()
                firebaseAuth.signOut()
                create.dismiss()
                if (currentUser != null) {
                    referenceOnline.child(currentUser.uid).setValue(0)
                }
                findNavController().navigate(R.id.registrationFragment)
            }
            itemAlertLogoutBinding.noBtn.setOnClickListener {
                create.dismiss()
            }
            create.setCancelable(false)
            create.show()
        }
        reference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var listFilter = ArrayList<User>()
                val value = snapshot.children
                for (i in value){
                    val value1 = i.getValue(User::class.java)
                     if (value1!=null && value1.uid== firebaseAuth.currentUser!!.uid){
                         Picasso.get().load(value1.photoUrl).into(fragmentChatsBinding.userImage)
                         fragmentChatsBinding.nameUser.text = value1.displayName
                     }
                }
            }

        })



        TabLayoutMediator(fragmentChatsBinding.tablayout,fragmentChatsBinding.viewPager){ tab,position->
            tab.text = listCategory[position].nameCategory
        }.attach()
        statTab()
        fragmentChatsBinding.tablayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView
                val itemBinding = ItemCategoryBinding.bind(customView!!)
                itemBinding.container.setBackgroundResource(R.drawable.item_category1)
                itemBinding.text.setTextColor(Color.BLACK)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView
                val itemBinding = ItemCategoryBinding.bind(customView!!)
                itemBinding.container.setBackgroundResource(R.drawable.item)
                itemBinding.text.setTextColor(Color.WHITE)

            }

        })

        var calback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as AppCompatActivity).finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,calback)
        return root
    }

    private fun statTab() {
        val tabCount = fragmentChatsBinding.tablayout.tabCount
    for (i in 0 until tabCount){
        val inflate1 = ItemCategoryBinding.inflate(LayoutInflater.from(root.context), null, false)
        val tabAt = fragmentChatsBinding.tablayout.getTabAt(i)
        tabAt!!.customView = inflate1.root
        inflate1.text.text = listCategory[i].nameCategory
        if (i==0){
            inflate1.container.setBackgroundResource(R.drawable.item)
            inflate1.text.setTextColor(Color.WHITE)
        }else{
            inflate1.container.setBackgroundResource(R.drawable.item_category1)
            inflate1.text.setTextColor(Color.BLACK)
        }
    }
    }

    private fun loadCategory() {
        listCategory = ArrayList()
        listCategory.add(Category("Chats",0))
        listCategory.add(Category("Groups",1))
    }


    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            referenceOnline.child(currentUser.uid).setValue(1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentUser != null) {
            referenceOnline.child(currentUser.uid).setValue(0)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChatsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}