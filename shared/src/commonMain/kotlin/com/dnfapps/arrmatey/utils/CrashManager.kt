package com.dnfapps.arrmatey.utils

interface CrashManager {
    fun initialize()
    fun getLastCrashLog(): String?
    fun clearCrashLog()
    fun shareCrashLog(log: String)
}