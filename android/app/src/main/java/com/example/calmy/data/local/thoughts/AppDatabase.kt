package com.example.calmy.data.local.thoughts

import android.content.Context
import com.google.gson.Gson

class AppDatabase private constructor(
    private val thoughtDao: ThoughtDao
) {
    fun thoughtDao(): ThoughtDao {
        return thoughtDao
    }

    companion object {
        fun create(context: Context, gson: Gson): AppDatabase {
            return AppDatabase(
                thoughtDao = ThoughtDao(
                    context = context.applicationContext,
                    gson = gson
                )
            )
        }
    }
}
