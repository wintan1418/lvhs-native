package com.lvhs.school

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class WebViewFragment : Fragment() {

    private var webView: WebView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingOverlay: android.view.View? = null
    private var currentUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.turbo_web_view)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        loadingOverlay = view.findViewById(R.id.loading_overlay)
        currentUrl = arguments?.getString(ARG_URL)
            ?: "${BuildConfig.BASE_URL}/"

        setupWebView()
        setupSwipeRefresh()
        webView?.loadUrl(currentUrl)
    }

    private fun setupWebView() {
        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.allowFileAccess = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.userAgentString =
                "${settings.userAgentString} $CUSTOM_USER_AGENT"

            val wv = this
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(wv, true)
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    if (isExternalUrl(url)) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        return true
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    swipeRefresh?.isRefreshing = false
                    CookieManager.getInstance().flush()
                    url?.let { currentUrl = it }

                    // Fade out loading overlay
                    loadingOverlay?.let { overlay ->
                        if (overlay.visibility == android.view.View.VISIBLE) {
                            overlay.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .withEndAction {
                                    overlay.visibility = android.view.View.GONE
                                }
                                .start()
                        }
                    }
                }
            }

            webChromeClient = WebChromeClient()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh?.apply {
            setColorSchemeResources(R.color.forest_green)
            setOnRefreshListener { webView?.reload() }
        }
    }

    fun navigateTo(url: String) { currentUrl = url; webView?.loadUrl(url) }
    fun canGoBack(): Boolean = webView?.canGoBack() ?: false
    fun goBack() { webView?.goBack() }

    private fun isExternalUrl(url: String): Boolean {
        val internalHost = "lvhs.ng"
        val uri = Uri.parse(url)
        val host = uri.host ?: return false
        return !host.contains(internalHost)
    }

    companion object {
        private const val ARG_URL = "url"
        private const val CUSTOM_USER_AGENT = "LVHS Android/1.0 Turbo Native"

        fun newInstance(url: String): WebViewFragment {
            return WebViewFragment().apply {
                arguments = Bundle().apply { putString(ARG_URL, url) }
            }
        }
    }
}
