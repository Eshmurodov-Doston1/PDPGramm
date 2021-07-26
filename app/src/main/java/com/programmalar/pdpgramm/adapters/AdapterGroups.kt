package com.programmalar.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.programmalar.pdpgramm.databinding.ItemGroupBinding
import com.programmalar.pdpgramm.models.Group
import com.programmalar.pdpgramm.models.MessageGroup

class AdapterGroups(var list: List<Group>,var onItemClickLitener: OnItemCklickListener):RecyclerView.Adapter<AdapterGroups.Vh>() {
    inner class Vh(var itemGroupBinding: ItemGroupBinding):RecyclerView.ViewHolder(itemGroupBinding.root){
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var referense= firebaseDatabase.getReference("group_messages")

        fun onBind(group: Group,position: Int){
            var count = 0
            itemGroupBinding.userImage.setImageResource(group.groupImage!!)
            itemGroupBinding.nameUser.text = group.groupName
            itemView.setOnClickListener {
                onItemClickLitener.onItemClick(group,position)
            }
            referense.child("${list[position].key}").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var listMessagesGroup = ArrayList<MessageGroup>()
                    val children = snapshot.children
                    for (child in children) {
                        if (child!=null) {
                            listMessagesGroup.add(child.getValue(MessageGroup::class.java)!!)
                        }
                    }
                    for (messageGroup in listMessagesGroup) {
                        count++
                    }
                    if (listMessagesGroup.size>0){
                        val message_group = listMessagesGroup[listMessagesGroup.size-1].message
                        itemGroupBinding.message.text = message_group
                        val message = itemGroupBinding.message.text.toString()
                        if (message.isNotEmpty()){
                            itemGroupBinding.messageTime.visibility = View.VISIBLE
                        var minut = listMessagesGroup[listMessagesGroup.size-1].date!!.substring(listMessagesGroup[listMessagesGroup.size-1].date!!.length-2)
                        var time =  listMessagesGroup[listMessagesGroup.size-1].date!!.substring(listMessagesGroup[listMessagesGroup.size-1].date!!.length-5,listMessagesGroup[listMessagesGroup.size-1].date!!.length-3)
                        itemGroupBinding.messageTime.text ="$time:$minut"
                        }
                    }else{
                        itemGroupBinding.message.text="No message"
                        itemGroupBinding.messageTime.visibility = View.INVISIBLE
                    }

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemGroupBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position],position)
    }

    interface OnItemCklickListener{
        fun onItemClick(group: Group,position: Int)
    }
}