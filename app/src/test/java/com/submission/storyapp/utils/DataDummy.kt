package com.submission.storyapp.utils

import com.submission.storyapp.domain.models.Story

object DataDummy {
    fun generateDummyStoriesResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                i.toString(),
                "photoUrl $i",
                "createdAt $i",
                "name $i",
                "desc $i",
                i + 1.0,
                i - 1.0
            )
            items.add(story)
        }
        return items
    }
}
