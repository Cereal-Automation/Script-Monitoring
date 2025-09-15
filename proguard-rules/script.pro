-verbose

## Include java runtime classes
-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.desktop.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.logging.jmod(!**.jar;!module-info.class)

# Don't print notes about reflection in GSON code, the Kotlin runtime, and
# our own optionally injected code.
-dontnote kotlin.**
-dontnote kotlinx.**
-dontnote com.google.gson.**
-dontnote proguard.configuration.ConfigurationLogger

# Preserve injected GSON utility classes and their members.
-keep,allowobfuscation class proguard.optimize.gson._*
-keepclassmembers class proguard.optimize.gson._* {
    *;
}

# Obfuscate class strings of injected GSON utility classes.
-adaptclassstrings proguard.optimize.gson.**

# Put all obfuscated classes into the nameless root package.
-repackageclasses ''

# Allow classes and class members to be made public.
-allowaccessmodification

-renamesourcefileattribute SourceFile

# Preserve all annotations.
-keepattributes *Annotation*

# Preserve the special static methods that are required in all enumeration
# classes.
-keepclassmembers enum * {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep entry point of script so it's not removed when minimizing
-keep,allowoptimization public class * extends com.cereal.sdk.Script {
    public <methods>;
}
-keep class * implements com.cereal.sdk.ScriptConfiguration

# Keep the SDK signature intact
-keep class com.cereal.sdk.** { *; }

# Keep script signature
-keepattributes Signature

-ignorewarnings

-keep class kotlin.Metadata

# Keep the configuration annotations
-keepclassmembers class * {
    @com.cereal.sdk.ScriptConfigurationItem *;
    @com.cereal.sdk.TaskConfigurationItem *;
}

# Keep all classes that implement com.cereal.sdk.statemodifier.StateModifier
-keep class * implements com.cereal.sdk.statemodifier.StateModifier {
    <init>(...);
    *;
}

# Keep the enum class so that annotations are preserved.
-keep enum * {}
