package com.dnfapps.arrmatey.utils

import android.content.Context
import android.content.Intent
import com.dnfapps.arrmatey.shared.MR
import java.io.File

class AndroidCrashManager(
    private val context: Context,
    private val moko: MokoStrings
) : CrashManager {
    private val logFile = File(context.filesDir, "latest_crash.txt")

    override fun initialize() {
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logFile.writeText(throwable.stackTraceToString())
            oldHandler?.uncaughtException(thread, throwable)
        }
    }

    override fun getLastCrashLog(): String? = if (logFile.exists()) logFile.readText() else null

    override fun clearCrashLog() { logFile.delete() }

    override fun shareCrashLog(log: String){
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, log)
            putExtra(Intent.EXTRA_SUBJECT, moko.getString(MR.strings.crash_report_subject))
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val shareIntent = Intent.createChooser(sendIntent, moko.getString(MR.strings.share_crash_log))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}