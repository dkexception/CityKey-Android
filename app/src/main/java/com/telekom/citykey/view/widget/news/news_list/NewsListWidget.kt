package com.telekom.citykey.view.widget.news.news_list

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.telekom.citykey.R
import com.telekom.citykey.domain.track.AdjustManager
import com.telekom.citykey.utils.NetworkConnection
import com.telekom.citykey.view.main.MainActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsListWidget : AppWidgetProvider(), KoinComponent {

    companion object {
        const val NEWS_IMAGE_SIZE = 512
        const val NEWS_LIST_WIDGET_REQUEST_CODE = 7834519
        const val ITEM_CORNER_RADIUS = 16
    }

    private val adjustManager: AdjustManager by inject()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.news_list_widget)
            val intent = Intent(context, NewsListWidgetRemoteViewService::class.java).apply {
                putExtra("widgetID", appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setRemoteAdapter(R.id.newsListView, intent)
            views.setEmptyView(R.id.newsListView, R.id.loadingIndicatorImageView)
            views.setImageViewResource(R.id.loadingIndicatorImageView, R.drawable.bg_news_list_widget_loader)
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, NEWS_LIST_WIDGET_REQUEST_CODE, activityIntent,
                PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.newsListView, pendingIntent)
            views.setOnClickPendingIntent(R.id.loadingIndicatorImageView, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        if (NetworkConnection.checkInternetConnection(context)) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.newsListView)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        adjustManager.trackEvent(R.string.news_medium_widget)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        adjustManager.trackEvent(R.string.remove_news_medium_widget)
    }
}
