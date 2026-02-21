package com.dnfapps.arrmatey.downloadclient.model

enum class DownloadClientType(
    val displayName: String,
    val defaultPort: Int,
    val iconKey: String
) {
    QBittorrent(
        displayName = "qBittorrent",
        defaultPort = 8080,
        iconKey = "qbittorrent"
    ),
    SABnzbd(
        displayName = "SABnzbd",
        defaultPort = 8080,
        iconKey = "sabnzbd"
    )
}
