# 保持 SDK 的公共 API
-keep class com.wslight.** { *; }
-keep interface com.wslight.** { *; }

# 保持 JNI 方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持序列化类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持注解
-keepattributes *Annotation*

# 保持行号信息（便于调试）
-keepattributes SourceFile,LineNumberTable

# OkHttp 规则
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.*

# Bouncy Castle 规则
-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.** { *; }

# JmDNS 规则
-dontwarn javax.jmdns.**
-keep class javax.jmdns.** { *; }

# JCodec 规则
-dontwarn org.jcodec.**
-keep class org.jcodec.** { *; }

# 通用规则
-dontwarn sun.misc.**
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 移除日志（可选）
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}