# Introduction
[![Maven Central](https://img.shields.io/maven-central/v/me.giacoppo/remoteconfig.svg)](http://repo1.maven.org/maven2/me/giacoppo/remoteconfig/)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/https/oss.sonatype.org/me.giacoppo/remoteconfig.svg)](https://oss.sonatype.org/content/repositories/releases/me/giacoppo/remoteconfig/) 
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=14)

RemoteConfig is an Android library that lets you manage all your remote configuration without requiring developers to manually download  each configuration and integrate them into the application.

![Library Architecture](https://github.com/GiuseppeGiacoppo/RemoteConfig/raw/master/readme/architecture.png)

You can upload to your server many configurations (messages, flags, values and so on) in json files and the library will do all the work as fetching, storing them and making them available all over your app.

![Multiple Configurations](https://github.com/GiuseppeGiacoppo/RemoteConfig/raw/master/readme/multiple_configurations.png)

## Wiki
A **complete and detailed** wiki is available [here](https://github.com/GiuseppeGiacoppo/RemoteConfig/wiki)

## Download
Grab via Gradle:
```groovy
implementation 'me.giacoppo:remoteconfig:LATEST_VERSION'
```

## Usage
Retrieve a specific instance of RemoteResource for every configuration class
```java
RemoteResource<AppConfig> remoteAppConfig = RemoteConfig.of(AppConfig.class);
RemoteResource<MessagesConfig> remoteMessagesConfig = RemoteConfig.of(MessagesConfig.class);
```
You're done. `remoteAppConfig` and `remoteMessagesConfig` will have the last activated values for app and messages configurations.

You will get an instance of a specific configuration with the `get()` method:
```java
AppConfig appConfig = remoteAppConfig.get();
String apiEndPoint = appConfig.getBaseUrl();
...
```

## Setup library
You can initialize RemoteConfig in your Application class including a single line of code. This has to be done once in your application lifecycle
```java
RemoteConfig.initializeWithDefaults(this);
```
This will initialize the library with default values. See below for specific details

### Initialize each remote resource
Initialize each remote resource before using it specifying remote and local repository. Remote repository is used to fetch updated configuration, local is used to store it inside your application.
You can create your own local and remote repository or use the default ones:
```java
RemoteConfig.of(MessagesConfig.class).initialize(
        new RemoteConfigSettings.Builder<MessagesConfig>()
                .setInternalRepository(SharedPreferencesLocalRepository.create(this, MessagesConfig.class))
                .setRemoteRepository(HttpGETRemoteRepository.create(MessagesConfig.class, "REMOTE_JSON_URL"))
                .setCacheStrategy(CacheStrategy.NO_CACHE)
                .build()
);
```
### Set default values for each configuration
You should set default values for each configuration, so that your app doesn't have to wait to fetch values at least once.
```java
AppConfig appConfig = new AppConfig();
appConfig.setBaseUrl("http://your.api.server/");
appConfig.setDefaultTimeout(10);
RemoteConfig.of(AppConfig.class).setDefaultConfig(appConfig);
```
### Fetch config from network
1. To fetch an updated configuration from a remote json, call the `fetch()` method
2. To set fetched configuration available to your app, call the `activateFetched()` method

```java
final RemoteResource<MessagesConfig> remoteMessagesConfig = RemoteConfig.of(MessagesConfig.class);
remoteMessagesConfig.fetch().addSuccessListener(new RemoteResource.FetchSuccess() {
                    @Override
                    public void onSuccess() {
                        remoteMessagesConfig.activateFetched();
                    }
                });
```

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :)

## Credits and libraries
RemoteConfig is an open source library inspired by [Firebase Remote Config](https://firebase.google.com/docs/remote-config)

* [RxJava](https://github.com/ReactiveX/RxJava) and [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Gson](https://github.com/google/gson)
* [OkHttp](http://square.github.io/okhttp)

## License
    Copyright 2018 Giuseppe Giacoppo
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
