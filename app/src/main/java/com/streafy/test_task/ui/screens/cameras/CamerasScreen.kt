package com.streafy.test_task.ui.screens.cameras

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.streafy.test_task.R
import com.streafy.test_task.domain.entities.Camera
import kotlin.math.roundToInt

@Composable
fun CamerasScreen(
    padding: PaddingValues,
    viewModel: CamerasViewModel = hiltViewModel()
) {
    val state by viewModel.state.observeAsState()

    when (val uiState = state) {
        CamerasUiState.Loading -> {
            Loading()
        }
        is CamerasUiState.Content -> {
            Content(
                padding,
                uiState.cameras,
                onRefresh = { viewModel.getCameras(isPullRefresh = true) },
                viewModel::onFavoriteClick
            )
        }
        is CamerasUiState.Error -> {
            Error(uiState.message) { viewModel.getCameras() }
        }
        else -> throw RuntimeException("Invalid CamerasUiState: $uiState")
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.width(128.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    padding: PaddingValues,
    cameras: List<Camera>,
    onRefresh: () -> Unit = {},
    onFavoriteClick: (Camera) -> Unit
) {
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
            state.endRefresh()
        }
    }
    Box(modifier = Modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier
                .padding(start = 21.dp, top = padding.calculateTopPadding() + 16.dp, end = 21.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cameras.groupBy { it.room }.forEach { (room, cameras) ->
                val title = room.ifBlank { "Неизвестная комната" }
                item {
                    Text(
                        text = title,
                        fontSize = 21.sp,
                    )
                }
                items(cameras) { camera ->
                    CameraCard(camera, onFavoriteClick)
                }
            }
        }
        PullToRefreshContainer(state = state, modifier = Modifier.align(Alignment.TopCenter))
    }
}

private enum class DragAnchors {
    Start,
    End,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraCard(
    camera: Camera,
    onFavoriteClick: (Camera) -> Unit
) {
    val loaded = remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween()
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchors.Start at 0f
                    DragAnchors.End at -150f
                }
            )
        }
    }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(), y = 0
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal)
        ) {
            Box {
                CameraPreview(previewUri = camera.snapshot) { loaded.value = true }
                if (loaded.value) {
                    CameraPreviewOverlay(isFavorite = camera.favorites, isRecording = camera.rec)
                }
            }
            Text(
                text = camera.name,
                modifier = Modifier.padding(16.dp)
            )
        }
        IconButton(
            onClick = { onFavoriteClick(camera) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(-1f),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.star_button),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }

}

@Composable
private fun CameraPreview(
    previewUri: String,
    onSuccess: () -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(previewUri)
            .crossfade(true)
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.loading_img),
        onSuccess = { onSuccess() },
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(207.dp),
        imageLoader = LocalContext.current.imageLoader.newBuilder().logger(DebugLogger())
            .build()
    )
}

@Composable
private fun BoxScope.CameraPreviewOverlay(
    isFavorite: Boolean,
    isRecording: Boolean
) {
    Icon(
        painter = painterResource(id = R.drawable.play_button),
        contentDescription = null,
        modifier = Modifier.align(Alignment.Center)
    )
    if (isFavorite) {
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopEnd),
            tint = Color.Unspecified
        )
    }
    if (isRecording) {
        Icon(
            painter = painterResource(id = R.drawable.rec),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopStart),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun Error(
    message: String?,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message ?: "Неизвестная ошибка")
        Button(onClick = onRetry) {
            Text(text = "Повторить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CamerasScreenPreview() {
    CamerasScreen(PaddingValues())
}