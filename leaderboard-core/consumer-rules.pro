# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson - Keep all model classes
-keep class dev.leaderboard.sdk.core.models.** { *; }
-keepclassmembers class dev.leaderboard.sdk.core.models.** { *; }
