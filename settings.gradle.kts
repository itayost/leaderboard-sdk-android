pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "leaderboard-sdk-android"

// SDK Modules
include(":leaderboard-core")
include(":leaderboard-game")
include(":leaderboard-hub")

// Sample Apps
include(":sample-game")
include(":sample-hub")
include(":sample-dev-hub")
