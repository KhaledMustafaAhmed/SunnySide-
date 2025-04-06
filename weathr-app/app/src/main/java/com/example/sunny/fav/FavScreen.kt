package com.example.sunny.fav

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sunny.R
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.utility.ResultResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    viewModel: FavViewModel,
    onAddPlaceClick: () -> Unit,
    onBackClick: () -> Unit,
    onPlaceClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showDeleteDialog.collectAsState()
    val placeToDelete by viewModel.placeToDelete.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.favourites)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlaceClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Place, contentDescription = "Add Place")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is ResultResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is ResultResponse.Failure -> {
                    Text(text = (uiState as ResultResponse.Failure).message)
                }
                is ResultResponse.Success -> {
                    val favorites = (uiState as ResultResponse.Success<List<FavoritePlaceEntity>>).value
                    if (favorites.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favourite places set\nPress add to add one",
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        FavoritesList(
                            favorites = favorites,
                            onDeleteClick = { viewModel.showDeleteDialog(it) },
                            onPlaceClick = onPlaceClick
                        )
                    }
                }
            }

            if (showDialog && placeToDelete != null) {
                DeleteConfirmationDialog(
                    placeName = placeToDelete!!.cityName,
                    onConfirm = { viewModel.confirmDelete() },
                    onDismiss = { viewModel.dismissDeleteDialog() }
                )
            }
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<FavoritePlaceEntity>,
    onDeleteClick: (FavoritePlaceEntity) -> Unit,
    onPlaceClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(favorites) { favorite ->
            FavoritePlaceItem(
                favorite = favorite,
                onDeleteClick = { onDeleteClick(favorite) },
                onClick = { onPlaceClick(favorite.id) }
            )
        }
    }
}

@Composable
private fun FavoritePlaceItem(
    favorite: FavoritePlaceEntity,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = favorite.cityName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Lat: ${"%.4f".format(favorite.latitude)}, Lng: ${"%.4f".format(favorite.longitude)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    placeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete $placeName?") },
        text = { Text(text = "Are you sure you want to remove $placeName from favorites?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}