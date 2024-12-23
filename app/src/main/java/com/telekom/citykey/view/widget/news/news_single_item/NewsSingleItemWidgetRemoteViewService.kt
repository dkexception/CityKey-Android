package com.telekom.citykey.view.widget.news.news_single_item

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.telekom.citykey.R
import com.telekom.citykey.domain.widget.WidgetInteractor
import com.telekom.citykey.models.content.CityContent
import com.telekom.citykey.utils.NetworkConnection
import com.telekom.citykey.view.widget.news.news_single_item.NewsSingleItemWidget.Companion.ITEM_CORNER_RADIUS
import com.telekom.citykey.view.widget.news.news_single_item.NewsSingleItemWidget.Companion.NEWS_SINGLE_IMAGE_SIZE
import org.koin.android.ext.android.inject

class NewsSingleItemWidgetRemoteViewService : RemoteViewsService() {

    private val widgetInteractor: WidgetInteractor by inject()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        NewsSingleItemViewsFactory(applicationContext, widgetInteractor.newsList)

    inner class NewsSingleItemViewsFactory(private val context: Context, private val newsList: List<CityContent>) :
        RemoteViewsFactory {

        private val newsImageGlideRequestOptions: RequestOptions by lazy {
            RequestOptions()
                .override(NEWS_SINGLE_IMAGE_SIZE, NEWS_SINGLE_IMAGE_SIZE)
                .transform(CenterInside(), RoundedCorners(ITEM_CORNER_RADIUS))
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
            val view = RemoteViews(context.packageName, R.layout.news_single_widget_item)

            val newsItem = newsList[position]
            populateNewsData(view, newsItem)

            val fillIntent = Intent().putExtra("newsItem", newsItem)
            view.setOnClickFillInIntent(R.id.newsSingleItemContainer, fillIntent)
            return view
        }

        override fun onDataSetChanged() {
            if (NetworkConnection.checkInternetConnection(context))
                widgetInteractor.getNewsForCurrentCity(isSingleItemWidget = true)
        }

        override fun onDestroy() {
            widgetInteractor.clearNewsList()
        }

        private fun populateNewsData(view: RemoteViews, cityContent: CityContent) {
            view.setTextViewText(R.id.description, cityContent.contentTeaser)
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
                    .submit()
                    .get()
                view.setImageViewBitmap(R.id.newsImage, bitmap)
            } catch (e: Exception) {
                view.setImageViewResource(R.id.newsImage, R.drawable.ic_news_widget_empty_image_placeholder)
            }
        }
    }

}
