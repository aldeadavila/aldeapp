/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aldeadvila.arcTemplate.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.aldeadvila.arcTemplate.data.TaskRepository
import com.aldeadvila.arcTemplate.ui.task.TaskUiState.Error
import com.aldeadvila.arcTemplate.ui.task.TaskUiState.Loading
import com.aldeadvila.arcTemplate.ui.task.TaskUiState.Success
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<TaskUiState> = taskRepository
        .tasks.map<List<String>, TaskUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addTask(name: String) {
        viewModelScope.launch {
            taskRepository.add(name)
        }
    }
}

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Error(val throwable: Throwable) : TaskUiState
    data class Success(val data: List<String>) : TaskUiState
}
