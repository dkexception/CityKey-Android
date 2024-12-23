package com.telekom.citykey.view.widget.news.news_list

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.telekom.citykey.R
import com.telekom.citykey.domain.widget.WidgetInteractor
import com.telekom.citykey.models.content.CityContent
import com.telekom.citykey.utils.NetworkConnection
import com.telekom.citykey.utils.extensions.toDateString
import com.telekom.citykey.view.widget.news.news_list.NewsListWidget.Companion.ITEM_CORNER_RADIUS
import com.telekom.citykey.view.widget.news.news_list.NewsListWidget.Companion.NEWS_IMAGE_SIZE
import org.koin.android.ext.android.inject

class NewsListWidgetRemoteViewService : RemoteViewsService() {

    private val widgetInteractor: WidgetInteractor by inject()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        NewsListViewsFactory(applicationContext, widgetInteractor.newsList)

    inner class NewsListViewsFactory(private val context: Context, private val newsList: List<CityContent>) :
        RemoteViewsFactory {

        private val newsImageGlideRequestOptions: RequestOptions by lazy {
            RequestOptions()
                .override(NEWS_IMAGE_SIZE, NEWS_IMAGE_SIZE)
                .transform(RoundedCorners(ITEM_CORNER_RADIUS))
                .placeholder(R.drawable.ic_news_widget_empty_image_placeholder)
                .error(R.drawable.ic_news_widget_empty_image_placeholder)
        }

        override fun onCreate() {}

        override fun getCount(): Int = newsList.size

        override fun hasStableIds(): Boolean = true

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewAt(position: Int): RemoteViews {
            val view = RemoteViews(context.packageName, R.layout.news_list_widget_item)
            val newsItem = newsList[position]
            populateNewsData(view, newsItem)

            val fillIntent = Intent().putExtra("newsItem", newsItem)
            view.setOnClickFillInIntent(R.id.newsItemContainer, fillIntent)
            return view
        }

        override fun onDataSetChanged() {
            if (NetworkConnection.checkInternetConnection(context))
                widgetInteractor.getNewsForCurrentCity(isSingleItemWidget = false)
        }

        override fun onDestroy() {
            widgetInteractor.clearNewsList()
        }

        private fun populateNewsData(view: RemoteViews, cityContent: CityContent) {
            view.setTextViewText(R.id.description, cityContent.contentTeaser)
            view.setTextViewText(R.id.timeStamp, cityContent.contentCreationDate.toDateString())
            view.setContentDescription(R.id.description, cityContent.contentTeaser)
            cityContent.thumbnail?.let { loadNewsImage(view, it) }
        }

        private fun loadNewsImage(view: RemoteViews, thumbnail: String) {
            try {
                val bitmap: Bitmap = Glide.with(context)
                    .asBitmap()
                    .load(thumbnail)
                    .centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(newsImageGlideRequestOptions)
                    .submit(NEWS_IMAGE_SIZE, NEWS_IMAGE_SIZE)
                    .get()
                view.setImageViewBitmap(R.id.newsImage, bitmap)
            } catch (e: Exception) {
                view.setImageViewResource(R.id.newsImage, R.drawable.ic_news_widget_empty_image_placeholder)
            }
        }
    }

}
