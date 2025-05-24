---
tags:
  - Android
  - 1402_2
enableOverview: "true"
css:
  - css/shabnam.css
theme: black
---

<!-- slide dir="rtl" -->

![[University-of-Isfahan.png|200]]

## برنامه نویسی دستگاه‌های سیار
#### درسنامه 11: Camerax

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با camerax
- تصویربرداری و ضبط ویدئو

---
### نیازمندی‌ها

```kotlin title:build.gradle.kts(:app)
...
// extended material icons
implementation("androidx.compose.material:material-icons-extended:1.6.6")
// viewmodel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//camerax
val cameraxVersion = "1.3.3"
implementation("androidx.camera:camera-core:$cameraxVersion")
implementation("androidx.camera:camera-camera2:$cameraxVersion")
implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
implementation("androidx.camera:camera-video:$cameraxVersion")
implementation("androidx.camera:camera-view:$cameraxVersion")
implementation("androidx.camera:camera-extensions:$cameraxVersion")
```

---
### مجوزها

```xml titl:AndroidManifest.xml
<uses-feature
	android:name="android.hardware.camera"
	android:required="false" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

---

![[Pasted image 20240427083416.png]]

---
### CameraPreview Composable
برای نمایش فید دوربین روی صفحه

```kotlin title:CameraPreview.kt
package info.navidlabs.androidprogrammingclass

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview (
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}
```

---
### GalleryPreview Composable
برای نمایش عکس‌هایی که گرفتیم

```kotlin title:GalleryPreview.kt
package info.navidlabs.androidprogrammingclass

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun GalleryPreview(
    bitmaps: List<Bitmap>,
    modifier: Modifier = Modifier
) {
    if (bitmaps.isEmpty()) {
        Box(
            modifier = modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Nothing to show!")
        }
    }
    else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier,
        ) {
            items(bitmaps) { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}
```

---
### GallaeryViewModel
برای نگهداری و کار با عکسها

```kotlin title:GallaryViewModel.kt
package info.navidlabs.androidprogrammingclass

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GalleryViewModel: ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
        Log.i("CAMERA", "added new photo")
    }
}
```

---
### MainActivity - Check Permissions

```kotlin
private fun checkPermissions(): Boolean {
	return CAMERAX_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
			applicationContext,
			it
		) == PackageManager.PERMISSION_GRANTED
	}
}

companion object {
	private val CAMERAX_PERMISSIONS = arrayOf(
		Manifest.permission.CAMERA,
		Manifest.permission.RECORD_AUDIO,
	)
}
```

---
### MainActivity - takePhoto

```kotlin
private fun takePhoto(
	controller: LifecycleCameraController,
	onPhoto: (Bitmap) -> Unit
) {
	controller.takePicture(
		ContextCompat.getMainExecutor(applicationContext),
		object : OnImageCapturedCallback() {
			override fun onCaptureSuccess(image: ImageProxy) {
				super.onCaptureSuccess(image)
				// rotate image
				val matrix = Matrix().apply {
					postRotate(image.imageInfo.rotationDegrees.toFloat())
//                        postScale(-1f, 1f)
				}
				val rotatedImage = Bitmap.createBitmap(
					image.toBitmap(),
					0,
					0,
					image.width,
					image.height,
					matrix,
					true,
				)
				// save
				onPhoto(rotatedImage)
			}

			override fun onError(exception: ImageCaptureException) {
				super.onError(exception)
				Log.e("CAMERA", "error: $exception")
			}
		}
	)
}
```

---
### MainActivity - UI

```kotlin
	@OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // basic permission handling
        if(!checkPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        setContent {
            AndroidProgrammingClassTheme {
                // state and constants
                val viewModel = viewModel<GalleryViewModel>()
                val bitmaps by viewModel.bitmaps.collectAsState()

                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                CameraController.VIDEO_CAPTURE
                        )
                    }
                }

                // ui
                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Drawer title", modifier = Modifier.padding(16.dp))
                            Divider()
                            GalleryPreview(bitmaps = bitmaps)
                        }
                    }
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier.fillMaxSize()
                        )

                        // camera switch button
                        IconButton(
                            onClick = {
                                controller.cameraSelector = if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                            },
                            modifier = Modifier.offset(16.dp, 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // controllers Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // take photo
                            IconButton(onClick = {
                                takePhoto(
                                    controller = controller,
                                    onPhoto = viewModel::onTakePhoto
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Take photo",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

	...
}
```

---
### Video Recording

```kotlin
	private fun recordVideo(controller: LifecycleCameraController) {
        if(recording != null) {
            recording?.stop()
            recording = null
            return
        }

        // save video on disk (can't be on memory)
        val outputFile = File(filesDir, "recording.mp4")

        // auto generated
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        recording = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(applicationContext)
        ) {event ->
            when(event) {
                is VideoRecordEvent.Finalize -> {
                    if(event.hasError()) {
                        recording?.close()
                        recording = null
                        Toast.makeText(
                            applicationContext,
                            "faced errors while recording",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "recording completed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
```

---
### Video Recording - UI

```kotlin
private var recording: Recording? = null

...

// record video
IconButton(onClick = {
	recordVideo(controller)
}) {
	Icon(
		imageVector = Icons.Default.Videocam,
		contentDescription = "Record video",
		tint = Color.White
	)
}
```

