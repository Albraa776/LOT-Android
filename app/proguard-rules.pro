# Proguard / R8 rules for LOT (Locally Offline Translation)

# Keep Room database entities and DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep LOT core models
-keep class com.albraa.lot.core.model.** { *; }

# Keep ML Kit components
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Keep Compose runtime
-keep class androidx.compose.** { *; }
