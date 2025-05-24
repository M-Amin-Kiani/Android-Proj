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
#### درسنامه8: Room

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با دیتابیس Room

---
<!-- slide dir="rtl" -->
### چرا دیتابیس
- نگهداری و بازیابی (جستجو و فیلتر) داده
- تعریف ارتباط بین داده‌های مختلف (Foreign key)
- کارایی و استاندارد

---
<!-- slide dir="rtl" -->
### انواع دیتابیس
- رابطه‌ای یا SQL: جدول و ستون و رابطه را تعریف میکنیم و داده باید این قالب را رعایت کنه تا ثبت بشه.
- غیر رابطه‌ای یا NoSQL: داده با هر فرمتی که بود ثبتش می‌کنیم.

![[Pasted image 20240412215250.png]]

---
### گزینه‌ها و مقایسه

|       -        | Realm                 | Room     |
| :------------: | --------------------- | -------- |
|      Type      | NoSQL                 | SQL      |
|    platform    | Android, iOS, Xamarin | Android  |
|  Performance   | Faster                | Slower   |
| Query Language | Realm Query Language  | SQL      |
| Thread Safety  | Requires Management   | Safe     |
|  Library Size  | Larger                | Smaller  |
| Data Migration | Manual                | Built-in |

---
### ساختار روم

![[Pasted image 20240413080020.png]]

---
### پروژه
- یه دفتر تلفن ساده

![[Pasted image 20240412214040.png | 300]]

---
<!-- slide -->
### وابستگی‌های
- `build.gradle(:project)`
    - **ksp usage:** annotation to mark classes as tables

```kotlin
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false  // added
}
```

>KSP stands for Kotlin Symbol Processing. It's an API that allows you to develop lightweight compiler plugins for Kotlin programs.

---
- `build.gradle(:app)`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")  // added
}

android {
	//...
	compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    // ...
}

dependencies {
	// ...
    // room
    val roomVersion = "2.5.0"
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
}
```

---
<!-- slide dir="rtl" -->
### مراحل استفاده از دیتابیس
- تعریف ساختار داده (`Contact.kt`)
- تعریف Data Access Object یا شی دسترسی به داده (`ContactDao.kt`) 
- تعریف پایگاه داده (`ContactDatabase.kt`)
- تعریف رویدادهای کار با داده (`ContactEvent.kt`)
- تعریف کلاس وضعیت برای فرم و رابط کاربری (`ContactState.kt`)
- تعریف ViewModel برای کنترل/آپدیت رابط کاربری (`ContactViewModel.kt`)
- تعریف کامپوزبل رابط کاربری (`ContactScreen.kt`)
- استفاده از کامپوزبل در اکتیویتی

---
### تعریف ساختار داده

```kotlin
package info.navidlabs.androidprogrammingclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
)
```

---
### انواع شناسه
- uuid
	- not sortable (random)
	- 550e8400-e29b-41d4-a716–446655440000
- snowflake
	- sortable (embedded timestamp)
	- 7184775978768404481
- ulid
	- sortable (embedded timestamp)
	- 01HVAWW8Q6253HBAMF7JBH50V8

---
### مقایسه انواع شناسه

|Feature|UUID|Snowflake ID|ULID|
|---|---|---|---|
|Length|128 bits (string)|64 bits (integer)|26 characters|
|Ordering|No inherent order|Ordered by timestamp|Lexicographically sortable|
|Collision probability|Very low|Requires coordination|Very low|
|Commonality|Widely used|Less common|Less common|
|Storage efficiency|Lower|Higher|Medium|

---
### تعریف شی دسترسی به داده

```kotlin
package info.navidlabs.androidprogrammingclass

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Upsert  // insert and update
    suspend fun upsertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    // queries
    @Query("SELECT * FROM contact ORDER BY firstName ASC")

    @Query("SELECT * FROM contact ORDER BY lastName ASC")
    fun getContactsOrderedByLastName(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY phoneNumber ASC")
    fun getContactsOrderedByPhoneNumber(): Flow<List<Contact>>
}
```

---
### تعریف پایگاه داده

```kotlin
package info.navidlabs.androidprogrammingclass

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class],
    version = 1,
)
abstract class ContactDatabase: RoomDatabase() {
    abstract val dao: ContactDao
}

