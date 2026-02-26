package com.dnfapps.arrmatey.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.navigation.NavigationManager
import org.koin.compose.koinInject

@Composable
fun NavigationDrawerButton() {
    val navigationManager: NavigationManager = koinInject()
    IconButton(onClick = {
        navigationManager.openDrawer()
    } ) {
        Icon(Icons.Default.Menu, null)
    }
}