package com.dnfapps.arrmatey.downloadclient.model

data class DownloadItem(
    val id: String,
    val name: String,
    val size: Long,
    val progress: Double,
    val downloadSpeed: Long,
    val uploadSpeed: Long,
    val eta: String,
    val status: DownloadItemStatus,
    val category: String,
    val addedOn: Long
)
