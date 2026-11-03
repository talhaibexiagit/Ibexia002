# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve AdMob
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Strip Log calls in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# Strip printStackTrace in release bytecode
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}
