package com.streafy.test_task.ui.screens.doors

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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.streafy.test_task.R
import com.streafy.test_task.domain.entities.Door
import kotlin.math.roundToInt

@Composable
fun DoorsScreen(
    padding: PaddingValues,
    viewModel: DoorsViewModel = hiltViewModel()
) {
    val state by viewModel.state.observeAsState(DoorsUiState.Loading)

    when (val uiState = state) {
        DoorsUiState.Loading -> {
            Loading()
        }
        is DoorsUiState.Content -> {
            Content(
                padding,
                uiState.doors,
                onRefresh = { viewModel.getDoors(isRefresh = true) },
                onFavoriteClick = viewModel::onFavoriteClick,
                onSaveDoorNameClick = viewModel::onSaveDoorNameClick
            )
        }
        is DoorsUiState.Error -> {
            Error(message = uiState.message) { viewModel.getDoors(isRefresh = true) }
        }
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
    doors: List<Door>,
    onRefresh: () -> Unit = {},
    onFavoriteClick: (Door) -> Unit,
    onSaveDoorNameClick: (Door, String) -> Unit
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
            items(doors) { door ->
                DoorCard(
                    door = door,
                    onFavoriteClick = onFavoriteClick,
                    onSaveDoorNameClick = onSaveDoorNameClick
                )
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
private fun DoorCard(
    door: Door,
    onFavoriteClick: (Door) -> Unit,
    onSaveDoorNameClick: (Door, String) -> Unit
) {
    val isEditing = remember {
        mutableStateOf(false)
    }

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
                    DragAnchors.End at -300f
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
            if (door.snapshot.isNotBlank()) {
                CameraPreview(
                    previewUri = door.snapshot
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing.value) {
                    EditDoorName(door) { door, name ->
                        onSaveDoorNameClick(door, name)
                        isEditing.value = false
                    }
                } else {
                    Text(
                        text = door.name
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = null
                )
            }
        }
        RevealedButtons(
            onFavoriteClick = onFavoriteClick,
            onEditClick = { isEditing.value = true },
            door = door
        )
    }
}

@Composable
private fun EditDoorName(
    door: Door,
    onSaveDoorNameClick: (Door, String) -> Unit
) {
    val name = remember { mutableStateOf(door.name) }

    Row {
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            modifier = Modifier.weight(0.5f)
        )
        Button(
            onClick = { onSaveDoorNameClick(door, name.value) },
            modifier = Modifier.weight(0.5f)
        ) {
            Text(text = "Сохранить")
        }
    }
}

@Composable
private fun BoxScope.RevealedButtons(
    onFavoriteClick: (Door) -> Unit,
    onEditClick: (Door) -> Unit,
    door: Door
) {
    Row(
        modifier = Modifier
            .zIndex(-1f)
            .align(Alignment.CenterEnd),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onEditClick(door) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        IconButton(
            onClick = { onFavoriteClick(door) }
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
    previewUri: String
) {
    val loaded = remember { mutableStateOf(false) }

    Box {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                //.data(previewUri)
                //Добавил плейсхолдеры(нужны для показа оверлея) т.к по ссылке из api выдает
                //Failed - https://serverspace.ru/wp-content/uploads/2019/06/backup-i-snapshot.png
                // - javax.net.ssl.SSLHandshakeException:
                //   java.security.cert.CertPathValidatorException:
                //   Trust anchor for certification path not found.
                .data("https://developer.android.com/codelabs/basic-android-kotlin-compose-amphibians-app/img/great-basin-spadefoot.png")
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.loading_img),
            onSuccess = { loaded.value = true },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(207.dp),
            imageLoader = LocalContext.current.imageLoader.newBuilder().logger(DebugLogger())
                .build()
        )
        if (loaded.value) {
            CameraPreviewOverlay()
        }
    }
}

@Composable
private fun BoxScope.CameraPreviewOverlay() {
    Icon(
        painter = painterResource(id = R.drawable.play_button),
        contentDescription = null,
        modifier = Modifier.align(Alignment.Center)
    )
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