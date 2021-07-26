package com.programmalar.pdpgramm.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.adapters.MessageAdapter
import com.programmalar.pdpgramm.databinding.DeleteMessgeBinding
import com.programmalar.pdpgramm.databinding.FragmentChatBinding
import com.programmalar.pdpgramm.databinding.FromitemBinding
import com.programmalar.pdpgramm.databinding.ToitemBinding
import com.programmalar.pdpgramm.models.Message
import com.programmalar.pdpgramm.models.MessagesCount
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
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
    lateinit var fragmentChatBinding:FragmentChatBinding
    lateinit var root:View
    lateinit var firebaseAuth: FirebaseAuth
    var countChat=0
    var isBoolean:Boolean=false
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var listMessage:ArrayList<Message>
    lateinit var messageAdapter: MessageAdapter
    lateinit var user:User
    @SuppressLint("SimpleDateFormat", "WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatBinding = FragmentChatBinding.inflate(inflater,container,false)
        root = fragmentChatBinding.root
        firebaseAuth = FirebaseAuth.getInstance()
        listMessage = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        var referensOnline = firebaseDatabase.getReference("isOnline")
        reference = firebaseDatabase.getReference("users")
         user = arguments?.getSerializable("user") as User
        Picasso.get().load(user.photoUrl).into(fragmentChatBinding.userImage)
        fragmentChatBinding.nameUser.text = user.displayName
        fragmentChatBinding.cons1.setOnClickListener {
           var bundle = Bundle()
           bundle.putSerializable("user",user)
           bundle.putInt("countChat",countChat)
           var navOptions = NavOptions.Builder()
           navOptions.setEnterAnim(R.anim.enter)
           navOptions.setExitAnim(R.anim.exite)
           navOptions.setPopEnterAnim(R.anim.pop_enter)
           navOptions.setPopExitAnim(R.anim.pop_exite)
           findNavController().navigate(R.id.userAccountFragment,bundle,navOptions.build())
       }

        referensOnline.child(user.uid!!).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val n = snapshot.getValue(Int::class.java)
              if (n==1){
                  fragmentChatBinding.message.text = "online"
              }else{
                  fragmentChatBinding.message.text = "last seen recently"
              }
            }

        })



        fragmentChatBinding.cluseBtn.setOnClickListener {
            closeKerBoard()
            findNavController().popBackStack()
        }
        fragmentChatBinding.send.setOnClickListener {
            val messageChat = fragmentChatBinding.messageText.text.toString()
            if (messageChat.isNotBlank() && messageChat.isNotEmpty()) {
                //linearLayoutManager.stackFromEnd=true
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val format = simpleDateFormat.format(Date())
                var key = reference.push().key!!
                var message = Message(
                    messageChat,
                    format,
                    firebaseAuth.currentUser!!.uid,
                    user.uid,
                    key
                )
                reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}/$key")
                    .setValue(message)
                reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}/$key").setValue(message)

                if (listMessage.size==0){
                    var messageCount = MessagesCount(listMessage.size+1)
                    firebaseDatabase.getReference("messages_count").child("${firebaseAuth.currentUser!!.uid}/${user.uid}").setValue(messageCount)
                }else {
                    var messageCount = MessagesCount(listMessage.size+1)
                    firebaseDatabase.getReference("messages_count").child("${firebaseAuth.currentUser!!.uid}/${user.uid}").setValue(messageCount)
                }


                fragmentChatBinding.messageText.setText("")
            } else {
                Toast.makeText(root.context, "No Message", Toast.LENGTH_SHORT).show()
            }


        }

        reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                listMessage.clear()
                for (child in children) {
                    listMessage.add(child.getValue(Message::class.java)!!)
                }
                for (message in listMessage) {
                    countChat++
                }

                messageAdapter = MessageAdapter(user,listMessage, firebaseAuth.currentUser!!.uid,object:MessageAdapter.FromMessageClick{
//                                reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}/${message.toUid}").removeValue()
//                                reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}/${message.key}").removeValue() user delete
                    override fun fromMessageMenu(
                        message: Message,
                        position: Int,
                        item: FromitemBinding
                    ) {
                        var popupMenu = PopupMenu(root.context,item.cons1)
                        popupMenu.inflate(R.menu.menu_item)
                        popupMenu.setOnMenuItemClickListener {
                            when(it.itemId){
                                R.id.copy->{
                                    var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    var clip = ClipData.newPlainText("Text",message.message)
                                    clipBoard.setPrimaryClip(clip)
                                }
                                R.id.copy_past->{
                                    var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    var clip = ClipData.newPlainText("Text",message.message)
                                    clipBoard.setPrimaryClip(clip)
                                    fragmentChatBinding.messageText.setText(message.message)
                                }
                                R.id.delete->{
                                    var alertDialog = AlertDialog.Builder(root.context,R.style.BottomSheetDialogThem)
                                    val create = alertDialog.create()
                                    var deleteMessgeBinding = DeleteMessgeBinding.inflate(
                                        LayoutInflater.from(root.context),null,false)
                                    deleteMessgeBinding.deleteCheck.text = "${user.displayName} dan ham o`chsinmi"
                                    deleteMessgeBinding.deleteBtn.setOnClickListener {
                                        if (deleteMessgeBinding.deleteCheck.isChecked){
                                            val uid = firebaseAuth.currentUser!!.uid
                                            reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(uid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                            reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(message.fromUid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                        }else{
                                            val uid = firebaseAuth.currentUser!!.uid
                                            reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(uid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                        }
                                        create.dismiss()
                                    }
                                    deleteMessgeBinding.nodeleteBtn.setOnClickListener {
                                        create.dismiss()
                                    }
                                    create.setView(deleteMessgeBinding.root)
                                    create.show()
                                }
                                R.id.edite->{
                                    fragmentChatBinding.messageText.setText(message.message)
                                    val uid = firebaseAuth.currentUser!!.uid
                                    fragmentChatBinding.edite.visibility =View.VISIBLE
                                    fragmentChatBinding.send.visibility = View.INVISIBLE
                                    isBoolean = true
                                    fragmentChatBinding.edite.setOnClickListener {
                                        fragmentChatBinding.edite.visibility =View.INVISIBLE
                                        fragmentChatBinding.send.visibility = View.VISIBLE
                                        isBoolean=false
                                        var message_m = fragmentChatBinding.messageText.text.toString()
                                        reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}/${message.key}").setValue(Message(message_m,message.date,message.fromUid,message.toUid,
                                            message.key!!
                                        ))
                                        reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}/${message.key}").setValue(Message(message_m,message.date,message.fromUid,message.toUid,
                                            message.key!!
                                        ))
                                        fragmentChatBinding.messageText.setText("")
                                    }
                                }
                            }
                            true
                        }
                        try {
                            var fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                            fieldMPopup.isAccessible = true
                            var mPopup  = fieldMPopup.get(popupMenu)
                            mPopup.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                                .invoke(mPopup, true)
                        }catch (e:Exception){
                            Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
                        }finally {
                            popupMenu.show()
                        }
                        popupMenu.show()
                    }
                },
                    object:MessageAdapter.ToMessageClick{
                    override fun ToMessageMenu(
                        message: Message,
                        position: Int,
                        item: ToitemBinding
                    ) {
                        var popupMenu = PopupMenu(root.context,item.cons1)
                        popupMenu.inflate(R.menu.menu_to)
                        popupMenu.setOnMenuItemClickListener {
                            when(it.itemId){
                                R.id.copy_->{
                                    var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    var clip = ClipData.newPlainText("Text",message.message)
                                    clipBoard.setPrimaryClip(clip)
                                }
                                R.id.copy12->{
                                    var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    var clip = ClipData.newPlainText("Text",message.message)
                                    clipBoard.setPrimaryClip(clip)
                                    fragmentChatBinding.messageText.setText(message.message)
                                }
                                R.id.delete1->{
                                    var alertDialog = AlertDialog.Builder(root.context,R.style.BottomSheetDialogThem)
                                    val create = alertDialog.create()
                                    var deleteMessgeBinding = DeleteMessgeBinding.inflate(
                                        LayoutInflater.from(root.context),null,false)
                                    deleteMessgeBinding.deleteCheck.visibility = View.INVISIBLE
                                    deleteMessgeBinding.messageDeleteTo.visibility = View.VISIBLE
                                    deleteMessgeBinding.messageDeleteTo.text = "Malumot faqat sizdan o`chadi"
                                    deleteMessgeBinding.deleteBtn.setOnClickListener {
                                        if (deleteMessgeBinding.deleteCheck.isChecked){
                                            val uid = firebaseAuth.currentUser!!.uid
                                            reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(uid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                            reference.child("${firebaseAuth.currentUser!!.uid}/messages/${user.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(message.fromUid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                        }else{
                                            val uid = firebaseAuth.currentUser!!.uid
                                            reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children1 = snapshot.children
                                                    for (dataSnapshot in children1) {
                                                        if (dataSnapshot.child("fromUid").getValue()!!.equals(uid) && dataSnapshot.child("message").getValue()!!
                                                                .equals(message.message) && dataSnapshot.child("key").getValue()!!.equals(message.key)){
                                                            dataSnapshot.ref.removeValue()
                                                            messageAdapter.notifyDataSetChanged()
                                                            break
                                                        }
                                                    }
                                                }

                                            })
                                        }
                                        create.dismiss()
                                    }
                                    deleteMessgeBinding.nodeleteBtn.setOnClickListener {
                                        create.dismiss()
                                    }
                                    create.setView(deleteMessgeBinding.root)
                                    create.show()
                                }
                            }
                            true
                        }
                        try {
                            var fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                            fieldMPopup.isAccessible = true
                            var mPopup  = fieldMPopup.get(popupMenu)
                            mPopup.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                                .invoke(mPopup, true)
                        }catch (e:Exception){
                            Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
                        }finally {
                            popupMenu.show()
                        }
                        popupMenu.show()

                    }
                })
                fragmentChatBinding.rvChat.adapter = messageAdapter
                messageAdapter.notifyDataSetChanged()
                fragmentChatBinding.rvChat.smoothScrollToPosition(messageAdapter.itemCount)
            }

        })


        if (isBoolean){
            fragmentChatBinding.messageText.addTextChangedListener {
                fragmentChatBinding.edite.visibility =View.VISIBLE
                fragmentChatBinding.send.visibility = View.INVISIBLE
            }
        }else{
            fragmentChatBinding.edite.visibility =View.INVISIBLE
            fragmentChatBinding.send.visibility = View.VISIBLE
        }
        var calback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,calback)
        return root
    }


    override fun onStop() {
        super.onStop()
        if (listMessage.size>=1) {
            var messageCount = MessagesCount(listMessage.size)
            firebaseDatabase.getReference("messages_count")
                .child("${firebaseAuth.currentUser!!.uid}/${user.uid}").setValue(messageCount)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (listMessage.size>=1) {
            var messageCount = MessagesCount(listMessage.size)
            firebaseDatabase.getReference("messages_count")
                .child("${firebaseAuth.currentUser!!.uid}/${user.uid}").setValue(messageCount)
        }
    }

    override fun onPause() {
        super.onPause()
//        var messageCount = MessagesCount(listMessage.size)
//        firebaseDatabase.getReference("messages_count").child("${firebaseAuth.currentUser!!.uid}/${user.uid}").setValue(messageCount)
    }
    private fun closeKerBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}