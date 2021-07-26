package com.programmalar.pdpgramm.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.adapters.MessageGroupAdapter
import com.programmalar.pdpgramm.databinding.DeleteMessgeBinding
import com.programmalar.pdpgramm.databinding.FragmentChatGroupBinding
import com.programmalar.pdpgramm.databinding.FromitemBinding
import com.programmalar.pdpgramm.databinding.ToitemBinding
import com.programmalar.pdpgramm.models.Group
import com.programmalar.pdpgramm.models.MessageGroup
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatGroupFragment : Fragment() {
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
    lateinit var fragmentChatGroupBinding:FragmentChatGroupBinding
    lateinit var root:View
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference_messageGroup:DatabaseReference
    lateinit var messageGroupAdapter:MessageGroupAdapter
    lateinit var reference: DatabaseReference
    lateinit var listMessage:ArrayList<MessageGroup>
      var isBoolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatGroupBinding = FragmentChatGroupBinding.inflate(inflater,container,false)
        root = fragmentChatGroupBinding.root
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        listMessage = ArrayList()
        var group = arguments?.getSerializable("group") as Group
        reference = firebaseDatabase.getReference("groups")
        reference_messageGroup = firebaseDatabase.getReference("group_messages")
        fragmentChatGroupBinding.cluseBtn.setOnClickListener {
            closeKerBoard()
            findNavController().popBackStack()
        }
        fragmentChatGroupBinding.userImage.setImageResource(R.drawable.ic_teamwork)
        fragmentChatGroupBinding.nameUser.text = group.groupName
        fragmentChatGroupBinding.send.setOnClickListener {
            val messageChat = fragmentChatGroupBinding.messageText.text.toString()
            if (messageChat.isNotBlank() && messageChat.isNotEmpty()){
                var key = reference_messageGroup.push().key
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val format = simpleDateFormat.format(Date())
                var messageGroup = MessageGroup(messageChat, firebaseAuth.currentUser!!.uid,format,group.key,
                    key!!
                )
                reference_messageGroup.child("${group.key}/${key}").setValue(messageGroup)
                fragmentChatGroupBinding.messageText.setText("")
            }else{
                Toast.makeText(root.context, "No message", Toast.LENGTH_SHORT).show()
            }
        }

        reference_messageGroup.child("${group.key}").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                listMessage.clear()
                for (child in children) {
                 listMessage.add(child.getValue(MessageGroup::class.java)!!)
                }
                fragmentChatGroupBinding.rvChat.smoothScrollToPosition(listMessage.size)
                messageGroupAdapter.notifyDataSetChanged()
            }

        })

        messageGroupAdapter = MessageGroupAdapter(root.context,listMessage,firebaseAuth.currentUser!!.uid,object:MessageGroupAdapter.FromMessageClick{
            override fun fromMessageMenu(
                group: MessageGroup,
                position: Int,
                item: FromitemBinding
            ) {
                var popupMenu = PopupMenu(root.context,item.cons1)
                popupMenu.inflate(R.menu.menu_item)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.copy->{
                            var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            var clip = ClipData.newPlainText("Text",group.message)
                            clipBoard.setPrimaryClip(clip)
                        }
                        R.id.copy_past->{
                            var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            var clip = ClipData.newPlainText("Text",group.message)
                            clipBoard.setPrimaryClip(clip)
                            fragmentChatGroupBinding.messageText.setText(group.message)
                        }
                        R.id.delete->{
                            var alertDialog =AlertDialog.Builder(root.context,R.style.BottomSheetDialogThem)
                            val create = alertDialog.create()
                            var deleteMessgeBinding = DeleteMessgeBinding.inflate(LayoutInflater.from(root.context),null,false)
                            deleteMessgeBinding.deleteCheck.visibility= View.GONE
                            deleteMessgeBinding.messageDeleteTo.visibility = View.VISIBLE
                            deleteMessgeBinding.messageDeleteTo.text = "Xabarni haqiqatdan o`chirmoqchimisiz"
                            deleteMessgeBinding.deleteBtn.setOnClickListener {
                                reference_messageGroup.child("${group.key}").addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val children = snapshot.children
                                        for (child in children) {
                                            if (child.child("message").value!!.equals(group.message) && child.child("keyMessage").value!!.equals(group.keyMessage)){
                                                child.ref.removeValue()
                                                messageGroupAdapter.notifyDataSetChanged()
                                                break
                                            }
                                        }
                                    }
                                })
                                create.dismiss()
                            }
                            deleteMessgeBinding.nodeleteBtn.setOnClickListener {
                                create.dismiss()
                            }
                            create.setView(deleteMessgeBinding.root)
                            create.setCancelable(false)
                            create.show()

                        }
                        R.id.edite->{
                            fragmentChatGroupBinding.messageText.setText(group.message)
                            fragmentChatGroupBinding.edite.visibility =View.VISIBLE
                            fragmentChatGroupBinding.send.visibility = View.INVISIBLE
                            isBoolean = true
                            fragmentChatGroupBinding.edite.setOnClickListener {
                                isBoolean = false
                                val messageGroup = fragmentChatGroupBinding.messageText.text.toString()
                                reference_messageGroup.child("${group.key}/${group.keyMessage}").setValue(MessageGroup(messageGroup,group.toUid,group.date,group.key,
                                    group.keyMessage!!
                                ))
                                fragmentChatGroupBinding.messageText.setText("")
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
                }catch (e: Exception){
                    Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
                }finally {
                    popupMenu.show()
                }
                popupMenu.show()
            }

        },object:MessageGroupAdapter.ToMessageClick{
            override fun ToMessageMenu(group: MessageGroup, position: Int, item: ToitemBinding) {
                var popupMenu = PopupMenu(root.context,item.cons1)
                popupMenu.inflate(R.menu.menu_to1)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.copy_to1->{
                            var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            var clip = ClipData.newPlainText("Text",group.message)
                            clipBoard.setPrimaryClip(clip)
                        }
                        R.id.copy1_to1->{
                            var clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            var clip = ClipData.newPlainText("Text",group.message)
                            clipBoard.setPrimaryClip(clip)
                            fragmentChatGroupBinding.messageText.setText(group.message)
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
        fragmentChatGroupBinding.rvChat.adapter = messageGroupAdapter

        if (isBoolean){
            fragmentChatGroupBinding.messageText.addTextChangedListener {
                fragmentChatGroupBinding.edite.visibility =View.VISIBLE
                fragmentChatGroupBinding.send.visibility = View.INVISIBLE
            }
        }else{
            fragmentChatGroupBinding.edite.visibility =View.INVISIBLE
            fragmentChatGroupBinding.send.visibility = View.VISIBLE
        }
        return root
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
         * @return A new instance of fragment ChatGroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatGroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}