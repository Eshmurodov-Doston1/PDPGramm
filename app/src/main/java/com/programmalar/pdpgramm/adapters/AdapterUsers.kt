package com.programmalar.pdpgramm.adapters

import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.RvItemBinding
import com.programmalar.pdpgramm.models.Message
import com.programmalar.pdpgramm.models.MessageGroup
import com.programmalar.pdpgramm.models.MessagesCount
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso
import java.util.logging.Handler
import kotlin.math.abs

class AdapterUsers(var context: Context,var list: List<User>,var onItemClickLitener: OnItemClickLitener):
    RecyclerView.Adapter<AdapterUsers.Vh>(){
    inner class Vh(var rvItemBinding: RvItemBinding):RecyclerView.ViewHolder(rvItemBinding.root){
        var listMessages = ArrayList<Message>()
        var referenceOnline = FirebaseDatabase.getInstance().getReference("isOnline")
        var firebaseAuth = FirebaseAuth.getInstance()
        var reference = FirebaseDatabase.getInstance().getReference("users")
        var firebaseDatabase = FirebaseDatabase.getInstance().getReference("messages_count")
        fun onBind(user: User,position: Int){
            var user_count:Int?=null
            var from_count= 0
            rvItemBinding.nameUser.text = user.displayName
            Picasso.get().load(user.photoUrl).into(rvItemBinding.userImage)
            itemView.setOnClickListener {
                onItemClickLitener.onItemClick(user,position)
                rvItemBinding.messageFrom.text = "0"
                rvItemBinding.card.visibility = View.INVISIBLE
            }

            referenceOnline.child(user.uid!!).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val n = snapshot.getValue(Int::class.java)
                    if (n == 1) {
                        rvItemBinding.online.setBackgroundResource(R.drawable.circle)


                    } else {
                        rvItemBinding.online.setBackgroundResource(R.drawable.no_online)

                    }
                }

            })

            reference.child("${user.uid}/messages/${firebaseAuth.currentUser!!.uid}").addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                   listMessages.clear()
                    var listMessages1 = ArrayList<Message>()
                    var count = 0
                    for (child in children) {
                        if (child!=null) {
                            listMessages.add(child.getValue(Message::class.java)!!)
                        }
                        count++
                    }
                    if (listMessages.isNotEmpty()) {
                        rvItemBinding.message.text = listMessages[listMessages.size - 1].message
                        val message = rvItemBinding.message.text.toString()
                        if (message.isNotEmpty()){
                            rvItemBinding.messageTime.visibility = View.VISIBLE
                            var minut = listMessages[count-1].date!!.substring(listMessages[count-1].date!!.length-2)
                            var time =  listMessages[count-1].date!!.substring(listMessages[count-1].date!!.length-5,listMessages[count-1].date!!.length-3)
                            rvItemBinding.messageTime.text ="$time:$minut"
                        }
                    }
                }

            })
            firebaseDatabase.child("${firebaseAuth.currentUser!!.uid}/${user.uid}").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value1 = snapshot.getValue(MessagesCount::class.java)
                    firebaseDatabase.child("${user.uid}/${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }
                        override fun onDataChange(snapshot1: DataSnapshot) {
                            val value = snapshot1.getValue(MessagesCount::class.java)
                            if ((value!=null && value1!=null) && (value1.destroyCount!! >=0 && value.destroyCount!! >=0 )) {
                                if (value1.destroyCount!! >= value.destroyCount!!) {
                                    rvItemBinding.card.visibility = View.INVISIBLE
                                    rvItemBinding.messageFrom.text = "0"
                                } else {
                                    var count = abs(value.destroyCount!! - value1.destroyCount!!)
                                    rvItemBinding.card.visibility = View.VISIBLE
                                    rvItemBinding.messageFrom.text = "$count"
                                }
                            }else if (value1==null && value!=null && value.destroyCount!! >=0){
                                if (value.destroyCount==0){
                                    rvItemBinding.card.visibility = View.INVISIBLE
                                    rvItemBinding.messageFrom.text = "${value.destroyCount}"
                                }else{
                                    rvItemBinding.card.visibility = View.VISIBLE
                                    rvItemBinding.messageFrom.text = "${value.destroyCount}"
                                }
                            }
                        }

                    })
                }

            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
            return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
            return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
            holder.onBind(list[position],position)
    }
    interface OnItemClickLitener{
        fun onItemClick(user: User,position: Int)
    }
}