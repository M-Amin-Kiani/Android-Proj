---
tags:
  - Android
  - 1402_2
enableOverview: "true"
css:
  - css/shabnam.css
theme: black
sources:
  - https://betulnecanli.medium.com/state-management-in-jetpack-compose-e4fa4ccaec74#:~:text=%F0%9F%93%9DThere%20are%20two%20main,such%20as%20remember%20or%20mutableStateOf
---

<!-- slide dir="rtl" -->

![[University-of-Isfahan.png|200]]

## برنامه نویسی دستگاه‌های سیار
#### درسنامه 4: بررسی Intent و Activity

- ترم بهار 1402-1403 دانشگاه اصفهان
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02
- تمرین اول و دوم: تحویل 16 اسفند
- اعلام موضوع: 14 اسفند

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- مرور مدیریت state با remember و rememberSavable
- آشنایی با Log و Logcat
- آشنایی با Activity
- آشنایی با Intent
---
### برنامه‌ی شمارنده

![[Pasted image 20240229112110.png|300]]
---

![[Pasted image 20240229111224.png]]

---

![[Pasted image 20240229111303.png]]

---

![[Pasted image 20240229111518.png]]

---
### CoffeeCounter Composable

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeCounterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CoffeeCounter()
                }
            }
        }
    }
}

@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var coffeeCount = 0
        Text("You've had $coffeeCount cups of coffee.")
        Button(onClick = { coffeeCount++ }, Modifier.padding(top = 8.dp)) {
            Text("Add one")
        }
    }
}
```

---

![[Pasted image 20240229112500.png]]

---
### Problem
- when we click the button, **coffeeCount** does update, but the component does **NOT**! 🙇‍♂
- we need to re-render/update (**recomposite**) the component (**composition**)

### Solution
- use JC's state tracking with **MutableStateOf**

---
### Reactive Programming, Observable Design Pattern and MutableState
- some data changes
- some action should take place as the result

---

![[observable.drawio.png]]
created with **draw.io**

---

```kotlin
@SuppressLint("UnrememberedMutableState")  // 🙂
@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        val coffeeCount: MutableState<Int> = mutableStateOf(0)
        
        Text("You've had ${coffeeCount.value} cups of coffee.")
        Button(
	        onClick = { coffeeCount.value++ }, 
	        Modifier.padding(top = 8.dp)) {
            Text("Add one")
        }
    }
}
```

---
### یادداشت

Compose also has other variants of **mutableStateOf**, such as **mutableIntStateOf**, **mutableLongStateOf**, **mutableFloatStateOf**, or **mutableDoubleStateOf**, which are optimized for the primitive types.

- from `betulnecanli.medium.com`

---

### Still Does Not Work~!
```kotlin info:6
@SuppressLint("UnrememberedMutableState")  // 🙂
@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        val coffeeCount: MutableState<Int> = mutableStateOf(0)
        Log.d("coffeeCounter", "coffeCounter: $coffeeCount")  // debug
        
        Text("You've had ${coffeeCount.value} cups of coffee.")
        Button(
	        onClick = { coffeeCount.value++ }, 
	        Modifier.padding(top = 8.dp)) {
            Text("Add one")
        }
    }
}
```

---

![[Pasted image 20240229115952.png]]

---
### مشکل از کجاست؟

```kotlin
@SuppressLint("UnrememberedMutableState")  // 🤦
```

---
### remember 🔫
- we need to remember state between renders (recompositions)!

```kotlin
@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        val coffeeCount: MutableState<Int> = remember { mutableStateOf(0) }
        
        Text("You've had ${coffeeCount.value} cups of coffee.")
        Button(onClick = { coffeeCount.value++ }, Modifier.padding(top = 8.dp)) {
            Text("Add one")
        }
    }
}
```
---
![[Pasted image 20240229121540.png|300]]

---
### تمرین سوم
- در کد زیر چرا با کلیک روی دکمه چیزی در لاگ اضافه نمیشه؟

```kotlin
@Composable  
fun CoffeeCounter(modifier: Modifier = Modifier) {  
    Column(modifier = modifier.padding(16.dp)) {  
        var coffeeCount = 0  
        Log.d("coffeeCounter", "coffeCounter: $coffeeCount")  
  
        Text("You've had $coffeeCount cups of coffee.")  
        Button(onClick = { coffeeCount++ }, Modifier.padding(top = 8.dp)) {  
            Text("Add one")  
        }  
    }}
```

---
### By ✋

```kotlin
@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var coffeeCount by remember { mutableStateOf(0) }
 
		Text("You've had $coffeeCount cups of coffee.")
        Button(onClick = { coffeeCount++ }, Modifier.padding(top = 8.dp)) {
            Text("Add one")
        }
    }
}
```

read more: [_delegated properties_](https://kotlinlang.org/docs/delegated-properties.html)

---

![[Untitled Diagram.drawio 1.png]]

---

![[Pasted image 20240229121828.png]]

---
### Problem
- configuration changed ... state lost!
- configuration changes when user:
	- rotate screen
	- change keyboard language
	- change theme
	- ...

### Solution
- rememberSaveable

---

```kotlin
@Composable
fun CoffeeCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var coffeeCount by rememberSaveable{ mutableStateOf(0) }
        
        Text("You've had $coffeeCount cups of coffee.")
        Button(
	        onClick = { coffeeCount++ }, 
	        Modifier.padding(top = 8.dp),  
	        enabled = coffeeCount < 3) {
            Text("Add one")
        }
    }
}
```

---
### Stateful vs Stateless
https://betulnecanli.medium.com/state-management-in-jetpack-compose-e4fa4ccaec74#:~:text=%F0%9F%93%9DThere%20are%20two%20main,such%20as%20remember%20or%20mutableStateOf

---
### Log

```kotlin
import android.util.Log;

