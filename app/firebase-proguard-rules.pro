# https://github.com/firebase/quickstart-android

# Analytics

-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Authentication

-keepattributes Signature
-keepattributes *Annotation*
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses

# Crashlytics

#-keepattributes EnclosingMethod
#-keepattributes InnerClasses

-dontwarn org.xmlpull.v1.**
-dontnote org.xmlpull.v1.**
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }
