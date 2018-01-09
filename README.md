# RemoteConfig

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.giacoppo/remoteconfig/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.giacoppo/remoteconfig)

RemoteConfig is an Android library that lets you manage all your remote configuration without requiring developers to manually download  each configuration and integrate them into the application.

![Library Architecture](https://github.com/GiuseppeGiacoppo/RemoteConfig/raw/master/readme/architecture.png)

You can upload to your server many configurations (messages, flags, values and so on) in json files and the library will do all the work as fetching, storing them and making them available all over your app.


## Download
Grab via Maven:
```xml
<dependency>
  <groupId>me.giacoppo</groupId>
  <artifactId>remoteconfig</artifactId>
  <version>LATEST_VERSION</version>
  <type>pom</type>
</dependency>
```

or Gradle:
```groovy
api 'me.giacoppo:remoteconfig:LATEST_VERSION'
```

## Usage
Retrieve a specific instance of RemoteResource for every configuration class
```java
RemoteResource<AppConfig> remoteAppConfig = RemoteConfig.of(AppConfig.class);
```
You're done. `remoteAppConfig` will have the most updated values of `AppConfig`

You will get an instance of `AppConfig` with a get() method:
```java
AppConfig appConfig = remoteAppConfig.get();
```


### Setup
You can initialize RemoteConfig in your Application class including a single line of code. This has to be done once in your application lifecycle
```java
RemoteConfig.initializeWithDefaults(this);
```
This will initialize the library with default values. See below for specific details

### Set default values for each configuration
You should set default values for each configuration, so that your app doesn't have to wait to fetch values at least once.
```java
AppConfig appConfig = new AppConfig();
appConfig.setBaseUrl("http://your.api.server/");
appConfig.setDefaultTimeout(10);
RemoteConfig.of(AppConfig.class).setDefaultConfig(appConfig);
```
### Fetch config from network
1. To fetch an updated configuration from a remote json, call the `fetch(String url, Callback callback)` method
2. To set fetched configuration available to your app, call the `activateFetched()` method

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## Credits and libraries
RemoteConfig is an open source library inspired by [Firebase Remote Config](https://firebase.google.com/docs/remote-config)

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
