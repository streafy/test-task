package com.streafy.test_task.ui.screens.cameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streafy.test_task.data.CamerasRepository
import com.streafy.test_task.domain.entities.Camera
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CamerasViewModel @Inject constructor(
    private val camerasRepository: CamerasRepository
) : ViewModel() {

    private val _state = MutableLiveData<CamerasUiState>(CamerasUiState.Loading)
    val state: LiveData<CamerasUiState>
        get() = _state

    init {
        getCameras()
    }

    fun getCameras(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = CamerasUiState.Loading
            _state.value = try {
                val cameras = if (isRefresh) {
                    camerasRepository.getNetworkCameras()
                } else {
                    camerasRepository.getCameras()
                }
                CamerasUiState.Content(cameras)
            } catch (e: Exception) {
                CamerasUiState.Error(e.message)
            }
        }
    }

    fun onFavoriteClick(camera: Camera) {
        viewModelScope.launch {
            val updatedCamera = camera.copy(favorites = !camera.favorites)
            camerasRepository.updateCamera(updatedCamera)
            _state.value = CamerasUiState.Loading
            _state.value = CamerasUiState.Content(camerasRepository.getLocalCameras())
        }
    }
}

sealed interface CamerasUiState {

    data object Loading : CamerasUiState
    data class Content(val cameras: List<Camera>) : CamerasUiState
    data class Error(val message: String?) : CamerasUiState
}

