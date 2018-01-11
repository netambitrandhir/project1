# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/preetdhillon/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn com.sendbird.android.shadow.okio.**
-dontwarn com.squareup.picasso.**
-dontwarn com.viewpagerindicator.**
# Class names are needed in reflection
-keepnames class com.amazonaws.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**
-dontwarn butterknife.internal.**
-dontwarn com.davemorrissey.labs.subscaleview.**
 -ignorewarnings


