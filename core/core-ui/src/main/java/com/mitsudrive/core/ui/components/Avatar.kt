package com.mitsudrive.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mitsudrive.core.ui.theme.*

@Composable
fun Avatar(
    imageUrl: String?,
    username: String,
    modifier: Modifier = Modifier,
    size: Int = 40,
    backgroundColor: Color = NeonBlue.copy(alpha = 0.2f)
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Аватар $username",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = username.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = (size / 2.5f).sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue
            )
        }
    }
}
