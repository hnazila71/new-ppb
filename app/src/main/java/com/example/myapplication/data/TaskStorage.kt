// app/src/main/java/com/example/myapplication/data/TaskStorage.kt

package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.model.Task
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Kelas adapter untuk memberi tahu Gson cara menangani tipe LocalDateTime
class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        return LocalDateTime.parse(json?.asString, formatter)
    }
}

class TaskStorage {
    // Pindahkan semua fungsi ke dalam companion object
    companion object {
        // Konfigurasi Gson untuk menggunakan adapter LocalDateTime yang kita buat
        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        private fun getFile(context: Context) = File(context.filesDir, "tasks.json")

        suspend fun saveTasks(context: Context, tasks: List<Task>) {
            withContext(Dispatchers.IO) {
                val json = gson.toJson(tasks)
                getFile(context).writeText(json)
            }
        }

        suspend fun loadTasks(context: Context): List<Task> {
            return withContext(Dispatchers.IO) {
                val file = getFile(context)
                if (file.exists()) {
                    try {
                        val json = file.readText()
                        val type = object : TypeToken<List<Task>>() {}.type
                        gson.fromJson<List<Task>>(json, type) ?: emptyList()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList() // Jika error, kembalikan list kosong
                    }
                } else {
                    emptyList()
                }
            }
        }
    }
}