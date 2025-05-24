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
#### درسنامه 7: WorkManager

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با ورکر و تسک منیجر

---
<!-- slide dir="rtl" -->
### چرا تسک منیجر
- تسک طولانی و در پس زمینه (بدون نوتیفیکیشن)
- سرویس در پس زمینه ممکنه توسط اندروید بسته بشه
- یه چیزی میخوایم که مطمئن باشیم کارمون تا پایان انجام میشه
- به صورت دوره‌ای کاری را انجام بدیم (نه فقط یکبار)
---
### پروژه
- یه تصویر را انقدر فشرده کنیم که به یه سایز مشخص برسه
- بر اساس پروژه اینتنت: امکان دریافت تصویر با اینتنت را اضافه کردین

---
<!-- slide dir="rtl" -->
### پیش‌نیاز
- [لینک](https://github.com/blueBye/IntentTutorial.git) پروژه غیر اولیه (شامل اینتنت و رنگ پس زمینه و نوشتن در فایل و غیره)
- **وابستگی:** کویل و ورکر (در فایل `build.gradle(:app)`)

```kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
implementation("androidx.work:work-runtime-ktx:2.9.0")
implementation("androidx.compose.runtime:runtime-livedata:1.6.4")
```
- تعریف intent-filter در `AndroidManifest.xml`
	- نوع: `android:launchMode="singleTop"`

```xml
<intent-filter>
	<action android:name="android.intent.action.SEND" />
	<category android:name="android.intent.category.DEFAULT" />
	<data android:mimeType="image/*" />
</intent-filter>
```

---
### تعریف ورکر
- `PhotoCompressionWorker.kt`

```kotlin
package info.navidlabs.intenttutorial

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PhotoCompressionWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
```

---
### تعریف ورکر

```kotlin
package info.navidlabs.intenttutorial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

class PhotoCompressionWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
		// use withContext to put task on a tread optimized for IO
        return withContext(Dispatchers.IO) {
	        // read data using companion object
            val stringUri = params.inputData.getString(KEY_CONTENT_URI)
            val compressionThresholdInBytes = params.inputData.getLong(
                KEY_COMPRESSION_THRESHOLD,
                0L
            )
            // use image's uri to read image bytes, return failure if image not provided
            val uri = Uri.parse(stringUri)
            val bytes = appContext.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()

			// decode image byes using BitmapFactory
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputBytes: ByteArray
            var quality = 100
	        // iterate and compress file until quality is lower that five of meet threshold for file sie
            do {
                val outputStream = ByteArrayOutputStream()
                outputStream.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    outputBytes = outputStream.toByteArray()
                    quality -= (quality * 0.1).roundToInt()
                }
            } while (outputBytes.size > compressionThresholdInBytes && quality > 5)
			// write file to cache (not permanant) using worker id
            val file = File(appContext.cacheDir, "${params.id}.jpg")
            file.writeBytes(outputBytes)
            
			// return success code and file path
            return@withContext Result.success(
                workDataOf(
                    KEY_RESULT_PATH to file.absolutePath
                )
            )
        }
    }

	// companion object for better readability
    companion object {
        const val KEY_CONTENT_URI = "KEY_CONTENT_URI"
        const val KEY_COMPRESSION_THRESHOLD = "KEY_COMPRESSION_THRESHOLD"
        const val KEY_RESULT_PATH = "KEY_RESULT_PATH"
    }
}
```

---
### تعریف ویومدل برای عکس

```kotlin
package info.navidlabs.intenttutorial  
  
import android.graphics.Bitmap  
import android.net.Uri  
import androidx.compose.runtime.getValue  
import androidx.compose.runtime.mutableStateOf  
import androidx.compose.runtime.setValue  
import androidx.lifecycle.ViewModel  
import java.util.UUID  
  
class PhotoViewModel: ViewModel() {  
    var uncompressedUri: Uri? by mutableStateOf(null)  
        private set  
    var compressedBitmap: Bitmap? by mutableStateOf(null)  
        private set  
    var workId: UUID? by mutableStateOf(null)  
        private set  
  
    fun updateUncompressedUri(uri: Uri?) {  
        uncompressedUri = uri  
    }  
  
    fun updateCompressedBitmap(bitmap: Bitmap?) {  
        compressedBitmap = bitmap  
    }  
  
    fun updateWorkId(uuid: UUID?) {  
        workId = uuid  
    }  
}
```

---
### تعریف اکتیویتی

```kotlin
package info.navidlabs.intenttutorial  
  
import android.content.Intent  
import android.graphics.BitmapFactory  
import android.net.Uri  
import android.os.Build  
import android.os.Bundle  
import androidx.activity.ComponentActivity  
import androidx.activity.compose.setContent  
import androidx.activity.viewModels  
import androidx.compose.foundation.Image  
import androidx.compose.foundation.layout.Arrangement  
import androidx.compose.foundation.layout.Column  
import androidx.compose.foundation.layout.Spacer  
import androidx.compose.foundation.layout.fillMaxSize  
import androidx.compose.foundation.layout.height  
import androidx.compose.material3.Text  
import androidx.compose.runtime.LaunchedEffect  
import androidx.compose.runtime.livedata.observeAsState  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.graphics.asImageBitmap  
import androidx.compose.ui.unit.dp  
import androidx.work.Constraints  
import androidx.work.OneTimeWorkRequestBuilder  
import androidx.work.WorkManager  
import androidx.work.workDataOf  
import coil.compose.AsyncImage  
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme  
  
class MainActivity : ComponentActivity() {  
  
    private lateinit var workManager: WorkManager  
    private val viewModel by viewModels<PhotoViewModel>()  
  
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        workManager = WorkManager.getInstance(applicationContext)  
  
        setContent {  
            IntentTutorialTheme {  
                Column(  
                    modifier = Modifier.fillMaxSize(),  
                    verticalArrangement = Arrangement.Center,  
                    horizontalAlignment = Alignment.CenterHorizontally,  
                ) {  
                    val workerResult = viewModel.workId?.let { id ->  
                        workManager.getWorkInfoByIdLiveData(id).observeAsState().value  
                    }  
                    LaunchedEffect(key1 = workerResult?.outputData) {  
                        if (workerResult?.outputData != null) {  
                            val filePath = workerResult.outputData.getString(  
                                PhotoCompressionWorker.KEY_RESULT_PATH  
                            )  
  
                            filePath?.let {  
                                val bitmap = BitmapFactory.decodeFile(it)  
                                viewModel.updateCompressedBitmap(bitmap)  
                            }  
                        }  
                    }  
                }  
                Column(  
                    modifier = Modifier.fillMaxSize(),  
                    verticalArrangement = Arrangement.Center,  
                    horizontalAlignment = Alignment.CenterHorizontally,  
                ) {  
                    viewModel.uncompressedUri?.let {  
                        Text(text="Uncompressed Photo")  
                        AsyncImage(model = it, contentDescription = null)  
                    }  
                    Spacer(modifier=Modifier.height(16.dp))  
                    viewModel.compressedBitmap?.let {  
                        Text(text="Compressed Photo")  
                        Image(bitmap = it.asImageBitmap(), contentDescription = null)  
                    }  
                }            }        }    }  
  
    override fun onNewIntent(intent: Intent?) {  
        super.onNewIntent(intent)  
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)  
        } else {  
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)  
        } ?: return  
  
        viewModel.updateUncompressedUri(uri)  
  
        val request = OneTimeWorkRequestBuilder<PhotoCompressionWorker>()  
            .setInputData(  
                workDataOf(  
                    PhotoCompressionWorker.KEY_CONTENT_URI to uri.toString(),  
                    PhotoCompressionWorker.KEY_COMPRESSION_THRESHOLD to 1024 * 20L,  
                )  
            )  
            .setConstraints(Constraints(requiresStorageNotLow = true))  
            .build()  
  
        viewModel.updateWorkId(request.id)  
        workManager.enqueue(request)  
    }  
}
```

---
### پیاده‌سازی ورکر
```kotlin
package info.navidlabs.intenttutorial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

class PhotoCompressionWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val stringUri = params.inputData.getString(KEY_CONTENT_URI)
            val compressionThresholdInBytes = params.inputData.getLong(
                KEY_COMPRESSION_THRESHOLD,
                0L
            )
            val uri = Uri.parse(stringUri)
            val bytes = appContext.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputBytes: ByteArray
            var quality = 100
            do {
                val outputStream = ByteArrayOutputStream()
                outputStream.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    outputBytes = outputStream.toByteArray()
                    quality -= (quality * 0.1).roundToInt()
                }
            } while (outputBytes.size > compressionThresholdInBytes && quality > 5)

            val file = File(appContext.cacheDir, "${params.id}.jpg")
            file.writeBytes(outputBytes)

            return@withContext Result.success(
                workDataOf(
                    KEY_RESULT_PATH to file.absolutePath
                )
            )
        }

    }

    companion object {
        const val KEY_CONTENT_URI = "KEY_CONTENT_URI"
        const val KEY_COMPRESSION_THRESHOLD = "KEY_COMPRESSION_THRESHOLD"
        const val KEY_RESULT_PATH = "KEY_RESULT_PATH"
    }
}
```

---
<!-- slide dir="rtl" -->
### انواع ورکر
- **Immediate:** همون لحظه یا در اولین فرصتی که منابع موجود بود اجرا میشه.
- **deferrable:** زمانی که یکسری شرایط برقرار بشه اجرا میشه (مثلا دستگاه در حالت idle باشه و اینترنت وصله).
- **long running:** به صورت طولانی مدت اجرا میشه و اگه دستگاه خاموش شد و به هر مشکلی خوردیم، دوباره خودکار شروع میشه (آپلود فایل بزرگ).

---
### انواع کار
- **یکبار اجرا:** فقط یکبار اجرا میشه (همون موقع یا وقتی که شرایط درست بود)

![[Pasted image 20240409064105.png | 600]]

---
### انواع کار (ادامه)
- **دوره‌ای:** به صورت دوره‌ای اجرا میشه

![[Pasted image 20240409064527.png | 500]]

---
### کار دوره‌ای

```kotlin title:"build.gradl.kts (:app)"
implementation("androidx.work:work-runtime-ktx:2.9.0")
```

```kotlin
package info.navidlabs.androidprogrammingclass

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class CustomWorker constructor(
    context: Context,
    workerParameters: WorkerParameters,
): CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        delay(60000)
        Log.d("Custom Worker", "working!!")
        return Result.success()
    }
}
```

---

```kotlin title:MainActivity.kt
package info.navidlabs.androidprogrammingclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import info.navidlabs.androidprogrammingclass.ui.theme.AndroidProgrammingClassTheme
import java.time.Duration
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProgrammingClassTheme {
                LaunchedEffect(key1 = Unit) {
                    val workRequest = PeriodicWorkRequestBuilder<CustomWorker>(
                        repeatInterval = 1,
                        repeatIntervalTimeUnit = TimeUnit.HOURS,
                    ).setBackoffCriteria(
                        backoffPolicy = BackoffPolicy.LINEAR,
                        duration = Duration.ofSeconds(15)
                    ).build()

                    val workManager = WorkManager.getInstance(applicationContext)
                    workManager.enqueue(workRequest)
                }
            }
        }
    }
}
```

---

![[Pasted image 20240409075809.png]]

---

![[Pasted image 20240409075946.png]]

![[Pasted image 20240409080036.png]]

---
### فقط یه ورکر

```kotlin
workManager.enqueueUniquePeriodicWork(  
    "myWorkerName",  
    ExistingPeriodicWorkPolicy.KEEP,  
    workRequest  
)
```
