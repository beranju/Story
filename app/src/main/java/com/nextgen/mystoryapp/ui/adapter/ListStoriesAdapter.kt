package com.nextgen.mystoryapp.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.story.remote.dto.StoryItem
import com.nextgen.mystoryapp.databinding.StoriItemBinding
import com.nextgen.mystoryapp.infra.utils.DateFormatter
import com.nextgen.mystoryapp.ui.common.extention.loadImage
import com.nextgen.mystoryapp.ui.detail.DetailFragment
import java.util.*


class ListStoriesAdapter : PagingDataAdapter<StoryItem, ListStoriesAdapter.StoriesViewHolder>(
    DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val binding = StoriItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    class StoriesViewHolder(private val binding: StoriItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryItem?) {
            binding.tvName.text = item
                ?.name
            binding.tvCreatedAt.text =
                DateFormatter.formatDate(item?.createdAt.toString(), TimeZone.getDefault().id)
            binding.ivFoto.loadImage(item?.photoUrl.toString())
            if (item?.lat != null && item.lon != null) {
                binding.ivPinMap.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                val mBundle = Bundle()
                mBundle.putString(DetailFragment.ID_STORY, item?.id)
                val extras =
                    FragmentNavigatorExtras(binding.ivFoto to "photo", binding.tvName to "name")
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_detailFragment, mBundle, null, extras)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
