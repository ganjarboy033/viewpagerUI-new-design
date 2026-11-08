# ViewPager UI - Transformer Showcase in Jetpack Compose

A modern, high-performance showcase and library of smooth **ViewPager page transformer animations** rebuilt from the ground up for **Jetpack Compose** (Kotlin).

---

## 👨‍💻 Credits & Acknowledgments

- **Original Concept & Library:** Created by **[Suyono Ion](https://github.com/suyonoion)**. Full credit and appreciation to Suyono Ion for the original ViewPager UI foundation and creative transitions.
- **Modern Jetpack Compose Port & Redesign:** Modernized by **[Bagja Dev](https://bagjadev.blogspot.com)**.
  - Rewrote the architecture from legacy ViewPager to hardware-accelerated Jetpack Compose (`graphicsLayer`).
  - Added the **Real-time Telemetry Debug Panel** and **Interactive Scrubber Mode**.
  - Implemented the automated **GitHub Actions CI/CD pipeline** with instant error-extraction diagnostics.
- **Pioneering Open-Source Foundations:** Special thanks to ToxicBakery, daimajia, and Georgi Eftimov for early transformer research and algorithms.
- **Tools & Platforms:**
  - Built & engineered with **[Google AI Studio](https://aistudio.google.com)**.
  - Hosted and automated via **[GitHub](https://github.com)** & GitHub Actions.

---

## ✨ Features

- **18 Ported Page Transformers**:
  - **3D Effects**: `CubeIn`, `CubeOut`, `FlipHorizontal`, `FlipVertical`, `Tablet`
  - **Rotations**: `RotateDown`, `RotateUp`
  - **Zoom & Scale**: `ZoomIn`, `ZoomOut`, `ZoomOutSlide`, `DepthPage`, `BackgroundToForeground`, `ForegroundToBackground`, `DrawFromBack`
  - **Slide & Stack**: `Default`, `Accordion`, `Stack`, `ParallaxPage`
- **Interactive Control Modes**:
  - **Swipe Mode**: Natural touch drag & fling with Compose `HorizontalPager`
  - **Scrubber Mode**: Frame-by-frame offset scrub (`-1.00` to `+1.00`) to inspect 3D projection, scale, rotation angles, and alpha at exact positions
  - **Auto-Play Carousel**: Animated looping with customizable playback speeds (1.2s, 2.2s, 3.5s)
- **Theme Modes**:
  - **Classic Holo**: Preserves the original 5 colorful numbered ViewPager cards (`#33B5E5`, `#AA66CC`, `#99CC00`, `#FFBB33`, `#FF4444`)
  - **Modern M3**: Material 3 cards with gradient backdrops, floating parallax elements, and iconography
- **In-App Panels**:
  - **Developer & Identity Panel**: Full creator credits, links to [bagjadev.blogspot.com](https://bagjadev.blogspot.com), GitHub, and Google AI Studio.
  - **Real-Time Debug Panel**: Live telemetry (scale, rotation, alpha, offset fraction), JVM heap metrics, and quick stress testing.
  - **Code Inspector**: Interactive code snippets for developers to copy into their own projects.

---

## 🛠️ Tech Stack

- **Target Runtime**: Android (API 26+)
- **UI Framework**: Jetpack Compose (BOM 2024.09.00, Material 3 1.3.0)
- **Language**: Kotlin 2.2.10
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin 9.1.1 (Kotlin DSL)
