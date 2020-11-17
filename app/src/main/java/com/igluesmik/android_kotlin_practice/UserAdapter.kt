package com.igluesmik.android_kotlin_practice

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.igluesmik.android_kotlin_practice.databinding.ItemUserBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var _userList : MutableList<User> = mutableListOf()
    var userList: List<User> = _userList
        set(value) {
            _userList.clear()
            _userList.addAll(value)
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: User) {
            binding.user = user
            Glide.with(binding.root)
                .load(user.avatarUrl)
                .into(binding.imgAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val binding : ItemUserBinding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_userList[position])
    }

    override fun getItemCount(): Int = _userList.size
}