Log.e("ApiUrl = ", "MyApiUrl") // error
Log.w("ApiUrl = ", "MyApiUrl") // warning
Log.i("ApiUrl = ", "MyApiUrl") // information
Log.d("ApiUrl = ", "MyApiUrl") // debug
```

---

![[Pasted image 20240229113210.png]]

---
<!-- slide dir="rtl" -->
### Activity
- بلوک‌های ساختاری اندروید

---
<!-- slide dir="rtl" -->
### تفاوت توسعه برنامه اندروید با دسکتاپ
- نقطه شروع برنامه
	- دسکتاپ: تابع `main`
	- اندروید: یک activity (پیش‌فرض `MainActivity`)
- مثال: اجرا gmail در مقابل کلیک روی لینک

---

```kotlin
package info.navidlabs.myApplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Hello world!")
        }
    }
}
```

---
### Intent

![[intent.drawio.png]]

---
### Intent (cont.)
- intents are used to transfer data to another component (Activity, Service, Broadcast)
- **Explicit intent:** sending data to an specific component.
- **Implicit intent:** sending data to any app that supports that intent.

---
### Adding Activity
![[Pasted image 20240302085051.png]]

---

![[Pasted image 20240302085145.png]]

---
### AndroidManifest.xml
- shows what your application can do (permissions, activities, etc)

![[Pasted image 20240302085605.png]]

---
### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.IntentTutorial"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity2"
            android:exported="false"
            android:label="@string/title_activity_main2"
            android:theme="@style/Theme.IntentTutorial" />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.IntentTutorial">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---
### MainActivity
```kotlin
package info.navidlabs.intenttutorial

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        Intent(applicationContext, MainActivity2::class.java).also{ 
						    startActivity(it)
						}
                    }) {
                        Text("click me")
                    }
                }
            }
        }
    }
}
```

---
### MainActivity2
```kotlin
package info.navidlabs.intenttutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntentTutorialTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Second Activity")
                }
            }
        }
    }
}
```

---

![[Pasted image 20240302090517.png|300]]

---

![[Pasted image 20240302090551.png|300]]

---
### Calling External Apps

- use **adb** to finding packages URIs (`choco install adb`)

![[Pasted image 20240302091350.png]]

---

### MainActivity

```kotlin
package info.navidlabs.intenttutorial

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        Intent(Intent.ACTION_MAIN).also {
                            it.`package` = "com.google.android.youtube"
                            try {
                                startActivity(it)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                    }) {
                        Text("click me")
                    }
                }
            }
        }
    }
}
```

---

![[Pasted image 20240302091746.png|300]]

---
### Implicit Intent (sending Email)

```kotlin
package info.navidlabs.intenttutorial

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"  // mime type
                            putExtra(Intent.EXTRA_EMAIL, "admin@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Gooooogle")
                            putExtra(Intent.EXTRA_TEXT, "hello there")
                        }
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        }
                    }) {
                        Text("click me")
                    }
                }
            }
        }
    }
}
```

---
### warning?

![[Pasted image 20240302092705.png]]

---

```xml info:33-38
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.IntentTutorial"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity2"
            android:exported="false"
            android:label="@string/title_activity_main2"
            android:theme="@style/Theme.IntentTutorial" />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.IntentTutorial">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

    <queries>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>
</manifest>
```

---
![[Pasted image 20240302093511.png|300]]

---
### Intent filter
we want our app to receive specific types of intent and be displayed to other (like Gmail did in previous image)

---
### AndroidManifest.xml

```xml info:29-33
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.IntentTutorial"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity2"
            android:exported="false"
            android:label="@string/title_activity_main2"
            android:theme="@style/Theme.IntentTutorial" />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.IntentTutorial">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="image/*" />
            </intent-filter>
        </activity>
    </application>

    <queries>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>
</manifest>
```

---
![[Pasted image 20240302094149.png|300]]

---
### prevent opening a new instance
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.IntentTutorial"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity2"
            android:exported="false"
            android:label="@string/title_activity_main2"
            android:theme="@style/Theme.IntentTutorial" />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:label="@string/app_name"
            android:theme="@style/Theme.IntentTutorial">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="image/*" />
            </intent-filter>
        </activity>
    </application>

    <queries>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>

</manifest>
```

---
### Draw Intent Content
![[Pasted image 20240302095053.png]]

---

```kotlin
package info.navidlabs.intenttutorial

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ImageViewModel: ViewModel() {
    var uri: Uri? by mutableStateOf(null)
        private set
    
    fun updateUri(uri: Uri?) {
        this.uri = uri
    }
}
```

---
### update dependencies
- add **coil** for image loading ([link](https://github.com/coil-kt/coil))

![[Pasted image 20240302095652.png]]

---

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "info.navidlabs.intenttutorial"
    compileSdk = 34

    defaultConfig {
        applicationId = "info.navidlabs.intenttutorial"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // image loader
    implementation("io.coil-kt:coil-compose:2.6.0")
}
```

---
### MainActivity
```kotlin
package info.navidlabs.intenttutorial

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import info.navidlabs.intenttutorial.ui.theme.IntentTutorialTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntentTutorialTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    viewModel.uri?.let {
                        AsyncImage(
                            model = viewModel.uri,
                            contentDescription = null
                        )    
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        }

        viewModel.updateUri(uri)
    }
}
```

---
![[Pasted image 20240302100231.png | 300]]

---

![[Pasted image 20240302100316.png|300]]

---
### Back Stack

![[backstack.drawio.png]]

---
The `NavController` holds a "back stack" that contains the destinations the user has visited.

```kotlin
navController.popBackStack(R.id.destinationId, true)
```
- read use cases in https://developer.android.com/guide/navigation/backstack

