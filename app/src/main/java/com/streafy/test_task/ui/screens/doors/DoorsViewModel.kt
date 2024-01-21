package com.streafy.test_task.ui.screens.doors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streafy.test_task.data.DoorsRepository
import com.streafy.test_task.domain.entities.Door
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoorsViewModel @Inject constructor(
    private val doorsRepository: DoorsRepository
) : ViewModel() {

    private val _state = MutableLiveData<DoorsUiState>(DoorsUiState.Loading)
    val state: LiveData<DoorsUiState>
        get() = _state

    init {
        getDoors()
    }

    fun getDoors(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = DoorsUiState.Loading
            _state.value = try {
                val doors = if (isRefresh) {
                    doorsRepository.getNetworkDoors()
                } else {
                    doorsRepository.getDoors()
                }
                DoorsUiState.Content(doors)
            } catch (e: Exception) {
                DoorsUiState.Error(e.message)
            }
        }
    }

    fun onFavoriteClick(door: Door) {
        viewModelScope.launch {
            val updatedDoor = door.copy(favorites = !door.favorites)
            doorsRepository.updateDoor(updatedDoor)
            _state.value = DoorsUiState.Loading
            _state.value = DoorsUiState.Content(doorsRepository.getLocalDoors())
        }
    }

    fun onSaveDoorNameClick(door: Door, name: String) {
        viewModelScope.launch {
            val updatedDoor = door.copy(name = name)
            doorsRepository.updateDoor(updatedDoor)
            _state.value = DoorsUiState.Loading
            _state.value = DoorsUiState.Content(doorsRepository.getLocalDoors())
        }
    }
}

sealed interface DoorsUiState {

    data object Loading : DoorsUiState
    data class Content(val doors: List<Door>) : DoorsUiState
    data class Error(val message: String?) : DoorsUiState
}