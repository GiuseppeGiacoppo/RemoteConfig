package me.giacoppo.remoteconfig

class TestApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = TestApplication()
            app.demo()
        }
    }

    private val remoteAppConfig by lazy { remoteConfig<AppConfig>() }

    init {
        initRemoteConfig {
            remoteResource<AppConfig>(
                storage("./configs"),
                network("https://demo7865768.mockable.io/messages.json")
            ) {
                resourceName = "welcome-config"
            }
        }
    }

    private fun demo() {
        clear()
        printCurrentConfig()
        showDefault()
        showFresh()
    }

    private fun clear() {
        remoteAppConfig.clear()
    }

    private fun showDefault() {
        remoteAppConfig.setDefaultConfig(AppConfig("This is the default welcome message."))
        printCurrentConfig()
    }

    private fun showFresh() {
        remoteAppConfig.fetch({
            println("Fetch is successful")
            remoteAppConfig.activateFetched()
            printCurrentConfig()
        }, {
            println("Fetch is failed: ${it.message}")
        })
    }

    private fun printCurrentConfig() {
        println("Config: ${remoteAppConfig.get()}")
    }

    data class AppConfig(
        val welcomeMessage: String?
    )
}