```

---
### تعریف رویدادهای کار با داده

```kotlin
package info.navidlabs.androidprogrammingclass

sealed interface ContactEvent {
    object SaveContact: ContactEvent  // singleton
    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent
    
    object ShowDialog: ContactEvent  // singleton 
    object HideDialog: ContactEvent  // singleton 
    
    data class SortContacts(val sortType: SortType): ContactEvent
    data class DeleteContact(val contact: Contact): ContactEvent
}
```

---

### Sealed Interface
Sealed interfaces restrict what can implement them. In this case, the only valid implementations are defined within this file.
- **Type Safety:** By using a sealed interface, the code ensures that only the defined event types can be used as ContactEvent.
- **Exhaustive Handling:** When using when expressions with ContactEvent, the compiler can warn you if you haven't handled all possible event types.

---
### Data Classes (for Modifying Contact Information)

- Used to represent events that modify specific properties of a contact (name or phone number).
    - They automatically provide essential methods like `equals`, `hashCode`, and `toString` which are important for comparing and representing these events.
    - They enforce immutability by making properties `val` (read-only). This ensures the event data remains consistent and avoids accidental modifications.

---
### Objects (for Simple Events)

- These events represent simple actions without any additional data. Objects are used when:
    - They are lightweight and efficient for simple events that don't require properties.
    - Using singletons ensures only one instance of these events exists, which makes sense for actions like showing/hiding a dialog.

---
### تعریف کلاس وضعیت برای فرم و رابط کاربری

```kotlin
package info.navidlabs.androidprogrammingclass

data class ContactState(
    val contacts: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME,
)
```

---

```kotlin
package info.navidlabs.androidprogrammingclass
// SortType.kt

