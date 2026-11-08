package com.example.viewpagertransformers.model

enum class TransformerCategory(val label: String) {
    ALL("All"),
    THREE_D("3D Effects"),
    ROTATION("Rotations"),
    ZOOM("Zoom & Scale"),
    SLIDE_STACK("Slide & Stack")
}

enum class PageTransformerType(
    val title: String,
    val category: TransformerCategory,
    val description: String,
    val tags: List<String>,
    val codeSnippet: String
) {
    DEFAULT(
        title = "Default",
        category = TransformerCategory.SLIDE_STACK,
        description = "Standard horizontal slide transition without custom scaling or rotations.",
        tags = listOf("Standard", "Smooth", "Default"),
        codeSnippet = """
            Modifier.graphicsLayer {
                // Default linear slide
                translationX = 0f
                alpha = 1f
            }
        """.trimIndent()
    ),
    ACCORDION(
        title = "Accordion",
        category = TransformerCategory.SLIDE_STACK,
        description = "Compresses the page horizontally around its inner edge, resembling an accordion fold.",
        tags = listOf("Folding", "ScaleX", "Accordion"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (position < 0f) 0f else 1f,
                    pivotFractionY = 0.5f
                )
                scaleX = if (position < 0f) 1f + position else 1f - position
            }
        """.trimIndent()
    ),
    BACKGROUND_TO_FOREGROUND(
        title = "BackgroundToForeground",
        category = TransformerCategory.ZOOM,
        description = "Left page stays at full scale while right page scales up from 0.5 to full size as it arrives.",
        tags = listOf("Depth", "Scale", "Entrance"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                val scale = minOf(if (position < 0f) 1f else 1f - position, 0.5f).coerceAtLeast(0.5f)
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                translationX = if (position < 0f) size.width * position else -size.width * position * 0.25f
            }
        """.trimIndent()
    ),
    FOREGROUND_TO_BACKGROUND(
        title = "ForegroundToBackground",
        category = TransformerCategory.ZOOM,
        description = "Current page scales down into the background as it departs while next page slides in.",
        tags = listOf("Receding", "Scale", "Exit"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                val scale = minOf(if (position > 0f) 1f else 1f + position, 0.5f).coerceAtLeast(0.5f)
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                translationX = if (position > 0f) size.width * position else -size.width * position * 0.25f
            }
        """.trimIndent()
    ),
    CUBE_IN(
        title = "CubeIn",
        category = TransformerCategory.THREE_D,
        description = "Pages fold inward 90 degrees around their touching edges like the inside surfaces of a cube.",
        tags = listOf("3D Cube", "RotationY", "Inward"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                cameraDistance = 12f * density
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (position > 0f) 0f else 1f,
                    pivotFractionY = 0.5f
                )
                rotationY = -90f * position
            }
        """.trimIndent()
    ),
    CUBE_OUT(
        title = "CubeOut",
        category = TransformerCategory.THREE_D,
        description = "Pages fold outward 90 degrees around their edges like revolving the outside faces of a cube.",
        tags = listOf("3D Cube", "RotationY", "Outward"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                cameraDistance = 12f * density
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (position < 0f) 1f else 0f,
                    pivotFractionY = 0.5f
                )
                rotationY = 90f * position
            }
        """.trimIndent()
    ),
    DEPTH_PAGE(
        title = "DepthPage",
        category = TransformerCategory.ZOOM,
        description = "Left page slides normally while incoming right page emerges from beneath with fade and subtle zoom.",
        tags = listOf("Elevation", "Fade", "Layered"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                if (position <= 0f) {
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                } else if (position <= 1f) {
                    val scaleFactor = 0.75f + (1f - 0.75f) * (1f - position.absoluteValue)
                    alpha = 1f - position
                    translationX = size.width * -position
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            }
        """.trimIndent()
    ),
    DRAW_FROM_BACK(
        title = "DrawFromBack",
        category = TransformerCategory.ZOOM,
        description = "Recedes the current page to the background while smoothly revealing the incoming page behind.",
        tags = listOf("Behind", "Fade", "Scale"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                if (position <= 0f) {
                    alpha = 1f + position
                    translationX = size.width * -position
                    val scale = 0.75f + 0.25f * (1f - position.absoluteValue)
                    scaleX = scale
                    scaleY = scale
                } else {
                    val scale = 0.75f + (0.3f - position).coerceIn(0f, 0.25f)
                    scaleX = scale
                    scaleY = scale
                    translationX = size.width * position
                }
            }
        """.trimIndent()
    ),
    FLIP_HORIZONTAL(
        title = "FlipHorizontal",
        category = TransformerCategory.THREE_D,
        description = "Flips the page around its vertical center Y-axis like turning a physical playing card.",
        tags = listOf("3D Flip", "Card", "RotationY"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                cameraDistance = 12f * density
                val rotation = 180f * position
                rotationY = rotation
                alpha = if (rotation.absoluteValue > 90f) 0f else 1f
            }
        """.trimIndent()
    ),
    FLIP_VERTICAL(
        title = "FlipVertical",
        category = TransformerCategory.THREE_D,
        description = "Flips the page around its horizontal center X-axis like flipping a calendar pad.",
        tags = listOf("3D Flip", "Calendar", "RotationX"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                cameraDistance = 12f * density
                val rotation = -180f * position
                rotationX = rotation
                alpha = if (rotation.absoluteValue > 90f) 0f else 1f
            }
        """.trimIndent()
    ),
    PARALLAX_PAGE(
        title = "ParallaxPage",
        category = TransformerCategory.SLIDE_STACK,
        description = "Page elements move at differential rates creating a simulated multi-plane depth effect.",
        tags = listOf("Parallax", "Depth", "Multiplane"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                // Outer page container moves normally, while inner layers offset
                translationX = 0f
            }
        """.trimIndent()
    ),
    ROTATE_DOWN(
        title = "RotateDown",
        category = TransformerCategory.ROTATION,
        description = "Swings the page down like an inverted pendulum anchored to the bottom-center point.",
        tags = listOf("Pendulum", "Swing", "RotationZ"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                transformOrigin = TransformOrigin(0.5f, 1f)
                rotationZ = 18.75f * position
            }
        """.trimIndent()
    ),
    ROTATE_UP(
        title = "RotateUp",
        category = TransformerCategory.ROTATION,
        description = "Swings the page upward like an arch pendulum anchored to the top-center point.",
        tags = listOf("Arch", "Swing", "RotationZ"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                transformOrigin = TransformOrigin(0.5f, 0f)
                rotationZ = -15f * position
            }
        """.trimIndent()
    ),
    STACK(
        title = "Stack",
        category = TransformerCategory.SLIDE_STACK,
        description = "Left pages stay static pinned in place while subsequent pages slide over the top of the stack.",
        tags = listOf("Stack", "Deck", "SlideOver"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                translationX = if (position < 0f) 0f else -size.width * position
            }
        """.trimIndent()
    ),
    TABLET(
        title = "Tablet",
        category = TransformerCategory.THREE_D,
        description = "Angled 3D rotation simulating opening the pages of a tablet book with perspective offset.",
        tags = listOf("Book", "Tablet", "Perspective"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                cameraDistance = 12f * density
                transformOrigin = TransformOrigin(0.5f, 0f)
                val rotation = (if (position < 0f) 30f else -30f) * position.absoluteValue
                rotationY = rotation
            }
        """.trimIndent()
    ),
    ZOOM_IN(
        title = "ZoomIn",
        category = TransformerCategory.ZOOM,
        description = "Page scales up towards the user viewport as it enters, accompanied by subtle alpha fade.",
        tags = listOf("Scale", "Zoom", "Dramatic"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                val scale = if (position < 0f) position + 1f else (1f - position).absoluteValue
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                alpha = if (position < -1f || position > 1f) 0f else (2f - scale).coerceIn(0f, 1f)
            }
        """.trimIndent()
    ),
    ZOOM_OUT(
        title = "ZoomOut",
        category = TransformerCategory.ZOOM,
        description = "Page scales down smoothly as user swipes between pages and restores at rest.",
        tags = listOf("Scale", "Zoom", "Smooth"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
                val scale = 1f + position.absoluteValue
                val invScale = 1f / scale
                scaleX = invScale
                scaleY = invScale
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                alpha = (1f - position.absoluteValue * 0.5f).coerceIn(0f, 1f)
            }
        """.trimIndent()
    ),
    ZOOM_OUT_SLIDE(
        title = "ZoomOutSlide",
        category = TransformerCategory.ZOOM,
        description = "The classic Android SDK documentation zoom-out slide transition that shrinks page margins.",
        tags = listOf("Classic", "Official", "Scale & Alpha"),
        codeSnippet = """
            Modifier.graphicsLayer {
                val position = pageOffset
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
        """.trimIndent()
    );

    companion object {
        fun fromIndex(index: Int): PageTransformerType =
            entries.getOrNull(index) ?: DEFAULT
    }
}
