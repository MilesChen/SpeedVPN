package com.wind.vpn.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActMessageCenterBinding
import com.github.kr328.clash.databinding.ViewItemMessageBinding
import com.github.kr328.clash.design.util.layoutInflater
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.bean.NoticeBean
import com.wind.vpn.holder.BaseHolder
import com.wind.vpn.toJson
import com.wind.vpn.util.dp2px
import com.wind.vpn.util.goTargetClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageActivity:BaseActivity() {
    private lateinit var binding:ActMessageCenterBinding
    private lateinit var adapter: MessageAdapter
    override fun genCustomView(): View {
        binding =ActMessageCenterBinding.inflate(layoutInflater,findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.recyclerList.layoutManager = LinearLayoutManager(this)
        binding.recyclerList.addItemDecoration(object: ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.top = dp2px(10f)
            }
        })
        adapter = MessageAdapter(){
            val bundle = Bundle()
            bundle.putString(KEY_MESSAGE_DETAIL, it.toJson())
            goTargetClass(this, MessageDetailActivity::class.java, bundle)
        }
        binding.recyclerList.adapter = adapter
        loadMessage()
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_message_center
    }

    private fun loadMessage() {
        launch(Dispatchers.IO) {
            val result = WindApi.getNotice()
            var dataList:List<NoticeBean> = ArrayList<NoticeBean>()
            if (result.isSuccess && result.data != null) {
                dataList = result.data!!
            }
            withContext(Dispatchers.Main) {
                adapter.updateData(dataList)
                binding.noContent.visibility = if (dataList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }



    }

}

class MessageHolder(val binding: ViewItemMessageBinding, val itemClick:(noticeBean: NoticeBean) -> Unit): BaseHolder<NoticeBean>(binding.root) {
    override fun onHolderBind(t: NoticeBean) {
        binding.tvMessageContent.text = t.content
        binding.tvMessageTitle.text = t.title
        itemView.apply {
            setOnClickListener{
                itemClick(t)
            }
        }
    }

}

class MessageAdapter(private val itemClick:(noticeBean:NoticeBean) -> Unit):Adapter<MessageHolder>() {
    private var datas:List<NoticeBean> = ArrayList<NoticeBean>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val binding = ViewItemMessageBinding.inflate(parent.context.layoutInflater, parent, false)
        return MessageHolder(binding, itemClick)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.onHolderBind(datas[position])
    }

    fun updateData(newDatas:List<NoticeBean>) {
        datas = newDatas
        notifyDataSetChanged()
    }
}