package com.dnfapps.arrmatey.downloadclient.state

import com.dnfapps.arrmatey.downloadclient.model.DownloadItem
import com.dnfapps.arrmatey.downloadclient.model.DownloadTransferInfo

sealed interface DownloadQueueState {
    object Initial: DownloadQueueState
    object Loading: DownloadQueueState
    data class Success(
        val items: List<DownloadItem>,
        val transferInfo: DownloadTransferInfo
    ): DownloadQueueState
    data class Error(
        val code: Int? = null,
        val message: String? = null,
        val cause: Throwable? = null
    ): DownloadQueueState
}
