package com.igluesmik.android_kotlin_practice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.igluesmik.android_kotlin_practice.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding

    val userAdapter = UserAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //https://api.github.com/users/4z7l

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        initView()


        return binding.root
    }

    private fun initView() {
        binding.recyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    val api= GithubApiClient.create(UserService::class.java).getUser(query)
                    api.enqueue(object : Callback<User> {
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            val list = listOf<User>(response.body()!!)
                            userAdapter.userList = list
                        }
                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Log.e("SEULGI", "getUserService onFailure"+t.localizedMessage)
                        }
                    })
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //false : default action , true : handled by my listener
                return false
            }
        })

    }

}