/*
 * Copyright (c) Mya Than Htet 2021.
 */

package com.mth.assistantmya.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mth.assistantmya.R
import com.mth.assistantmya.model.ChatModel


class ChatRecyclerAdapter(
    private val messageModelModalArrayList: ArrayList<ChatModel>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.user_msg, parent, false)
                return UserViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.bot_msg, parent, false)
                return BotViewHolder(view)
            }
        }
        return null!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val modal: ChatModel = messageModelModalArrayList[position]
        when (modal.sender) {
            "user" ->
                (holder as UserViewHolder).userTV.setText(modal.message)
            "bot" ->
                (holder as BotViewHolder).botTV.setText(modal.message)
        }
    }

    override fun getItemCount(): Int {
        return messageModelModalArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageModelModalArrayList[position].sender) {
            "user" -> 0
            "bot" -> 1
            else -> -1
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userTV: TextView = itemView.findViewById(R.id.idTVUser)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var botTV: TextView = itemView.findViewById(R.id.idTVBot)

    }


}
