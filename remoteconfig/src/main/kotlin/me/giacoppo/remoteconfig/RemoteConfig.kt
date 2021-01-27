package me.giacoppo.remoteconfig

import me.giacoppo.remoteconfig.core.RemoteConfigContext

/**
 * RemoteConfig is a Kotlin library that lets you manage all your remote configuration
 * without requiring developers to manually download each configuration and integrate
 * them into the Kotlin application.
 */
object RemoteConfig : RemoteConfigContext {
    var logger: ((String) -> Unit)? = null
    @PublishedApi internal val resourcesNames: MutableMap<Class<*>, String> = mutableMapOf()
    @PublishedApi internal val resources: MutableMap<String, RemoteResource<*>> = mutableMapOf()
    private val checkInitialization: RemoteConfig.() -> Unit = { }

    /**
     * Register a new remote resource.
     * @param resourceClass class that matches the remote resource
     * @param init initialization block of RemoteResource. It needs at least remote and local repository
     */
    fun <T: Any> initRemoteResource(resourceClass: Class<T>, init: RemoteResource<T>.() -> Unit) {
        val remoteResource = RemoteResource(resourceClass.kotlin, logger).initialize(init)
        resourcesNames[resourceClass] = remoteResource.resourceName
        resources[remoteResource.resourceName] = remoteResource
    }

    /**
     * Get the instance of Remote Resource
     *
     * @param clazz class of config
     *
     * @return the instance of a RemoteResource that wraps a specific config class
     */
    fun <T: Any> of(clazz: Class<T>): RemoteResource<T> {
        val nameOfConfig = resourcesNames[clazz]!!
        return of<T>(nameOfConfig)
    }

    /**
     * Get the instance of Remote Resource
     *
     * @param nameOfConfig name of config
     *
     * @return the instance of a RemoteResource that wraps a specific config class
     */
    @Suppress("Unchecked_Cast")
    fun <T: Any> of(nameOfConfig: String): RemoteResource<T> {
        val remoteResource = resources[nameOfConfig]
        if (remoteResource != null) {
            logger?.invoke("$nameOfConfig object already in memory")
            return remoteResource as RemoteResource<T>
        } else {
            throw IllegalArgumentException("$nameOfConfig not initialized")
        }
    }

    fun initialize(init: RemoteConfig.() -> Unit): RemoteConfig {
        return this
                .apply(init)
                .apply(checkInitialization)
    }
}
