package com.telekom.citykey.view.home.news.article

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.telekom.citykey.R
import com.telekom.citykey.databinding.ArticleHeaderBinding
import com.telekom.citykey.databinding.ArticleHtmlBinding
import com.telekom.citykey.domain.city.CityInteractor
import com.telekom.citykey.models.content.CityContent
import com.telekom.citykey.utils.DialogUtil
import com.telekom.citykey.utils.NetworkConnection
import com.telekom.citykey.utils.extensions.*

class ArticleContentAdapter(
    private val cityContent: CityContent, private val htmlContentList: List<String>, private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_HTML = 1
    }

    override fun getItemCount(): Int = htmlContentList.size + 1

    override fun getItemViewType(position: Int): Int = if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_HTML

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_HEADER ->
                ViewHolderHeader(ArticleHeaderBinding.bind(parent.inflateChild(R.layout.article_header)))

            else ->
                ViewHolderHtml(ArticleHtmlBinding.bind(parent.inflateChild(R.layout.article_html)))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderHeader -> holder.loadHeaderContent()
            is ViewHolderHtml -> holder.loadHtmlContent()
        }
    }

    inner class ViewHolderHeader(val binding: ArticleHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun loadHeaderContent() {
            if (URLUtil.isValidUrl(cityContent.contentImage)) {
                binding.topLayout.visibility = View.VISIBLE
                binding.image.loadFromURLwithProgress(
                    cityContent.contentImage,
                    {
                        fragment.safeRun {
                            binding.loading.setVisible(false)
                            binding.errorLayout.setVisible(it)
                        }
                    }
                )
            }

            binding.credits.text = cityContent.imageCredit
            val pendingUrl = cityContent.contentSource

            binding.title.text = cityContent.contentTeaser
            if (!cityContent.contentSubtitle.isNullOrBlank()) {
                binding.subtitle.visibility = View.VISIBLE
                binding.subtitle.text = cityContent.contentSubtitle
            }

            with(binding.headDetail) {
                when (cityContent.contentTyp) {
                    "RABATTE" -> text = ""
                    "TIPPS" -> text = fragment.getString(R.string.h_003_home_articles_info_tips_category)
                    "ANGEBOTE" -> text = fragment.getString(R.string.h_003_home_articles_info_offers_category)
                    else -> {
                        text = cityContent.contentCreationDate.toDateString()
                        contentDescription = cityContent.contentCreationDate.toDateString().replace(".", "")
                    }
                }
            }

            with(binding.btnMore) {
                if (pendingUrl.isNullOrBlank().not()) {
                    setTextColor(CityInteractor.cityColorInt)
                    visibility = View.VISIBLE
                    setOnClickListener { fragment.openLink(pendingUrl!!) }
                }
            }

            with(binding.retryButton) {
                setTextColor(CityInteractor.cityColorInt)
                TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(CityInteractor.cityColorInt))
                setOnClickListener { loadImage(cityContent) }
            }
        }

        private fun loadImage(cityContent: CityContent) {
            if (!NetworkConnection.checkInternetConnection(fragment.requireContext())) {
                DialogUtil.showRetryDialog(fragment.requireContext(), { loadImage(cityContent) })
            } else {
                binding.errorLayout.setVisible(false)
                binding.loading.setVisible(true)
                binding.image.loadFromURLwithProgress(
                    cityContent.contentImage,
                    {
                        fragment.safeRun {
                            binding.loading.setVisible(false)
                            binding.errorLayout.setVisible(it)
                        }
                    }
                )
            }
        }
    }

    inner class ViewHolderHtml(val binding: ArticleHtmlBinding) : RecyclerView.ViewHolder(binding.root) {
        private val pageLinkHandlerWebViewClient by lazy {
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    fragment.attemptOpeningWebViewUri(request?.url)
                    return true
                }
            }
        }

        fun loadHtmlContent() {
            val content = htmlContentList[bindingAdapterPosition - 1]
            binding.contentWebView.apply {
                webViewClient = pageLinkHandlerWebViewClient
                linkifyAndLoadNonHtmlTaggedData(content)
            }
        }
    }

}
