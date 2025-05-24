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
#### درسنامه 5: MVVM و Context

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726"  -->
## در این درسنامه
- آشنایی با MVVM و viewModel
- بررسی lifecycle
- بررسی clean architecture
- آشنایی با Context

---
### MVVM
- stands for Model-View-ViewModel.
	- Model: data structure and interaction with data sources (database, API, etc.).
	- View: UI components.
	- viewModel: business logic (how/what data is accesses)
- an architecture design pattern that allow scaling project more easily.

---

![[MVVM.drawio.png]]

---
### in MVVM
- data changes are independent of user action (UI change)
	- activity or other components life cycle don't effect the data
- we control what view gets and what is can do in viewModel (format, filter, etc.)
- user actions: click, swipe screen, input text, etc.

---

![[viewModel form.drawio.png]]

---
### viewModel

![[Pasted image 20240308200302.png]]

---

![[Pasted image 20240308200548.png]]

---

```kotlin title:BackgroundColorViewModel.kt
package info.navidlabs.intenttutorial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class BackgroundColorViewModel {
    var backgroundColor by mutableStateOf(Color.White)
        private set

    fun changeBackgroundColor(newColor: Color = Color.Red) {
        backgroundColor = newColor
    }
}
```

---

```kotlin title:MainActivity.kt
package info.navidlabs.intenttutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme

class MainActivity : ComponentActivity() {
    private val bgviewModel = BackgroundColorViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileWriter = FileWriter(this)

        setContent {
            IntentTutorialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(bgviewModel.backgroundColor),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        bgviewModel.changeBackgroundColor()
                    }) {
                        Text(text = "Change background color to Red")
                    }
                }
            }
        }
    }
}
```

---
### wait a second

![[Pasted image 20240308202233.png]]

---

```kotlin title:BackgroundViewModel.kt
// ...
import androidx.lifecycle.ViewModel

class BackgroundColorViewModel: ViewModel() {
    // ...
}
```

```kotlin title:MainActivity.kt
//...
class MainActivity : ComponentActivity() {  
    private val bgviewModel by viewModels<BackgroundColorViewModel>()
//...
```

---

![[Pasted image 20240308202832.png]]

---
### it gets better!
```kotlin title:build.gradle.kt:app
	//...
	
	// lifecycle  
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
```

![[Pasted image 20240308203111.png]]

---
```kotlin
package info.navidlabs.intenttutorial

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntentTutorialTheme {
                val bgviewModel by viewModels<BackgroundColorViewModel>()

                Column(
                    modifier = Modifier.fillMaxSize().background(bgviewModel.backgroundColor),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        bgviewModel.changeBackgroundColor()
                    }) {
                        Text(text = "Change background color to Red")
                    }
                }
            }
        }
    }
}
```
---
### viewModel constructor

```kotlin title:MainActivity.kt
import androidx.lifecycle.viewmodel.compose.viewModel
//...
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntentTutorialTheme {
				 val bgviewModel = viewModel<BackgroundColorViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return BackgroundColorViewModel(message="testing") as T
                        }
                    }
                )
				
                Column(
                    modifier = Modifier.fillMaxSize().background(bgviewModel.backgroundColor),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        bgviewModel.changeBackgroundColor()
                    }) {
                        Text(text = "Change background color to Red")
                    }
                }
            }
        }
    }
}
```

---
```kotlin
package info.navidlabs.intenttutorial

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class BackgroundColorViewModel(
    val message: String
): ViewModel() {
    var backgroundColor by mutableStateOf(Color.White)
        private set

    fun changeBackgroundColor(newColor: Color = Color.Red) {
        backgroundColor = newColor
        Log.i("BackgroundViewModel", "message: $message")
    }
}
```

---
![[Pasted image 20240308205115.png]]

---
### Clear Architecture
- package structure should be understandable.
- small projects: domain, data, presentation packages.
- larger projects: each feature will has it's own package and domain, data, presentation.

---

```
├───auth
│   ├───data
│   │   ├───api
│   │   │   └───tests
│   │   └───database
│   │       └───tests
│   ├───domain
│   │   └───models
│   │       └───tests
│   └───presentation
│       ├───login
│       │   └───tests
│       └───signup
│           └───tests
└───profile \\similar to auth
```

---
### Activity Lifecycle

![[Pasted image 20240307180314.png]]

---

```kotlin
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // part of lifecycle
    }

    override fun onNewIntent(intent: Intent?) {
        // not a part of lifecycle
    }
}
```

---
### Context
- context is an instance of a class that act as bridge between your app and Android operating system 😕
- it allows your application to interact with the android operating system.
	- communicating to other apps (intetnts!)
	- getting resources (strings, images, etc.)
	- using databases and filesystem

---
#### example 1: intents
see previous session: [[04-Activity و Intent]]

---
#### example 2: writing to a file

```kotlin
package info.navidlabs.intenttutorial

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme
import java.io.File


class FileWriter(private val context: Context) {
    fun writeData(data: String) {
        val fileName = "temp.txt"
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val folder = context.filesDir
        val file = File(folder, fileName)
        try {
            file.createNewFile()
            file.appendText(data)
            Toast.makeText(context, "written to $folder/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("FileWriter", "Error writing to file: ${e.message}")
        }
    }
}


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileWriter = FileWriter(this)

        setContent {
            IntentTutorialTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        fileWriter.writeData("hello world\n")
                    }) {
                        Text(text = "Write")
                    }
                }
            }
        }
    }
}
```

---
### Android 10 and lower

```xml title:AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission
        android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />

    <application
	    ...
```

---

```sh
(adb) $ cat /data/user/0/info.navidlabs.intenttutorial/files/temp.txt
cat: /data/user/0/info.navidlabs.intenttutorial/files/temp.txt: Permission denied
(adb) $ exit
$ adb exec-out run-as info.navidlabs.intenttutorial cat /data/data/info.navidlabs.intenttutorial/files/temp.txt
hello world
hello world
hello world
```

---

![[context.drawio.png]]

---

```kotlin
import android.content.Context

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageViewModel>()
    val appContext: Context = this  // context!
    
    override fun onCreate(savedInstanceState: Bundle?) { <--ommited--> }
}
```

```kotlin
import android.content.Context

class MyApplication: Application() {
	override fun onCreate()
	val appContext: Context = this // also context!
}
```

---
### App Life Cycle
**1. onCreate()**: This method is called when the application is first created. It's responsible for any initial setup tasks, such as initializing resources, databases, or registering listeners.

**2. onTerminate()**: This method is called when the application is terminated by the system. It's typically used for cleanup tasks like releasing resources or saving data.

---
### App Life Cycle (continue)

**3. onLowMemory()**: This method is called when the system is running low on memory and needs to reclaim resources from applications. You can use this method to perform tasks like releasing cached data or freeing up memory-intensive objects.

---
### App Life Cycle (continue)
**4. onTrimMemory(int level)**: This method is a more granular version of `onLowMemory()`. It provides information about the severity of the memory situation by passing an `int level` parameter. You can use this information to decide on more specific actions to take, such as aggressively releasing resources or pausing background tasks.

---
### App Life Cycle (continue)

**5. onConfigurationChanged(Configuration newConfig)**: This method is called when the device configuration changes, such as when the screen orientation changes or the language settings are modified. You can use this method to update your UI layout or behavior to adapt to the new configuration.

---

![[text856.png]]