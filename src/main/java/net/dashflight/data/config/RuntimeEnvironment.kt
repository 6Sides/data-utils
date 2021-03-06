package net.dashflight.data.config

enum class RuntimeEnvironment(val description: String) {

    DEVELOPMENT("development"),
    DOCKER("docker"),
    STAGING("staging"),
    PRODUCTION("production");

    companion object {

        var _currentEnvironment: RuntimeEnvironment? = null

        fun fromString(name: String): RuntimeEnvironment {
            for (entry in values()) {
                if (entry.description.equals(name, ignoreCase = true)) {
                    return entry
                }
            }
            throw IllegalArgumentException(String.format("Invalid environment specified (%s). Must be one of [development, staging, production]", name))
        }

        val currentEnvironment: RuntimeEnvironment
            get() = try {
                _currentEnvironment ?: fromString(System.getenv("environment"))
            } catch (ex: IllegalArgumentException) {
                throw IllegalStateException("The application is not running with a valid environment specified."
                        + "Try running it again with an environment variable specifying the current environment. E.g. environment=staging")
            }
    }

}