package com.submission.storyapp.presentation.core.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.databinding.StoryCardBinding
import com.submission.storyapp.utils.parseDate

class StoryAdapter(
    val onClick: (Story, StoryCardBinding) -> Unit
) : ListAdapter<Story, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(
        val binding: StoryCardBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story){ binding.apply {
            Glide.with(context).load(story.photoUrl).into(ivStory)
            tvTitle.text = story.name
            tvDate.text = story.createdAt.parseDate()
            tvDescription.text = story.description
        }}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val binding = StoryCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
        holder.itemView.setOnClickListener{
            onClick(story, holder.binding)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}
