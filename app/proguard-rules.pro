# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

#-dontobfuscate

-repackageclasses ''
-allowaccessmodification
-optimizationpasses 9
-ignorewarnings

-renamesourcefileattribute ''
-shrinkunusedprotofields
-overloadaggressively
