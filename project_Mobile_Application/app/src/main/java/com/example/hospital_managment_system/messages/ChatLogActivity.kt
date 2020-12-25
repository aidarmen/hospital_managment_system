package com.example.hospital_managment_system.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.SearchForDoctorsFragment
import com.example.hospital_managment_system.models.ChatMessage
import com.example.hospital_managment_system.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.edittext_chat_log
import kotlinx.android.synthetic.main.activity_chat_log.recyclerview_chat_log
import kotlinx.android.synthetic.main.activity_chat_log.send_button_chat_log
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_message.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_message)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(SearchForDoctorsFragment.USER_KEY)


        supportActionBar?.title = toUser!!.username
        message_name.text = toUser!!.username


//        //avatar image profile
//        Picasso.get().load(url).into(profile_image_chat)


        //setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
    }

    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                var seconds = chatMessage!!.timestamp.toLong()

                val d = Date(seconds * 1000)
                val df: DateFormat = SimpleDateFormat("dd/MMM  hh:mm:ss")
                var time = df.format(d).toString()
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)


                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = SearchForDoctorsFragment.currentUser
                        adapter.add(ChatToItem(chatMessage.text, currentUser!!,time))
                    } else {

                        adapter.add(ChatFromItem(chatMessage.text, toUser!!,time))

                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {


            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (text.trim().isEmpty() || text.isNullOrBlank()){

        }
        else{
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<User>(SearchForDoctorsFragment.USER_KEY)
            val toId = user!!.uid

            if (fromId == null) return

            val reference = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId").push()
            val toReference = FirebaseDatabase.getInstance().getReference("/user-message/$toId/$fromId").push()

            val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }

            toReference.setValue(chatMessage)
        }
    }

//    private fun setupDummyData() {
//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
//        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
//        adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
//        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
//        adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
//        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
//
//        recyclerview_chat_log.adapter = adapter
//    }
}

class ChatFromItem(val text: String, val user: User,val time: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.from_message_time.text = time ?: "time not known"
       //load image
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)


    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User,val time: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.to_message_time.text = time ?: "time not known"
        //load image
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
