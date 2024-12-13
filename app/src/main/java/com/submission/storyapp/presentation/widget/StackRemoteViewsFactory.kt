package com.submission.storyapp.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.lifecycle.asFlow
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.submission.storyapp.R
import com.submission.storyapp.di.WidgetEntryPoint
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class StackRemoteViewsFactory (
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {
    private var widgetItems = listOf<Story>()

    private val storyUseCases: StoryUseCases by lazy {
        EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java).storyUseCases()
    }

    // Use supervisor job so that when coroutine failed, it doesn't interrupt other coroutines
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() { coroutineScope.launch {
        storyUseCases.getStoriesWithLocation().asFlow().collect{
            when (it) {
                is ResponseWrapper.Success -> {
                    Log.e("StackRemoteViewsFactory", "Success loading stories: ${it.data.size}")
                    updateItems(it.data)
                }
                is ResponseWrapper.Error -> {
                    Log.e("StackRemoteViewsFactory", "Error loading stories: ${it.error}")
                }
                ResponseWrapper.Loading -> {
                    Log.d("StackRemoteViewsFactory", "Loading stories...")
                }
            }
        }
    }}

    @Suppress("Deprecation")
    private fun updateItems(newStories: List<Story>) {
        widgetItems = newStories
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(
            AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, StoryWidget::class.java)
            ), R.id.stack_view
        )
    }

    override fun onDataSetChanged() {  }

    override fun onDestroy() {
        coroutineScope.cancel()
    }

    override fun getCount(): Int = widgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val story = widgetItems[position]

        val views = RemoteViews(context.packageName, R.layout.widget_item)

        val storyPhotoBitmap = Glide
            .with(context)
            .asBitmap()
            .apply(RequestOptions().override(200, 200))
            .load(story.photoUrl)
            .submit()
            .get()

        views.setImageViewBitmap(R.id.story_image, storyPhotoBitmap)

        val extras = bundleOf(StoryWidget.EXTRA_ITEM to story.name)

        views.setOnClickFillInIntent(
            R.id.story_image,
            Intent().apply { putExtras(extras) }
        )

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = true
}
