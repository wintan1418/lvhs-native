package com.lvhs.school

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UpdateChecker(private val context: Context) {

    fun check() {
        thread {
            try {
                val url = URL("${BuildConfig.BASE_URL}/api/v1/app_version")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val serverVersionCode = json.getInt("version_code")
                    val serverVersion = json.getString("version")
                    val downloadUrl = json.getString("url")
                    val message = json.optString("message", "A new version is available.")
                    val forceUpdate = json.optBoolean("force_update", false)

                    val currentVersionCode = context.packageManager
                        .getPackageInfo(context.packageName, 0).longVersionCode.toInt()

                    if (serverVersionCode > currentVersionCode) {
                        (context as? android.app.Activity)?.runOnUiThread {
                            showUpdateDialog(serverVersion, message, downloadUrl, forceUpdate)
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
                // Silent fail — no update check interruption
            }
        }
    }

    private fun showUpdateDialog(version: String, message: String, url: String, force: Boolean) {
        val builder = AlertDialog.Builder(context)
            .setTitle("Update Available (v$version)")
            .setMessage(message)
            .setPositiveButton("Update Now") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }

        if (!force) {
            builder.setNegativeButton("Later", null)
        } else {
            builder.setCancelable(false)
        }

        builder.show()
    }
}
