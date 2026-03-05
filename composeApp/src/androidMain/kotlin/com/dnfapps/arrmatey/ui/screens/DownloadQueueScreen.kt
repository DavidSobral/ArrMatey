package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.downloadclient.model.DownloadItem
import com.dnfapps.arrmatey.downloadclient.model.DownloadItemStatus
import com.dnfapps.arrmatey.downloadclient.state.DownloadClientCommandState
import com.dnfapps.arrmatey.downloadclient.state.DownloadQueueState
import com.dnfapps.arrmatey.downloadclient.viewmodel.DownloadQueueViewModel
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.navigation.NavigationDrawerButton
import com.dnfapps.arrmatey.utils.mokoString
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadQueueScreen(
    viewModel: DownloadQueueViewModel = koinInject()
) {
    val queueState by viewModel.downloadQueueState.collectAsStateWithLifecycle()
    val commandState by viewModel.commandState.collectAsStateWithLifecycle()

    var deleteTarget by remember { mutableStateOf<DownloadItem?>(null) }

    LaunchedEffect(commandState) {
        if (commandState is DownloadClientCommandState.Success) {
            deleteTarget = null
            viewModel.resetCommandState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val count = (queueState as? DownloadQueueState.Success)?.items?.size ?: 0
                    val suffix = if (count > 0) " ($count)" else ""
                    Text(text = mokoString(MR.strings.downloads) + suffix)
                },
                navigationIcon = {
                    NavigationDrawerButton()
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (val state = queueState) {
                is DownloadQueueState.Initial,
                is DownloadQueueState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DownloadQueueState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.message ?: mokoString(MR.strings.error),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is DownloadQueueState.Success -> {
                    if (state.items.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = mokoString(MR.strings.no_activity))
                        }
                    } else {
                        Text(
                            text = "↓ ${state.transferInfo.downloadSpeed.bytesAsFileSizeString()}/s  ↑ ${state.transferInfo.uploadSpeed.bytesAsFileSizeString()}/s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(items = state.items, key = { it.id }) { item ->
                                DownloadQueueItem(
                                    item = item,
                                    actionInProgress = commandState is DownloadClientCommandState.Loading,
                                    onPause = { viewModel.pauseDownload(item.id) },
                                    onResume = { viewModel.resumeDownload(item.id) },
                                    onDelete = { deleteTarget = item }
                                )
                            }
                        }
                    }
                }
            }
        }

        deleteTarget?.let { item ->
            DeleteDownloadDialog(
                commandState = commandState,
                onDismiss = { deleteTarget = null },
                onConfirm = { deleteFiles ->
                    viewModel.deleteDownload(item.id, deleteFiles)
                }
            )
        }
    }
}

@Composable
private fun DownloadQueueItem(
    item: DownloadItem,
    actionInProgress: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${item.status.label()}  •  ${item.downloadSpeed.bytesAsFileSizeString()}/s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.eta.isNotBlank()) {
                Text(
                    text = "ETA: ${item.eta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { item.progress.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (item.status == DownloadItemStatus.Paused) {
                    IconButton(
                        onClick = onResume,
                        enabled = !actionInProgress
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    }
                } else {
                    IconButton(
                        onClick = onPause,
                        enabled = !actionInProgress
                    ) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = null)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    enabled = !actionInProgress
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun DeleteDownloadDialog(
    commandState: DownloadClientCommandState,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    var deleteFiles by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(mokoString(MR.strings.confirm)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Remove this download?")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = deleteFiles, onCheckedChange = { deleteFiles = it })
                    Text(mokoString(MR.strings.delete_files))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(deleteFiles) },
                enabled = commandState !is DownloadClientCommandState.Loading
            ) {
                Text(mokoString(MR.strings.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(mokoString(MR.strings.no))
            }
        }
    )
}

private fun DownloadItemStatus.label(): String = when (this) {
    DownloadItemStatus.Downloading -> "Downloading"
    DownloadItemStatus.Paused -> "Paused"
    DownloadItemStatus.Queued -> "Queued"
    DownloadItemStatus.Completed -> "Completed"
    DownloadItemStatus.Failed -> "Failed"
    DownloadItemStatus.Seeding -> "Seeding"
    DownloadItemStatus.Stalled -> "Stalled"
    else -> name
}
