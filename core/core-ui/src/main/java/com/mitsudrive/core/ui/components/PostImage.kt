package com.mitsudrive.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mitsudrive.core.ui.theme.*

@Composable
fun PostImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    height: Int = 200
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Изображение поста",
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(Dimens.radius_md))
            .background(CardBackground),
        contentScale = ContentScale.Crop
    )
}
