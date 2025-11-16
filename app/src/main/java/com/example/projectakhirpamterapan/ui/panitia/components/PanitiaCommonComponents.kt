package com.example.projectakhirpamterapan.ui.panitia.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width

@Composable
fun ErrorBanner(
    message: String,
    onRetry: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Coba lagi",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.primary,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VerticalScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(6.dp)
    ) {
        val layoutInfo = listState.layoutInfo
        val totalItems = layoutInfo.totalItemsCount
        val visibleItemsInfo = layoutInfo.visibleItemsInfo

        if (totalItems <= 0 || visibleItemsInfo.isEmpty()) {
            return@BoxWithConstraints
        }

        val viewportHeightPx =
            (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset)
                .coerceAtLeast(1)

        val avgItemSizePx = visibleItemsInfo
            .map { it.size }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toFloat()
            ?.coerceAtLeast(1f)
            ?: viewportHeightPx.toFloat()

        val contentHeightPx = avgItemSizePx * totalItems.toFloat()

        val scrollableHeightPx = (contentHeightPx - viewportHeightPx.toFloat())
            .coerceAtLeast(1f)

        if (scrollableHeightPx <= 1f) {
            return@BoxWithConstraints
        }

        val contentScrollOffsetPx =
            listState.firstVisibleItemIndex * avgItemSizePx +
                    listState.firstVisibleItemScrollOffset.toFloat()

        val rawProgress = (contentScrollOffsetPx / scrollableHeightPx)
            .coerceIn(0f, 1f)

        val animatedProgress by animateFloatAsState(
            targetValue = rawProgress,
            animationSpec = tween(durationMillis = 80),
            label = "scrollbarProgress"
        )

        val thumbHeight = (40.dp).coerceAtMost(maxHeight * 0.7f)
        val thumbOffset = (maxHeight - thumbHeight) * animatedProgress

        // Track
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(4.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    colorScheme.onSurface.copy(alpha = 0.08f)
                )
        )

        // Thumb
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = thumbOffset)
                .width(6.dp)
                .height(thumbHeight)
                .clip(RoundedCornerShape(50))
                .background(
                    colorScheme.primary.copy(alpha = 0.9f)
                )
        )
    }
}

@Composable
fun EventSkeletonCard() {
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.onSurface.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.onSurface.copy(alpha = alpha * 0.9f))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.onSurface.copy(alpha = alpha * 0.6f))
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📅",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Belum ada event untuk filter ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap tombol + di kanan bawah untuk membuat event baru.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
