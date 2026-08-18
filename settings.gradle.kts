pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MitsuDrive"
include(":app")

// Core modules
include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-storage")
include(":core:core-sync")
include(":core:core-auth")
include(":core:core-location")

// Feature modules
include(":features:auth:auth-api")
include(":features:auth:auth-impl")
include(":features:auth:auth-ui")

include(":features:feed:feed-api")
include(":features:feed:feed-impl")
include(":features:feed:feed-ui")

include(":features:chat:chat-api")
include(":features:chat:chat-impl")
include(":features:chat:chat-ui")

include(":features:map:map-api")
include(":features:map:map-impl")
include(":features:map:map-ui")

include(":features:garage:garage-api")
include(":features:garage:garage-impl")
include(":features:garage:garage-ui")

include(":features:sos:sos-api")
include(":features:sos:sos-impl")
include(":features:sos:sos-ui")
