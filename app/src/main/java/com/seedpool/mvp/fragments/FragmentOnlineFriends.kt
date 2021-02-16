package com.seedpool.mvp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.seedpool.mvp.HomeActivity
import com.seedpool.mvp.MessageActivity
import com.seedpool.mvp.R
import com.seedpool.mvp.models.Models
import com.seedpool.mvp.utils.FirebaseUtils
import com.seedpool.mvp.utils.utils
import com.seedpool.mvp.utils.visible
import kotlinx.android.synthetic.main.item_contact_layout.view.*
import kotlinx.android.synthetic.main.layout_recycler_view.view.*

class FragmentOnlineFriends : Fragment() {

    private var rootView:View? = null

    fun setOnlineListener() = object : HomeActivity.OnlineUsersLoaded{
        override fun onLoaded(users: List<Models.Contact>) {
            //setOnlineAdapter(users)
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_recycler_view,container, false)
        rootView = view

        activity?.let {

        ViewModelProviders.of(activity!!)[OnlineVM::class.java]
            .getOnlineUsers(context)
            .observe(it, Observer { contacts ->
                Log.d("FragmentOnlineFriends", "onCreateView: $contacts")
                setOnlineAdapter(contacts, view)
            })

    }

        return view


    }





    private fun setOnlineAdapter(onlineUsers: List<Models.Contact>, view:View){


        Log.e("FragmentOnlineFriends", "setOnlineAdapter: root view is null = ${view == null}, list size = ${onlineUsers.size}")


        view.recycler_back_message?.visible =onlineUsers.isEmpty()

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val name = itemView.name
            val pic = itemView.pic

        }

        view.recyclerView?.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
                return ViewHolder(layoutInflater.inflate(R.layout.item_layout_online,
                    p0, false))
            }

            override fun getItemCount(): Int = onlineUsers.size

            override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
                p0.name.text = utils.getNameFromNumber(context!!, onlineUsers[p1].number)
                FirebaseUtils.loadProfileThumbnail(context!!, onlineUsers[p1].uid, p0.pic)

                p0.itemView.setOnClickListener {
                    startActivity(
                        Intent(context, MessageActivity::class.java)
                            .apply {
                                putExtra(FirebaseUtils.KEY_UID, onlineUsers[p1].uid)
                                putExtra(utils.constants.KEY_TARGET_TYPE, FirebaseUtils.KEY_CONVERSATION_SINGLE)
                                putExtra(utils.constants.KEY_NAME_OR_NUMBER, onlineUsers[p1].number)
                            })
                }
            }

        }


    }
}