# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\rutvik\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**

-keep class com.google.** {*;}

-keep class retrofit.** { *; }
-keep class package.with.model.classes.** { *; }
-keepclassmembernames interface * {
    @retrofit.http.* <methods>;
}

-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

-useuniqueclassmembernames

-allowaccessmodification

-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod
-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
    void listener_*(...);
    public void * (android.view.View);
}

-dontwarn android.support.**
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.app.** { *; }
-keepattributes *Annotation*

-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}