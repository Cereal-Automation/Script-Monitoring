# kdriver CDP domains and events must not be obfuscated
-keep class dev.kdriver.cdp.** { *; }
-keep class dev.kdriver.core.** { *; }

# Keep kotlinx.serialization annotations for kdriver
-keepclassmembers class dev.kdriver.** {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.SerialName *;
}
