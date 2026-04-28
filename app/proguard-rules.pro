# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\13929\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Kotlin
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.**
-keepclassmembers class kotlin.Metadata { *; }

# Coroutines
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory[] { *; }
-keepclassmembernames class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }
-keepclassmembernames class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }
