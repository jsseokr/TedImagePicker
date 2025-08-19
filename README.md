# TedImagePicker [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-TedImagePicker-green.svg?style=flat)](https://android-arsenal.com/details/1/7697)

TedImagePicker is **simple/beautiful/smart** image picker

- Support Image/Video/Image&Video
- Support Single/Multi select
- Support more configuration option
- Support Preview with ViewPager2

|       Image Select        |    Select Album    |          Scroller           |
| :-----------------------: | :----------------: | :-------------------------: |
| ![](art/multi_select.png) | ![](art/album.png) | ![](art/scroll_handler.png) |

</br></br>

## Demo

![](art/full.gif)

|       Image Select        |    Select Album    |          Scroller           |
| :-----------------------: | :----------------: | :-------------------------: |
| ![](art/multi_select.gif) | ![](art/album.gif) | ![](art/scroll_handler.gif) |

</br></br>

## Setup

### Gradle

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ParkSangGwon/tedimagepicker.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ParkSangGwon%22%20AND%20a:%tedimagepicker%22)

```gradle

repositories {
  google()
  mavenCentral()
}

dependencies {
    implementation 'io.github.ParkSangGwon:tedimagepicker:x.y.z'
    //implementation 'io.github.ParkSangGwon:tedimagepicker:1.6.1'
}

```

If you think this library is useful, please press star button at upside. </br>
<img src="https://phaser.io/content/news/2015/09/10000-stars.png" width="200">

</br></br>

## How to use

### 1.Enable databinding

- TedImagePicker use databinding
- Set enable databinding in your app `build.gradle`

```
dataBinding {
    enabled = true
}

or

buildFeatures {
    dataBinding = true
}
```

### 2.Start TedImagePicker/TedRxImagePicker

- TedImagePicker support `Listener` and `RxJava`style

#### Listener

##### Single image

- Kotlin

```kotlin
TedImagePicker.with(this)
    .start { uri -> showSingleImage(uri) }
```

- Java

```java
TedImagePicker.with(this)
        .start(new OnSelectedListener() {
            @Override
            public void onSelected(@NotNull Uri uri) {
                showSingleImage(uri);
            }
        });
TedImagePicker.with(this)
        .start(uri -> {
            showSingleImage(uri);
        });
```

##### Multi image

```kotlin
TedImagePicker.with(this)
    .startMultiImage { uriList -> showMultiImage(uriList) }
```

```java
TedImagePicker.with(this)
        .startMultiImage(new OnMultiSelectedListener() {
            @Override
            public void onSelected(@NotNull List<Uri> uriList) {
                showMultiImage(uriList);
            }
        });
```

#### RxJava

```kotlin
TedRxImagePicker.with(this)
    .start()
    .subscribe({ uri -> showSingleImage(uri) }, { throwable -> showError(throwable) })

TedRxImagePicker.with(this)
    .startMultiImage()
    .subscribe({ uriList -> showMultiImage(uriList) }, { throwable -> showError(throwable) })
```

### 3.Preview Media with ViewPager2

- Support both image and video preview
- Swipe left/right to navigate
- Show selection status
- Auto-detect media type
- **Selection changes are automatically applied**

```kotlin
// Preview all media with current selection
val allMediaUris = listOf(uri1, uri2, uri3, ...)
val selectedUris = listOf(uri1, uri3)

// Basic preview
TedImagePicker.with(this)
    .preview(allMediaUris, selectedUris)

// Preview with result callback
TedImagePicker.with(this)
    .previewResultListener { updatedSelectedUris ->
        // Handle updated selection
        selectedUris = updatedSelectedUris
        updateUI()
    }
    .preview(allMediaUris, selectedUris)

// Or use RxJava style
TedRxImagePicker.with(this)
    .previewResultListener { updatedSelectedUris ->
        // Handle updated selection
        selectedUris = updatedSelectedUris
        updateUI()
    }
    .preview(allMediaUris, selectedUris)
```

```java
// Java
TedImagePicker.with(this)
    .previewResultListener(new OnPreviewResultListener() {
        @Override
        public void onPreviewResult(List<Uri> updatedSelectedUris) {
            // Handle updated selection
            selectedUris = updatedSelectedUris;
            updateUI();
        }
    })
    .preview(allMediaUris, selectedUris);

TedRxImagePicker.with(this)
    .previewResultListener(new OnPreviewResultListener() {
        @Override
        public void onPreviewResult(List<Uri> updatedSelectedUris) {
            // Handle updated selection
            selectedUris = updatedSelectedUris;
            updateUI();
        }
    })
    .preview(allMediaUris, selectedUris);
```

#### Preview Features

- **Media Type Detection**: Automatically detects image vs video
- **Image Support**: Uses GestureImageView for zoom/pan
- **Video Support**: Uses ExoPlayer for video playback
- **Selection Toggle**: Tap selection icon to toggle selection
- **Navigation**: Swipe left/right or use position indicator
- **Lifecycle Management**: Automatically pauses/resumes video playback
- **Real-time Updates**: Selection changes are immediately reflected in the UI
- **Result Callback**: Get notified when selection changes via `previewResultListener`
- **State Persistence**: Selection state is maintained during navigation

### 4.More configuration

```kotlin
TedImagePicker.with(this)
    .mediaType(MediaType.IMAGE_AND_VIDEO)
    .selectType(SelectType.MULTI)
    .maxCount(10)
    .minCount(1)
    .showCameraTile(true)
    .showTitle(true)
    .title("Select Media")
    .buttonText("Done")
    .startMultiImage { uriList ->
        // Handle selected media
    }
```

## Features

- **Media Types**: Image, Video, Image & Video
- **Selection Modes**: Single, Multi
- **Preview**: Full-screen preview with ViewPager2
- **Selection Management**: Real-time selection updates with callbacks
- **Camera Integration**: Built-in camera support
- **Permissions**: Automatic permission handling
- **RxJava Support**: Reactive programming support
- **DataBinding**: Modern Android development
- **Customization**: Extensive customization options
- **Performance**: Optimized for large media collections
