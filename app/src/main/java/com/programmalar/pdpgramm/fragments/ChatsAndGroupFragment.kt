package com.programmalar.pdpgramm.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentContainer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.adapters.AdapterGroups
import com.programmalar.pdpgramm.adapters.AdapterUsers
import com.programmalar.pdpgramm.databinding.AddGroupBinding
import com.programmalar.pdpgramm.databinding.FragmentChatsAndGroupBinding
import com.programmalar.pdpgramm.models.Group
import com.programmalar.pdpgramm.models.User
import java.lang.ref.PhantomReference
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsAndGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsAndGroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
        }
    }
    lateinit var fragmentChatsAndrGroupBinding: FragmentChatsAndGroupBinding
    lateinit var root:View
    lateinit var adapterGroups:AdapterGroups
    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var adapterUser:AdapterUsers
    lateinit var listUsers:ArrayList<User>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listGroups:ArrayList<Group>
    lateinit var listColors:ArrayList<String>
    lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     fragmentChatsAndrGroupBinding = FragmentChatsAndGroupBinding.inflate(inflater,container,false)
        root =fragmentChatsAndrGroupBinding.root
        firebaseAuth = FirebaseAuth.getInstance()
        listGroups = ArrayList()
        handler = Handler(Looper.getMainLooper())
        fireBaseDatabase = FirebaseDatabase.getInstance()
        listUsers = ArrayList()
        reference = fireBaseDatabase.getReference("users")

        var referenceGroup =fireBaseDatabase.getReference("groups")
        when(param2){
            0->{
                var currentUser = firebaseAuth.currentUser
                val email = currentUser!!.email
                val displayName = currentUser!!.displayName
                val photoUrl = currentUser!!.photoUrl
                val uid = currentUser!!.uid
                var user = User(email,displayName, photoUrl.toString(),uid,randomColor())
                reference.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var listFilter = ArrayList<User>()
                        val children = snapshot.children
                        listUsers.clear()
                        for (i in children){
                            val value = i.getValue(User::class.java)
                            if (value!=null && value.uid!=uid){
                                listUsers.add(value)
                            }
                            if (value!=null && value.uid==uid){
                                listFilter.add(value)
                            }
                        }
                        if (listFilter.isEmpty()){
                            reference.child(uid).setValue(user)
                        }
                        adapterUser = AdapterUsers(root.context,listUsers,object:AdapterUsers.OnItemClickLitener{
                            override fun onItemClick(user: User, position: Int) {
                                var bundle = Bundle()
                                bundle.putSerializable("user",user)
                                var navOptions = NavOptions.Builder()
                                navOptions.setEnterAnim(R.anim.enter)
                                navOptions.setExitAnim(R.anim.exite)
                                navOptions.setPopEnterAnim(R.anim.pop_enter)
                                navOptions.setPopExitAnim(R.anim.pop_exite)
                                findNavController().navigate(R.id.chatFragment,bundle,navOptions.build())
                            }
                        })
                        fragmentChatsAndrGroupBinding.rv.adapter = adapterUser
                        adapterUser.notifyDataSetChanged()
                    }

                })
            }
            1->{
                fragmentChatsAndrGroupBinding.buttonAdd.visibility = View.VISIBLE


                fragmentChatsAndrGroupBinding.buttonAdd.setOnClickListener {
                    var alertDialog = AlertDialog.Builder(root.context,R.style.BottomSheetDialogThem)
                    val create = alertDialog.create()
                    var addGroupBinding = AddGroupBinding.inflate(LayoutInflater.from(root.context),null,false)
                    create.setView(addGroupBinding.root)
                    addGroupBinding.save.setOnClickListener {
                        var key = referenceGroup.push().key!!
                        val name = addGroupBinding.nameGroup.text.toString()
                        val info = addGroupBinding.info.text.toString()
                        if(name.isNotBlank() && info.isNotBlank()) {
                            var group = Group(name, info, key, R.drawable.ic_teamwork)
                            referenceGroup.child("$key").setValue(group)
                            create.dismiss()
                        }else{
                            Toast.makeText(root.context, "Iltimos malumotlarni to`liq qiling", Toast.LENGTH_SHORT).show()
                        }
                    }
                    addGroupBinding.cancle.setOnClickListener {
                        create.dismiss()
                    }
                    create.setCancelable(false)
                    create.show()
                }

                referenceGroup.addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children
                        listGroups.clear()
                        for (child in children) {
                            var a = child.getValue(Group::class.java)
                            if (a != null)
                            listGroups.add(a)
                        }
                        adapterGroups.notifyDataSetChanged()
                    }

                })
                adapterGroups = AdapterGroups(listGroups,object:AdapterGroups.OnItemCklickListener{
                    override fun onItemClick(group: Group, position: Int) {
                        var bundle = Bundle()
                        bundle.putSerializable("group",group)
                        var navOptions = NavOptions.Builder()
                        navOptions.setEnterAnim(R.anim.enter)
                        navOptions.setExitAnim(R.anim.exite)
                        navOptions.setPopEnterAnim(R.anim.pop_enter)
                        navOptions.setPopExitAnim(R.anim.pop_exite)
                        findNavController().navigate(R.id.chatGroupFragment,bundle,navOptions.build())
                    }
                })
                fragmentChatsAndrGroupBinding.rv.adapter = adapterGroups
            }
        }

        return root
    }


    fun  randomColor(): String {
        listColors = ArrayList()
        listColors.add("#B61D1D")
        listColors.add("#EF281A")
        listColors.add("#757170")
        listColors.add("#6016E3")
        listColors.add("#3F51B5")
        listColors.add("#048BF6")
        listColors.add("#00BCD4")
        listColors.add("#009688")
        listColors.add("#4CAF50")
        listColors.add("#7FD619")
        listColors.add("#CDDC39")
        listColors.add("#ECD404")
        listColors.add("#FF9800")
        listColors.add("#FF5722")
        listColors.add("#D55F3A")
        listColors.add("#9C27B0")
        listColors.add("#465295")
        listColors.add("#60C3CF")
        var rendom = Random()
        val nextInt = rendom.nextInt(listColors.size)
        return listColors[nextInt]
    }
        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatsAndGroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
            ChatsAndGroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}