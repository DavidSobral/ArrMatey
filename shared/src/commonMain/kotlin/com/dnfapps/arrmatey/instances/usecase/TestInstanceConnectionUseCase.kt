package com.dnfapps.arrmatey.instances.usecase

import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.client.onError
import com.dnfapps.arrmatey.client.onSuccess
import com.dnfapps.arrmatey.instances.repository.InstanceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestInstanceConnectionUseCase(
    private val instanceManager: InstanceManager
) {
    operator fun invoke(id: Long): Flow<OperationStatus> = flow {
        emit(OperationStatus.InProgress)

        val repository = instanceManager.getRepository(id)
        if (repository == null) {
            emit(OperationStatus.Error(message = "Instance cannot be found"))
            return@flow
        }

        repository.testConnection()
            .onSuccess {
                emit(OperationStatus.Success())
            }
            .onError { code, message, cause ->
                emit(OperationStatus.Error(code, message, cause))
            }
    }
}