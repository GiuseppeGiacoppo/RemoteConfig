# Introduction
[![](https://jitpack.io/v/GiuseppeGiacoppo/RemoteConfig.svg)](https://jitpack.io/#GiuseppeGiacoppo/RemoteConfig)

RemoteConfig is a Kotlin library that lets you manage all your remote configuration without requiring developers to manually download each configuration and integrate them into the Kotlin application.

![Library Architecture](https://github.com/GiuseppeGiacoppo/RemoteConfig/raw/master/readme/remoteconfig_image1.png)

You can have many configurations (messages, flags, server) on remote files, the library will do all the work for you.

![Multiple Configurations](https://github.com/GiuseppeGiacoppo/RemoteConfig/raw/master/readme/remoteconfig_image2.png)
## Download
Grab via Gradle:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.GiuseppeGiacoppo:RemoteConfig:LATEST_VERSION'
}
```
## Usage
Retrieve a specific instance of RemoteResource for every configuration class
```kotlin
fun welcome() {
    val remoteAppConfig = remoteConfig<AppConfig>()
    val appConfig = remoteAppConfig.get()
    println(appConfig.welcomeMessage)
}
```
You're done. `remoteAppConfig` will provide you the latest app configuration.
## Setup library
You need to setup each remote configuration with minimum effort. For each configuration, specify a remote repository and a local repository.
The library will know where to fetch the configuration and where to store it locally.
```kotlin
fun main(args: Array<String>) {
    initRemoteConfig {
        remoteResource<AppConfig>(
            storage("./configs"),
            network("https://www.your.server/latest/appconfig.json")
        )

        remoteResource<MessagesConfig>(
            // init other configs
        )
    }
}
```
### Default configuration
Fetching is an async operation, this means it can take a while, and it can fail.
It is possible to set a default configuration that will be marked as *active* if no more recent config is available.
```kotlin
val remoteAppConfig = remoteConfig<AppConfig>()
remoteAppConfig.setDefaultConfig(AppConfig("This is the default welcome message."))
```
### Fetch from the server
Fetch the configuration every time you need, invoking `fetch` method. Fresh configuration will be saved locally and you can activate it.
```kotlin
remoteAppConfig.fetch({
    println("Fetch is successful")
    remoteAppConfig.activateFetched()
}, {
    println("Fetch is failed")
    it.printStackTrace()
})
```
### Multiple configurations
The configuration will be named by the configuration class name. 
You can have multiple configurations that share the same class by specifying a custom resource name
```kotlin
fun main(args: Array<String>) {
    initRemoteConfig {
        remoteResource<MessagesConfig>(
            storage("./configs"),
            network("https://www.your.server/latest/homemessages.json")
        ) {
            resourceName = "home-messages"
        }

        remoteResource<MessagesConfig>(
            storage("./configs"),
            network("https://www.your.server/latest/detailmessages.json")
        ) {
            resourceName = "detail-messages"
        }
    }
    
    // you can then fetch, activate and use it
    val homeMessages = remoteConfig<MessagesConfig>("home-messages")
    val detailMessages = remoteConfig<MessagesConfig>("detail-messages")
}
```
### Configuration Format
RemoteConfig expects that each configuration is in json format. It supports also text format, and you can even create your own `ResourceMapper`.
```kotlin
fun main(args: Array<String>) {
    initRemoteConfig {
        remoteResource<String>(
            storage("./configs"),
            network("https://www.your.server/latest/custom.txt")
        ) {
            format = TextResourceMapper
        }
    }
}
```
## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Added some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :)

## Credits and libraries
RemoteConfig is an open source library inspired by [Firebase Remote Config](https://firebase.google.com/docs/remote-config)
* [Gson](https://github.com/google/gson)
* [OkHttp](http://square.github.io/okhttp)