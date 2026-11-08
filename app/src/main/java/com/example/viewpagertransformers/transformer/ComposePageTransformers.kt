package com.example.viewpagertransformers.transformer

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.example.viewpagertransformers.model.PageTransformerType
import kotlin.math.absoluteValue

/**
 * Applies a ViewPager transformer animation to a Compose Pager item based on its relative page offset.
 *
 * @param type The [PageTransformerType] to apply.
 * @param position The position of the page relative to the center (0f = active, -1f = left, 1f = right).
 */
fun Modifier.applyPageTransformer(
    type: PageTransformerType,
    position: Float,
    cameraMultiplier: Float = 14f
): Modifier = this.graphicsLayer {
    // Configurable camera distance for 3D perspective
    cameraDistance = cameraMultiplier * density

    // Clamp or handle out of range
    if (position <= -1.01f || position >= 1.01f) {
        if (type != PageTransformerType.DEFAULT && type != PageTransformerType.PARALLAX_PAGE) {
            alpha = 0f
            return@graphicsLayer
        }
    }

    when (type) {
        PageTransformerType.DEFAULT -> {
            translationX = 0f
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
            rotationX = 0f
            rotationY = 0f
            rotationZ = 0f
            alpha = 1f
        }

        PageTransformerType.ACCORDION -> {
            transformOrigin = TransformOrigin(
                pivotFractionX = if (position < 0f) 0f else 1f,
                pivotFractionY = 0.5f
            )
            val scale = if (position < 0f) 1f + position else 1f - position
            scaleX = scale.coerceIn(0f, 1f)
            scaleY = 1f
            alpha = if (position <= -1f || position >= 1f) 0f else 1f
        }

        PageTransformerType.BACKGROUND_TO_FOREGROUND -> {
            val scale = (if (position < 0f) 1f else 1f - position).coerceIn(0.5f, 1f)
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            translationX = if (position < 0f) {
                0f
            } else {
                -size.width * position * 0.35f
            }
            alpha = if (position < 0f) 1f + position * 0.5f else 1f
        }

        PageTransformerType.FOREGROUND_TO_BACKGROUND -> {
            val scale = (if (position > 0f) 1f else 1f + position).coerceIn(0.5f, 1f)
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            translationX = if (position > 0f) {
                0f
            } else {
                -size.width * position * 0.35f
            }
            alpha = if (position > 0f) 1f - position * 0.5f else 1f
        }

        PageTransformerType.CUBE_IN -> {
            transformOrigin = TransformOrigin(
                pivotFractionX = if (position > 0f) 0f else 1f,
                pivotFractionY = 0.5f
            )
            rotationY = -90f * position
            alpha = if (position.absoluteValue > 1f) 0f else 1f
        }

        PageTransformerType.CUBE_OUT -> {
            transformOrigin = TransformOrigin(
                pivotFractionX = if (position < 0f) 1f else 0f,
                pivotFractionY = 0.5f
            )
            rotationY = 90f * position
            alpha = if (position.absoluteValue > 1f) 0f else 1f
        }

        PageTransformerType.DEPTH_PAGE -> {
            if (position <= 0f) {
                translationX = 0f
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
            } else if (position <= 1f) {
                val scaleFactor = 0.75f + (1f - 0.75f) * (1f - position.absoluteValue)
                alpha = 1f - position
                translationX = -size.width * position
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
        }

        PageTransformerType.DRAW_FROM_BACK -> {
            if (position <= 0f) {
                alpha = (1f + position).coerceIn(0f, 1f)
                translationX = -size.width * position * 0.5f
                val scale = 0.75f + 0.25f * (1f - position.absoluteValue)
                scaleX = scale
                scaleY = scale
            } else {
                val scale = 0.75f + (0.3f - position).coerceIn(0f, 0.25f)
                scaleX = scale
                scaleY = scale
                translationX = size.width * position * 0.25f
                alpha = (1f - position * 0.7f).coerceIn(0f, 1f)
            }
        }

        PageTransformerType.FLIP_HORIZONTAL -> {
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            val rotation = 180f * position
            rotationY = rotation
            alpha = if (rotation.absoluteValue > 90f) 0f else 1f
        }

        PageTransformerType.FLIP_VERTICAL -> {
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            val rotation = -180f * position
            rotationX = rotation
            alpha = if (rotation.absoluteValue > 90f) 0f else 1f
        }

        PageTransformerType.PARALLAX_PAGE -> {
            // Container moves naturally; inner elements can apply their own offset
            translationX = 0f
            alpha = 1f
        }

        PageTransformerType.ROTATE_DOWN -> {
            transformOrigin = TransformOrigin(0.5f, 1f)
            rotationZ = 18.75f * position
            alpha = (1f - position.absoluteValue * 0.4f).coerceIn(0f, 1f)
        }

        PageTransformerType.ROTATE_UP -> {
            transformOrigin = TransformOrigin(0.5f, 0f)
            rotationZ = -15f * position
            alpha = (1f - position.absoluteValue * 0.4f).coerceIn(0f, 1f)
        }

        PageTransformerType.STACK -> {
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            if (position < 0f) {
                // Pin left page in place
                translationX = -size.width * position
                scaleX = (1f + position * 0.1f).coerceIn(0.9f, 1f)
                scaleY = scaleX
                alpha = (1f + position * 0.3f).coerceIn(0.7f, 1f)
            } else {
                // Right page slides naturally over top
                translationX = 0f
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
            }
        }

        PageTransformerType.TABLET -> {
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            val rotation = (if (position < 0f) 30f else -30f) * position.absoluteValue
            rotationY = rotation
            val scale = (1f - position.absoluteValue * 0.15f).coerceIn(0.85f, 1f)
            scaleX = scale
            scaleY = scale
            alpha = (1f - position.absoluteValue * 0.3f).coerceIn(0f, 1f)
        }

        PageTransformerType.ZOOM_IN -> {
            val scale = if (position < 0f) 1f + position else (1f - position).absoluteValue
            scaleX = scale.coerceIn(0f, 1f)
            scaleY = scale.coerceIn(0f, 1f)
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            alpha = (2f - scale).coerceIn(0f, 1f)
        }

        PageTransformerType.ZOOM_OUT -> {
            val scale = (1f - position.absoluteValue * 0.35f).coerceIn(0.65f, 1f)
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 0.5f)
            alpha = (1f - position.absoluteValue * 0.4f).coerceIn(0f, 1f)
        }

        PageTransformerType.ZOOM_OUT_SLIDE -> {
            if (position >= -1f && position <= 1f) {
                val scaleFactor = maxOf(0.85f, 1f - position.absoluteValue)
                val vertMargin = size.height * (1f - scaleFactor) / 2f
                val horzMargin = size.width * (1f - scaleFactor) / 2f
                translationX = if (position < 0f) horzMargin - vertMargin / 2f else -horzMargin + vertMargin / 2f
                scaleX = scaleFactor
                scaleY = scaleFactor
                alpha = 0.5f + (scaleFactor - 0.85f) / (1f - 0.85f) * 0.5f
            } else {
                alpha = 0f
            }
        }
    }
}
