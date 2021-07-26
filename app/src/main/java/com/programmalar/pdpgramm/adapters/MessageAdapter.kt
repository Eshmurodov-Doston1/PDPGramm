package com.programmalar.pdpgramm.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.FromitemBinding
import com.programmalar.pdpgramm.databinding.ToitemBinding
import com.programmalar.pdpgramm.models.Message
import com.programmalar.pdpgramm.models.MessagesCount
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso

class MessageAdapter(var user: User, var listMessages:List<Message>, var uid:String, var fromMessageClick: FromMessageClick, var toMessageClick:ToMessageClick):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var firebaseAuth = FirebaseAuth.getInstance()
    var referenceOnline = FirebaseDatabase.getInstance().getReference("isOnline")
    var firebaseReference = FirebaseDatabase.getInstance().getReference("users")
    var firebaseDatabase = FirebaseDatabase.getInstance()
    inner class FromVh(var fromItemBinding:FromitemBinding):RecyclerView.ViewHolder(fromItemBinding.root){
        fun onBindFrom(message: Message,position: Int){
            fromItemBinding.messageFrom.text = message.message
            var firebaseAuth = FirebaseAuth.getInstance()
            var minut = message.date!!.substring( message.date!!.length-2)
            var time =  message.date!!.substring(message.date!!.length-5,message.date!!.length-3)
            fromItemBinding.time.text = "$time:$minut"
            fromItemBinding.date.text = message.date!!.substring(0,10)

          Picasso.get().load(firebaseAuth.currentUser!!.photoUrl).into(fromItemBinding.imageFrom)
            fromItemBinding.root.setOnLongClickListener {
                fromMessageClick.fromMessageMenu(message,position,fromItemBinding)
                true
            }
            firebaseReference.child("${firebaseAuth.currentUser!!.uid}").addValueEventListener(object:ValueEventListener{
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

        }
    }
    inner class ToVh(var toitemBinding: ToitemBinding):RecyclerView.ViewHolder(toitemBinding.root){
        fun onBindTo(message: Message,position: Int){
            toitemBinding.messageFrom.text = message.message
            var minut = message.date!!.substring( message.date!!.length-2)
            var time =  message.date!!.substring(message.date!!.length-5,message.date!!.length-3)
            toitemBinding.time.text = "$time:$minut"
            Picasso.get().load(user.photoUrl).into(toitemBinding.imageTo)
            toitemBinding.date.text = message.date!!.substring(0,10)
            toitemBinding.root.setOnLongClickListener {
                toMessageClick.ToMessageMenu(message,position,toitemBinding)
                true
            }
            var gradientDrawable = toitemBinding.cons1.background.mutate() as GradientDrawable
            gradientDrawable.setColor(Color.parseColor("${user.colorMessage}"))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (listMessages[position].fromUid==uid){
            return 1
        }
        return 2
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            return FromVh(FromitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ToitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun getItemCount(): Int {
        return listMessages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
             val fromVh = holder as FromVh
            fromVh.onBindFrom(listMessages[position],position)
        }else{
            val toVh = holder as ToVh
            toVh.onBindTo(listMessages[position],position)
        }
    }

    interface FromMessageClick{
        fun fromMessageMenu(message: Message,position: Int,item:FromitemBinding)
    }
    interface ToMessageClick{
        fun ToMessageMenu(message: Message,position: Int,item:ToitemBinding)
    }
}