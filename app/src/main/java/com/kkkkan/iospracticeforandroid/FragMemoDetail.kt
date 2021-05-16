package com.kkkkan.iospracticeforandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kkkkan.iospracticeforandroid.api.MemoData
import com.kkkkan.iospracticeforandroid.databinding.FragMemoDetailBinding
import com.kkkkan.iospracticeforandroid.databinding.ViewMemoDetailItemBinding

class FragMemoDetail : Fragment() {
    companion object {
        private const val keyMemo = "key_memo"
        fun getNewInstance(memo: MemoData): FragMemoDetail {
            val f = FragMemoDetail()
            f.arguments = Bundle().apply {
                putSerializable(keyMemo, memo)
            }
            return f
        }
    }

    lateinit var binding: FragMemoDetailBinding
    private val adapter = MemoItemAdapter()

    private fun getMemo(): MemoData {
        return arguments!!.getSerializable(keyMemo) as MemoData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragMemoDetailBinding>(
            inflater,
            R.layout.frag_memo_detail,
            container,
            false
        )
        val memo = getMemo()
        binding.title.text = memo.title
        binding.contentList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.contentList.adapter = adapter
        // RecyclerViewのアイテム間に仕切り線をつける
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.contentList.addItemDecoration(dividerItemDecoration)
        adapter.setData(memo.contents)
        return binding.root
    }

    private inner class MemoItemAdapter : RecyclerView.Adapter<MemoItemViewHolder>() {
        private val contents: ArrayList<String> = ArrayList()

        fun setData(contents: List<String>) {
            this.contents.clear()
            this.contents.addAll(contents)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoItemViewHolder {
            val binding = DataBindingUtil.inflate<ViewMemoDetailItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_memo_detail_item,
                parent,
                false
            )
            return MemoItemViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return contents.size
        }

        override fun onBindViewHolder(holder: MemoItemViewHolder, position: Int) {
            holder.binding.content.setText(contents[position])
            holder.binding.executePendingBindings() // DataBindingに中身の変更をお知らせ
        }

    }


    data class MemoItemViewHolder(val binding: ViewMemoDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}