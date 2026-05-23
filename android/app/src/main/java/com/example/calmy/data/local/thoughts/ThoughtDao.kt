package com.example.calmy.data.local.thoughts

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.thoughtsDataStore by preferencesDataStore(name = "calmy_thoughts")

class ThoughtDao(
    context: Context,
    private val gson: Gson
) {
    private val dataStore = context.applicationContext.thoughtsDataStore
    private val listType = object : TypeToken<List<ThoughtEntity>>() {}.type

    suspend fun getThoughts(userId: String): List<ThoughtEntity> {
        val json = dataStore.data.first()[key(userId)].orEmpty()
        if (json.isBlank()) {
            return emptyList()
        }
        val deletedIds = getDeletedThoughtIds(userId)
        return runCatching {
            gson.fromJson<List<ThoughtEntity>>(json, listType).orEmpty()
                .filterNot { thought -> thought.id in deletedIds }
                .sortedWith(compareBy<ThoughtEntity> { thought -> thought.createdAt }.thenBy { thought -> thought.id })
        }.getOrDefault(emptyList())
    }

    suspend fun replaceThoughts(userId: String, thoughts: List<ThoughtEntity>) {
        val deletedIds = getDeletedThoughtIds(userId)
        val pendingThoughts = getThoughts(userId).filter { thought -> thought.isPendingSync }
        val sortedThoughts = thoughts
            .filter { thought -> thought.userId == userId }
            .plus(pendingThoughts)
            .filterNot { thought -> thought.id in deletedIds }
            .distinctBy { thought -> thought.id }
            .sortedWith(compareBy<ThoughtEntity> { thought -> thought.createdAt }.thenBy { thought -> thought.id })
        dataStore.edit { preferences ->
            preferences[key(userId)] = gson.toJson(sortedThoughts, listType)
        }
    }

    suspend fun addPendingThought(thought: ThoughtEntity) {
        addThought(thought.copy(isPendingSync = true))
    }

    suspend fun addThought(thought: ThoughtEntity) {
        val thoughts = getThoughts(thought.userId)
            .plus(thought)
            .distinctBy { value -> value.id }
            .sortedWith(compareBy<ThoughtEntity> { value -> value.createdAt }.thenBy { value -> value.id })
        dataStore.edit { preferences ->
            preferences[key(thought.userId)] = gson.toJson(thoughts, listType)
        }
    }

    suspend fun getPendingThoughts(userId: String): List<ThoughtEntity> {
        return getThoughts(userId).filter { thought -> thought.isPendingSync }
    }

    suspend fun replaceWithSyncedThoughts(userId: String, thoughts: List<ThoughtEntity>) {
        val deletedIds = getDeletedThoughtIds(userId)
        val sortedThoughts = thoughts
            .filter { thought -> thought.userId == userId }
            .filterNot { thought -> thought.id in deletedIds }
            .map { thought -> thought.copy(isPendingSync = false) }
            .sortedWith(compareBy<ThoughtEntity> { thought -> thought.createdAt }.thenBy { thought -> thought.id })
        dataStore.edit { preferences ->
            preferences[key(userId)] = gson.toJson(sortedThoughts, listType)
        }
    }

    suspend fun deleteThought(userId: String, thoughtId: String) {
        val filteredThoughts = getThoughts(userId).filterNot { thought -> thought.id == thoughtId }
        val deletedIds = getDeletedThoughtIds(userId).plus(thoughtId)
        dataStore.edit { preferences ->
            preferences[key(userId)] = gson.toJson(filteredThoughts, listType)
            preferences[deletedKey(userId)] = deletedIds
        }
    }

    suspend fun getDeletedThoughtIds(userId: String): Set<String> {
        return dataStore.data.first()[deletedKey(userId)].orEmpty()
    }

    private fun key(userId: String) = stringPreferencesKey("thoughts_$userId")
    private fun deletedKey(userId: String) = stringSetPreferencesKey("deleted_thoughts_$userId")
}