enum class SortType {
    FIRST_NAME,
    LAST_NAME,
    PHONE_NUMBER,
}
```

---
### تعریف ویو مدل برای کنترل/آپدیت رابط کاربری

```kotlin
package info.navidlabs.androidprogrammingclass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel (
    private val dao: ContactDao
): ViewModel() {
    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _state = MutableStateFlow(ContactState())
    private val _contacts = _sortType  // triggers automatically when sortType changes
        .flatMapLatest {sortType ->
            when(sortType) {
                SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
                SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
            }
        }
        .stateIn(  // used for sharing flows with viewModels
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    // combine flows so that if any of the specified values changes, a new flow is executed
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType,
        )
    }.stateIn(
        viewModelScope,
        // Creates a new instance of the combined flow whenever a new collector subscribes. 
        // if the flow completes and then a new collector subscribes within 5 seconds, 
        // it will be restarted with the latest values. Otherwise, a new instance will be created
        SharingStarted.WhileSubscribed(5000),  
        ContactState()  //  initial value emitted by the flow before any data is combined
    )

    fun onEvent(event: ContactEvent) {
        when(event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                )
                viewModelScope.launch {
                    dao.upsertContact(contact)
                }
                _state.update {
                    it.copy(
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = "",
                    )
                }
            }
            ContactEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingContact = true
                ) }
            }
            ContactEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingContact = false
                ) }
            }
            is ContactEvent.SetFirstName -> {
                _state.update { it.copy(
                    firstName = event.firstName
                ) }
            }
            is ContactEvent.SetLastName -> {
                _state.update { it.copy(
                    lastName = event.lastName
                ) }
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update { it.copy(
                    phoneNumber = event.phoneNumber
                ) }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
        }
    }
}
```

---
### flowMapLatest

In Kotlin Coroutines with Flow, flatMapLatest is an operator used to transform one flow into another flow while handling emissions from the original flow in a specific way.

---
### flowMapLatest

**Functionality**
- It takes a lambda expression as an argument.
- Inside the lambda, it receives the emitted value from the original flow.
- Based on the emitted value, the lambda expression returns a new flow.
- flatMapLatest cancels any previously emitted new flow (created by the lambda) whenever a new value is emitted from the original flow. **This ensures that only the latest transformation based on the most recent emission is processed.**

---
### flowMapLatest

**Benefits**
- **Handling Rapid Emissions:** It's useful when you want to react to the latest value from the original flow and discard any ongoing transformations based on previous values.

---
### stateIn

**Arguments**
- **`viewModelScope`:** This argument specifies the coroutine scope used for internal coroutines launched by `stateIn`. By using `viewModelScope`, the coroutines launched for collecting the flow will be tied to the lifecycle of the ViewModel. **This means they will be automatically cancelled when the ViewModel is cleared** (e.g., when the fragment or activity using the ViewModel is destroyed).

---

- **`SharingStarted.WhileSubscribed()`:** This argument defines the sharing strategy for the flow. Sharing strategies determine how the flow behaves when there are multiple collectors (subscribers) interested in its emissions. Here, `SharingStarted.WhileSubscribed()` is used:
    - **`SharingStarted`:** This part tells `stateIn` to create a new instance of the original flow whenever the flow is started for collection (i.e., when a new collector subscribes).
    - **`WhileSubscribed()`:** This further specifies that the flow should be restarted if it completes while there are still active collectors. **This ensures that the UI always receives the latest data**, even if the flow completes and emits a final value at some point.

---

- `emptyList()`: This argument represents **the initial value that will be emitted by the flow before any data is actually collected**. In this case, an empty list is provided as the initial value for the contact list.
    
---

**Overall Functionality:**

By using `.stateIn` with these arguments, this code achieves the following:
- The flow (`_contacts` in this case) is **shared across the lifecycle of the ViewModel**.
- Whenever the **UI becomes interested** in collecting the data (subscribes to the flow), a **new instance of the original flow is created using** `SharingStarted`.
- This new instance ensures the **UI receives the latest data**, even if the flow has completed previously, because it will be restarted with `WhileSubscribed`.
- The **UI initially receives an empty list** (`emptyList()`) before any data is fetched from the database.

---
### تعریف کامپوزبل رابط کاربری (فرم)

```kotlin
package info.navidlabs.androidprogrammingclass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
           onEvent(ContactEvent.HideDialog)
        },
        confirmButton = {
            Button(onClick = {
                onEvent(ContactEvent.SaveContact)
            }) {
                Text(text = "Save Contact")
            }
        },
        title = { Text(text = "Add Contact") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.firstName,
                    onValueChange = {
                        onEvent(ContactEvent.SetFirstName(it))
                    },
                    placeholder = {
                        Text(text = "First Name")
                    }
                )

                TextField(
                    value = state.lastName,
                    onValueChange = {
                        onEvent(ContactEvent.SetLastName(it))
                    },
                    placeholder = {
                        Text(text = "Last Name")
                    }
                )

                TextField(
                    value = state.phoneNumber,
                    onValueChange = {
                        onEvent(ContactEvent.SetPhoneNumber(it))
                    },
                    placeholder = {
                        Text(text = "Phone Number")
                    }
                )
            }
        }
    )
}
```

---
### تعریف کامپوزبل رابط کاربری (لیست)

```kotlin
package info.navidlabs.androidprogrammingclass

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(ContactEvent.ShowDialog)
            }) {
                Icon(
                    imageVector= Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { padding ->
        if (state.isAddingContact) {
            AddContactDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier.clickable {
                                onEvent(ContactEvent.SortContacts(sortType))
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(ContactEvent.SortContacts(sortType))
                                }
                            )
                            Text(text=sortType.name)
                        }
                    }
                }
            }

            items(state.contacts) { contact ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(6f)
                    ) {
                        Text(
                            text="${contact.firstName} ${contact.lastName}",
                            fontSize = 20.sp
                        )
                        Text(
                            text="${contact.phoneNumber}",
                            fontSize = 12.sp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = {
                            onEvent(ContactEvent.DeleteContact(contact))
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
```

---
### استفاده از کامپوزبل در اکتیویتی

```kotlin
package info.navidlabs.androidprogrammingclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import info.navidlabs.androidprogrammingclass.ui.theme.AndroidProgrammingClassTheme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProgrammingClassTheme {
                val state by viewModel.state.collectAsState()
                ContactScreen(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}
```

