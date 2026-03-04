package com.dnfapps.arrmatey.downloadclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.downloadclient.state.DownloadClientCommandState
import com.dnfapps.arrmatey.downloadclient.state.DownloadQueueState
import com.dnfapps.arrmatey.downloadclient.usecase.DeleteDownloadUseCase
import com.dnfapps.arrmatey.downloadclient.usecase.ObserveDownloadQueueUseCase
import com.dnfapps.arrmatey.downloadclient.usecase.PauseDownloadUseCase
import com.dnfapps.arrmatey.downloadclient.usecase.ResumeDownloadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadQueueViewModel(
    observeDownloadQueueUseCase: ObserveDownloadQueueUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val resumeDownloadUseCase: ResumeDownloadUseCase,
    private val deleteDownloadUseCase: DeleteDownloadUseCase
): ViewModel() {

    val downloadQueueState: StateFlow<DownloadQueueState> = observeDownloadQueueUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DownloadQueueState.Initial
        )

    private val _commandState = MutableStateFlow<DownloadClientCommandState>(DownloadClientCommandState.Initial)
    val commandState: StateFlow<DownloadClientCommandState> = _commandState.asStateFlow()

    fun pauseDownload(id: String) {
        viewModelScope.launch {
            pauseDownloadUseCase(id).collect { state ->
                _commandState.value = state.toCommandState()
            }
        }
    }

    fun resumeDownload(id: String) {
        viewModelScope.launch {
            resumeDownloadUseCase(id).collect { state ->
                _commandState.value = state.toCommandState()
            }
        }
    }

    fun deleteDownload(id: String, deleteFiles: Boolean) {
        viewModelScope.launch {
            deleteDownloadUseCase(id, deleteFiles).collect { state ->
                _commandState.value = state.toCommandState()
            }
        }
    }

    fun resetCommandState() {
        _commandState.value = DownloadClientCommandState.Initial
    }

    private fun OperationStatus.toCommandState(): DownloadClientCommandState = when (this) {
        is OperationStatus.Idle -> DownloadClientCommandState.Initial
        is OperationStatus.InProgress -> DownloadClientCommandState.Loading
        is OperationStatus.Success -> DownloadClientCommandState.Success
        is OperationStatus.Error -> DownloadClientCommandState.Error(
            code = code,
            message = message,
            cause = cause
        )
    }
}
