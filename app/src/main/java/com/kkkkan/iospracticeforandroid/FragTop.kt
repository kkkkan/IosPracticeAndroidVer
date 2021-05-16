package com.kkkkan.iospracticeforandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kkkkan.iospracticeforandroid.api.MemoData
import com.kkkkan.iospracticeforandroid.api.getAllData
import com.kkkkan.iospracticeforandroid.databinding.FragTopBinding
import com.kkkkan.iospracticeforandroid.databinding.ViewCardItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class FragTop : Fragment() {
    companion object {
        fun getNewInstance(): FragTop {
            return FragTop()
        }
    }

    lateinit var binding: FragTopBinding
    val adapter = CardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate<FragTopBinding>(inflater, R.layout.frag_top, container, false)
        binding.memos.adapter = adapter
        binding.memos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        GlobalScope.launch {
            try {
                val response = getAllData().execute()
                if (response.isSuccessful) {
                    //return response.body()
                    GlobalScope.launch(Dispatchers.Main) {
                        adapter.setData(response.body())
                    }
                } else {
                    // failed
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return binding.root
    }


    inner class CardAdapter : RecyclerView.Adapter<CardViewHolder>() {
        private val memos: ArrayList<MemoData> = ArrayList()
        fun setData(memos: List<MemoData>?) {
            this.memos.clear()
            if (memos != null) {
                this.memos.addAll(memos)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return memos.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val binding = DataBindingUtil.inflate<ViewCardItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_card_item,
                parent,
                false
            )
            return CardViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.binding.contentsContainer.removeAllViews()
            val memo = memos[position]
            holder.binding.title.text = memo.title
            memo.contents.forEach {
                val textView = TextView(binding.root.context).apply {
                    text = it
                }
                holder.binding.contentsContainer.addView(textView)
            }
            holder.binding.executePendingBindings()
        }
    }


    class CardViewHolder(val binding: ViewCardItemBinding) : RecyclerView.ViewHolder(binding.root)
}