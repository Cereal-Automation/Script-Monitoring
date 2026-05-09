# Required for LogFactory discovery
-keep class org.apache.commons.logging.LogFactory { *; }
-keep class org.apache.commons.logging.impl.LogFactoryImpl { *; }
-keep class org.apache.commons.logging.impl.Jdk14Logger { *; }

# If you are using JCL on Android, keep this specifically
-keep class android.org.apache.commons.logging.impl.LogFactoryImpl { *; }