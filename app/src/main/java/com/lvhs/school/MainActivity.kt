package com.lvhs.school

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lvhs.school.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Bottom tab routes — these are universal for all roles
    // The Rails app handles role-based redirection after login
    private val tabs = mapOf(
        R.id.nav_home to "/",
        R.id.nav_portal to "/admin/dashboard",
        R.id.nav_notifications to "/profile",
        R.id.nav_profile to "/profile"
    )

    private var currentTabId = R.id.nav_home
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CookieManager.getInstance().setAcceptCookie(true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadTab(R.id.nav_home)
            handleDeepLink(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleDeepLink(it) }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragment = activeFragment
        if (fragment is WebViewFragment && fragment.canGoBack()) {
            fragment.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId != currentTabId) {
                loadTab(item.itemId)
            }
            true
        }
    }

    private fun loadTab(tabId: Int) {
        val tag = "tab_$tabId"
        val transaction = supportFragmentManager.beginTransaction()

        activeFragment?.let { transaction.hide(it) }

        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            val path = tabs[tabId] ?: "/"
            val url = "${BuildConfig.BASE_URL}$path"
            fragment = WebViewFragment.newInstance(url)
            transaction.add(R.id.fragment_container, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        transaction.commit()
        activeFragment = fragment
        currentTabId = tabId
    }

    private fun handleDeepLink(intent: Intent) {
        val data = intent.data ?: return
        val path = data.path ?: return
        val matchingTab = tabs.entries.find { path.startsWith(it.value) }
        if (matchingTab != null) {
            binding.bottomNavigation.selectedItemId = matchingTab.key
        }
    }
}
