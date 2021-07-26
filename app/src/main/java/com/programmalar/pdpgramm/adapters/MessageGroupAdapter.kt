package com.programmalar.pdpgramm.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.FromitemBinding
import com.programmalar.pdpgramm.databinding.ToitemBinding
import com.programmalar.pdpgramm.models.Message
import com.programmalar.pdpgramm.models.MessageGroup
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso

class MessageGroupAdapter(var context: Context,var list: List<MessageGroup>,var uid:String,var fromMessageClick: FromMessageClick,var toMessageClick: ToMessageClick):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var firebaseAuth = FirebaseAuth.getInstance()
    var firbaseReference = FirebaseDatabase.getInstance().getReference("users")
    inner class fromVh(var fromItemBinding:FromitemBinding):RecyclerView.ViewHolder(fromItemBinding.root){
        fun onBindFrom(messageGroup: MessageGroup,position: Int){
            fromItemBinding.messageFrom.text = messageGroup.message
            var firebaseAuth = FirebaseAuth.getInstance()
            var minut = messageGroup.date!!.substring( messageGroup.date!!.length-2)
            var time =  messageGroup.date!!.substring(messageGroup.date!!.length-5,messageGroup.date!!.length-3)
            fromItemBinding.time.text = "$time:$minut"
            firbaseReference.child("${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("colorMessage").value
                  //  fromItemBinding.cons1.setBackgroundColor(Color.parseColor(value.toString()))
                 var gradientDrawable = fromItemBinding.cons1.background.mutate() as GradientDrawable
                    gradientDrawable.setColor(Color.parseColor(value.toString()))
                }

            })
            //
            fromItemBinding.date.text = messageGroup.date!!.substring(0,10)
            Picasso.get().load(firebaseAuth.currentUser!!.photoUrl).into(fromItemBinding.imageFrom)
            fromItemBinding.root.setOnLongClickListener {
                fromMessageClick.fromMessageMenu(messageGroup,position,fromItemBinding)
                true
            }
        }
    }
    inner class toVh(var toitemBinding: ToitemBinding):RecyclerView.ViewHolder(toitemBinding.root){
        fun onBindTo(messageGroup: MessageGroup,position: Int){
            toitemBinding.messageFrom.text = messageGroup.message
            var minut = messageGroup.date!!.substring( messageGroup.date!!.length-2)
            var time =  messageGroup.date!!.substring(messageGroup.date!!.length-5,messageGroup.date!!.length-3)
            toitemBinding.time.text = "$time:$minut"
            toitemBinding.date.text = messageGroup.date!!.substring(0,10)
            toitemBinding.root.setOnLongClickListener {
                toMessageClick.ToMessageMenu(messageGroup,position,toitemBinding)
                true
            }
            firbaseReference.child("${messageGroup.toUid}").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(User::class.java)
                    Picasso.get().load(value!!.photoUrl).into(toitemBinding.imageTo)
                }

            })
            firbaseReference.child("${messageGroup.toUid}").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("colorMessage").value
                    //  fromItemBinding.cons1.setBackgroundColor(Color.parseColor(value.toString()))
                    var gradientDrawable = toitemBinding.cons1.background.mutate() as GradientDrawable
                    gradientDrawable.setColor(Color.parseColor(value.toString()))
                }

            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            return fromVh(FromitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return toVh(ToitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
            val fromVh = holder as fromVh
            fromVh.onBindFrom(list[position],position)
        }else{
            val toVh = holder as toVh
            toVh.onBindTo(list[position],position)
        }
    }

    override fun getItemViewType(position: Int): Int {
     if (list[position].toUid== uid){
         return 1
     }
        return 0
    }
    interface FromMessageClick{
        fun fromMessageMenu(group: MessageGroup, position: Int,item:FromitemBinding)
    }
    interface ToMessageClick{
        fun ToMessageMenu(group: MessageGroup, position: Int,item:ToitemBinding)
    }
}