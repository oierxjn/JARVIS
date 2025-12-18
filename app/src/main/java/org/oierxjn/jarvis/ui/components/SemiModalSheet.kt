
package org.oierxjn.jarvis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.oierxjn.jarvis.R

/**
 * A composable function that creates a semi-modal sheet with animations.
 * The sheet appears from the bottom of the screen with a semi-transparent background.
 *
 * @param isVisible Boolean to control the visibility of the sheet
 * @param onDismiss Callback function to be called when the sheet is dismissed
 * @param modifier Modifier to be applied to the sheet
 * @param fraction Fraction of the screen height to be occupied by the sheet (default: 0.75f)
 * @param content Composable content to be displayed inside the sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemiModalSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    fraction: Float = 0.75f,
    content: @Composable () -> Unit = {}
) {
    val springSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    // Animated container for the entire sheet
    AnimatedVisibility(
//        modifier = Modifier.background(Color.Red.copy(alpha = 0.2f)),
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = springSpec,
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = springSpec,
            targetOffsetY = { it }
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // 点击背景关闭
                    detectTapGestures {
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(fraction) // 占屏幕高度的75%
                    ,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var dragOffset by remember { mutableFloatStateOf(0f) }
                    // 拖动手柄
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        if (change.positionChange() != Offset.Zero) change.consume()
                                        dragOffset += dragAmount.y
                                    },
                                    onDragEnd = {
                                        if (dragOffset > 0.8f) {
                                            onDismiss()
                                        }else {
                                            dragOffset = 0f
                                        }
                                    }
                                )
                            }
                    )

                    // 关闭按钮
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24dp),
                            contentDescription = "关闭"
                        )
                    }
                }

                // 内容区域
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
