# Keep Hilt annotations and generated code
-keep class * implements dagger.hilt.internal.GeneratedEntryPoint { <init>(); }
-keep class * implements dagger.hilt.internal.GeneratedComponent { <init>(); }
-keep class dagger.hilt.internal.processedrootsentinel.*.*
-keep class * extends androidx.hilt.work.HiltWorker { <init>(...); }

# Keep Firebase model classes (if using @Keep or @PropertyName)
# -keep class com.unswipe.android.data.model.** { *; }

# Keep Room entities, DAOs, and type converters
-keep class androidx.room.TypeConverter { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase_Impl
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep Kotlin Coroutines metadata
-keep class kotlin.coroutines.jvm.internal.* { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep Google Play Billing classes
-keep class com.android.vending.billing.** { *; }

# Keep DataStore Protos (if used)
# -keep class com.unswipe.android.datastore.UserSettings { *; } # Replace with your proto generated class

# Keep Serialization classes (if using Kotlinx Serialization)
# -keep class kotlinx.serialization.** { *; }
# -keep class com.unswipe.android.** { # Adjust package name
#     @kotlinx.serialization.Serializable <methods>;
# }
# -keepclassmembers class com.unswipe.android.** { # Adjust package name
#     @kotlinx.serialization.Serializable *;
# } 