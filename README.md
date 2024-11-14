<p align="center">
    <img src="https://github.com/szwedzik/RssNews/blob/main/Images/rssnews.png" alt="logo"/>
</p>

# RssNews

RssNews is an Android application that allows users to read, favorite, and manage news articles fetched from RSS feeds. The project supports user authentication through email/password and Google Sign-In, with articles stored in Firebase Realtime Database. The application was created as part of the PRM (Mobile Programming) course at PJATK.

## Features

- User authentication via email/password and Google Sign-In
- Fetch news articles from RSS feeds
- Mark articles as read/unread
- Favorite/unfavorite articles
- Persist data using Firebase Realtime Database
- Support for local storage using SharedPreferences
- Push notifications for new articles
- Swipe to refresh for fetching new articles
- TopAppBar for category selection and account management

## Screenshots

### Login Screen
<img src="https://github.com/szwedzik/RssNews/blob/main/Images/login-screen.png" alt="Login Screen" width="300"/>

### Main Screen
<img src="https://github.com/szwedzik/RssNews/blob/main/Images/home-page.png" alt="Main Screen" width="300"/>

### Article Details
<img src="https://github.com/szwedzik/RssNews/blob/main/Images/webview.png" alt="Article Details" width="300"/>

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/szwedzik/rssnews.git
    ```

2. Open the project in Android Studio.

3. Build the project to install all dependencies.

4. Run the project on an Android emulator or a physical device.

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 30 or later
- Firebase project with Realtime Database and Authentication enabled
- Google services JSON file (`google-services.json`)

## Dependencies

- [Firebase](https://firebase.google.com/docs/android/setup) for authentication and database
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for asynchronous programming
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Accompanist](https://google.github.io/accompanist/swiperefresh/) for swipe to refresh

## Getting Started

### Firebase Configuration

1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Enable Authentication (Email/Password and Google Sign-In).
3. Enable Realtime Database.
4. Download the `google-services.json` file and place it in the `app` directory of your project.
5. Replace the placeholder in `strings.xml` with your Firebase key:

    ```xml
    <resources>
        <string name="app_name">RssNews</string>
        <string name="default_web_client_id">REPLACE_THIS_WITH_YOUR_FIREBASE_KEY</string>
    </resources>
    ```

### Setting Up Authentication

We use Firebase Authentication for user login and registration.

```kotlin
class FirebaseAuthManager(activity: Activity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    // Other authentication methods...
}
```

### Fetching Articles
We use RssParser to fetch and parse articles from RSS feeds.
```kotlin
class RssViewModel : ViewModel() {
    private val rssParser: RssParser = RssParser()

    fun fetchArticles(url: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val channel = rssParser.getRssChannel(url)
                val articles = channel.items.map { article ->
                    Article(
                        title = article.title ?: "",
                        link = article.link ?: "",
                        description = cleanDescription(article.description ?: ""),
                        imageUrl = article.image ?: null,
                        categories = article.categories,
                        pubDate = article.pubDate ?: "",
                        creator = article.author ?: "",
                        isRead = sharedPreferencesManager.isArticleRead(article.link ?: ""),
                        isFavorite = sharedPreferencesManager.isArticleFavorite(article.link ?: "")
                    )
                }
                _articles.value = articles
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
```

### Handling Favorites
Favorites are stored in Firebase Realtime Database.
```kotlin
class FirebaseDatabaseManager {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun saveFavoriteArticle(article: Article) {
        val articleId = article.link.hashCode().toString()
        database.child("favorite_articles").child(articleId).setValue(article)
    }

    fun getFavoriteArticles(): List<Article> {
        val snapshot = database.child("favorite_articles").get().await()
        val articles = snapshot.children.mapNotNull { it.getValue(Article::class.java) }
        return articles
    }
}
```

### Push Notifications
Push notifications are handled using WorkManager.
```kotlin
class ArticleCheckWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Check for new articles and send notifications
        return Result.success()
    }
}
```

### Permissions
The application requires the following permissions:

- INTERNET
- ACCESS_NETWORK_STATE

Permissions are requested at runtime for devices running Android M (API 23) or later.

```kotlin
private fun checkPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), REQUEST_CODE)
    }
}
```

## Contributing
1. Fork the repository
2. Create your feature branch (git checkout -b feature/my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin feature/my-new-feature)
5. Create a new Pull Request

# License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/szwedzik/RssNews?tab=MIT-1-ov-file#readme) file for details.
