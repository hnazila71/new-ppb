package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

// This extension property creates the DataStore instance for your app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tasks")

object TaskStorage {

    // A key to store the list of tasks in DataStore
    private val TASKS_KEY = stringPreferencesKey("tasks_list")
    private val gson = Gson()

    /**
     * Saves the list of tasks to DataStore by converting it to a JSON string.
     */
    suspend fun saveTasks(context: Context, tasks: List<Task>) {
        val jsonString = gson.toJson(tasks)
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = jsonString
        }
    }

    /**
     * Loads the list of tasks from DataStore.
     * It returns an empty list if no tasks are saved yet.
     */
    suspend fun loadTasks(context: Context): List<Task> {
        val preferences = context.dataStore.data.first()
        val jsonString = preferences[TASKS_KEY]
        return if (jsonString != null) {
            // If data exists, convert it from JSON back to a list of Tasks
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            // Otherwise, return an empty list
            emptyList()
        }
    }
}