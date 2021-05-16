package com.kkkkan.iospracticeforandroid

import android.os.Bundle
import android.transition.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.MainThread
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

    private lateinit var binding: FragTopBinding
    private val adapter = CardAdapter()

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

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        refreshData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }


    override fun onPause() {
        super.onPause()
        if (binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    /**
     * 新たにデータを取得しに行く。
     * 終了後、更新中のクルクルが出ていたら消す。
     */
    @MainThread
    private fun refreshData() {
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
            } finally {
                GlobalScope.launch(Dispatchers.Main) {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }


    private inner class CardAdapter : RecyclerView.Adapter<CardViewHolder>() {
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
                    textSize = 16f // setTextSizeはsp単位
                    setPadding(
                        0,
                        4 * context.resources.displayMetrics.density.toInt(),
                        0,
                        4 * context.resources.displayMetrics.density.toInt()
                    )

                }
                holder.binding.contentsContainer.addView(textView)
            }
            holder.binding.root.setOnClickListener {
                val enterTs = TransitionSet()
                enterTs.ordering = TransitionSet.ORDERING_TOGETHER
                enterTs.addTransition(ChangeBounds())
                val returnTs = TransitionSet()
                enterTs.ordering = TransitionSet.ORDERING_TOGETHER
                enterTs.addTransition(Fade())
                val transition = TransitionSet().apply {
                    addTransition(ChangeBounds())
                    addTransition(ChangeTransform())
                    addTransition(ChangeImageTransform())
                }
                holder.binding.root.transitionName = "sharedName"
                holder.binding.title.transitionName = "title"
                holder.binding.contentsContainer.transitionName = "contents"
                fragmentManager?.beginTransaction()?.apply {
                    val f = FragMemoDetail.getNewInstance(memo, "sharedName")
                    f.sharedElementEnterTransition = transition
                    f.sharedElementReturnTransition = transition
                    addSharedElement(holder.binding.root, "sharedName")
//                    addSharedElement(holder.binding.title,"title")
//                    addSharedElement(holder.binding.contentsContainer,"contents")
                    setReorderingAllowed(true)
                    replace(R.id.fragment_container, f)
                    addToBackStack(null)
                    commit()
                }
            }
            holder.binding.executePendingBindings()
        }
    }


    class CardViewHolder(val binding: ViewCardItemBinding) : RecyclerView.ViewHolder(binding.root)
}