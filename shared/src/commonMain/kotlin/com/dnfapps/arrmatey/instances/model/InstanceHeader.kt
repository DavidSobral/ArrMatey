package com.dnfapps.arrmatey.instances.model

import kotlinx.serialization.Serializable

@Serializable
data class InstanceHeader(
    val key: String = "",
    val value: String = "",
    val sendOnlyOnLocal: Boolean = false,
    val sendOnlyOnRemote: Boolean = false
)