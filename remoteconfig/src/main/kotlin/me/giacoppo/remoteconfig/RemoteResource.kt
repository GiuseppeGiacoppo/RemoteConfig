package me.giacoppo.remoteconfig

import me.giacoppo.remoteconfig.core.*
import me.giacoppo.remoteconfig.mappers.JsonResourceMapper
import java.util.*
import kotlin.reflect.KClass

/**
 * RemoteResource represents a remote resource of configuration.
 * It fetches the config from specified remote repository and saves it into local repository.
 * Resource name should be configured when obfuscation is enabled or you have multiple configurations based on same class.
 * It is possible to specify the type of config (Json, Text). Default is Json format
 *
 * @param <T> class of configuration
 */
class RemoteResource<T : Any> @PublishedApi internal constructor(
        private val resourceClass: KClass<T>,
        private val logger: ((String) -> Unit)? = null
): RemoteResourceContext {
    var resourceName: String = resourceClass.java.simpleName.toLowerCase(Locale.getDefault())
    lateinit var resourceLocalRepository: ResourceLocalRepository
    lateinit var resourceRemoteRepository: ResourceRemoteRepository
    var format: ResourceMapper = JsonResourceMapper

    internal fun initialize(init: RemoteResource<T>.() -> Unit): RemoteResource<T> {
        return this
                .apply(init)
                .apply(checkInitialization)
    }

    /**
     * Set a default configuration. It doesn't need to be activated and it will be returned if no
     * fresher configurations are fetched and activated
     *
     * @param resource config that will be stored as default value
     */
    fun setDefaultConfig(resource: T) {
        resourceLocalRepository.storeDefault(format.toRepository(resource))
    }

    /**
     * Fetch the remote configuration and store it as fetched config.
     *
     * @param success callback for success
     * @param error callback for errors
     */
    fun fetch(success: (() -> Unit)? = null, error: ((Throwable) -> Unit)? = null) {
        fetchAndSave(success, error)
    }

    /**
     * Fetch the remote configuration and store it as fetched config.
     * If a previous config was fetched before max age, it will invoke success without fetching it.
     *
     * @param maxAgeInMillis max age of last fetched resource. It will fetch new resource only if last fetched resource is not expired
     * @param success callback for success. Invoked also if last fetched resource is not expired
     * @param error callback for errors
     */
    fun fetch(maxAgeInMillis: Long, success: (() -> Unit)? = null, error: ((Throwable) -> Unit)? = null) {
        if (!shouldFetchResource(maxAgeInMillis)) {
            success?.invoke()
        } else {
            fetch(success, error)
        }
    }

    /**
     * Activate last fetched config if present. Otherwise does nothing
     */
    fun activateFetched() {
        resourceLocalRepository.activate()
    }

    /**
     * Return last activated config, if present. Otherwise will return the default config, or null
     *
     * @return last activated config or null
     */
    fun get(): T? {
        return resourceLocalRepository.getActive()?.let { format.fromRepository(it, resourceClass.java) }
    }

    /**
     * Clear default, fetched and activated resource
     */
    fun clear() {
        resourceLocalRepository.clear()
    }

    private fun fetchAndSave(success: (() -> Unit)? = null, error: ((Throwable) -> Unit)? = null) {
        resourceRemoteRepository.fetch({
            resourceLocalRepository.storeFetched(it)
            success?.invoke()
        }, {
            logger?.invoke("Error fetching remote config: ${it.message}")
            error?.invoke(it)
        })
    }

    private fun shouldFetchResource(maxAgeInMillis: Long): Boolean =
            !resourceLocalRepository.isFetchedFresh(maxAgeInMillis)

    private companion object {
        val checkInitialization: RemoteResource<*>.() -> Unit = {
            check(this::resourceLocalRepository.isInitialized) { RemoteConfigMessages.REMOTE_RESOURCE_LOCAL_REPOSITORY_NOT_INITIALIZED }
            check(this::resourceRemoteRepository.isInitialized) { RemoteConfigMessages.REMOTE_RESOURCE_REMOTE_REPOSITORY_NOT_INITIALIZED }
            check(this.resourceName.isNotBlank()) { RemoteConfigMessages.REMOTE_RESOURCE_RESOURCE_NAME_NOT_VALID }

            resourceLocalRepository.setResourceName(resourceName)
        }
    }
}
