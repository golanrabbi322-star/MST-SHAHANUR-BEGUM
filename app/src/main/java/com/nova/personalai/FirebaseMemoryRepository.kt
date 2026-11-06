package com.nova.personalai

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class MemoryType(val titleBn: String) {
    CHAT("চ্যাট মেমোরি"),
    PREFERENCE("পছন্দ ও সেটিংস"),
    WORKFLOW("ওয়ার্কফ্লো স্মৃতি"),
    PROJECT("প্রজেক্ট ও কনটেন্ট")
}

data class NovaMemory(
    val id: String = "",
    val type: String = "CHAT",
    val title: String = "",
    val content: String = "",
    val updatedAt: Long = 0L
)

class FirebaseMemoryRepository(
    private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nova_local_memories", Context.MODE_PRIVATE)

    private val auth: FirebaseAuth? by lazy {
        try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
    }

    private val db: FirebaseFirestore? by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (_: Exception) {
            try {
                FirebaseFirestore.getInstance()
            } catch (_: Exception) {
                null
            }
        }
    }

    val currentUserId: String? get() = auth?.currentUser?.uid
    val isCloudConnected: Boolean get() = auth?.currentUser != null && db != null

    suspend fun save(
        type: MemoryType,
        title: String,
        content: String
    ): String = withContext(Dispatchers.IO) {
        require(title.isNotBlank()) { "শিরোনাম লিখুন।" }
        require(content.isNotBlank()) { "স্মৃতির তথ্য লিখুন।" }
        require(title.length <= 200) { "শিরোনাম ২০০ অক্ষরের মধ্যে রাখুন।" }
        require(content.length <= 20000) { "তথ্য ২০,০০০ অক্ষরের মধ্যে রাখুন।" }

        val newId = "mem_${System.currentTimeMillis()}_${(1000..9999).random()}"
        val memory = NovaMemory(
            id = newId,
            type = type.name,
            title = title.trim(),
            content = content.trim(),
            updatedAt = System.currentTimeMillis()
        )

        // Always save to local cache for instant zero-latency recall
        saveLocal(memory)

        // If authenticated with Firestore, sync to cloud
        val uid = auth?.currentUser?.uid
        val firestore = db
        if (uid != null && firestore != null) {
            try {
                val ref = firestore.collection("users")
                    .document(uid)
                    .collection("memories")
                    .document(newId)

                ref.set(
                    mapOf(
                        "id" to newId,
                        "type" to type.name,
                        "title" to title.trim(),
                        "content" to content.trim(),
                        "updatedAt" to memory.updatedAt
                    )
                ).await()
            } catch (_: Exception) {
                // Kept safely in local storage
            }
        }

        newId
    }

    suspend fun list(
        type: MemoryType? = null
    ): List<NovaMemory> = withContext(Dispatchers.IO) {
        val uid = auth?.currentUser?.uid
        val firestore = db

        if (uid != null && firestore != null) {
            try {
                var query: Query = firestore.collection("users").document(uid).collection("memories")
                if (type != null) {
                    query = query.whereEqualTo("type", type.name)
                }

                val snapshot = query
                    .orderBy("updatedAt", Query.Direction.DESCENDING)
                    .limit(100)
                    .get()
                    .await()

                val cloudList = snapshot.documents.mapNotNull { doc ->
                    val typeName = doc.getString("type") ?: return@mapNotNull null
                    NovaMemory(
                        id = doc.id,
                        type = typeName,
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        updatedAt = doc.getLong("updatedAt") ?: 0L
                    )
                }

                if (cloudList.isNotEmpty()) {
                    // Update local cache
                    saveAllLocal(cloudList)
                    return@withContext cloudList
                }
            } catch (_: Exception) {
                // Fallback to local
            }
        }

        // Return local list
        val local = getLocalMemories()
        if (type != null) {
            local.filter { it.type == type.name }
        } else {
            local
        }
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        require(id.isNotBlank())
        deleteLocal(id)

        val uid = auth?.currentUser?.uid
        val firestore = db
        if (uid != null && firestore != null) {
            try {
                firestore.collection("users")
                    .document(uid)
                    .collection("memories")
                    .document(id)
                    .delete()
                    .await()
            } catch (_: Exception) {
                // Local deleted
            }
        }
    }

    private fun getLocalMemories(): List<NovaMemory> {
        val raw = prefs.getString("memories_json", "[]") ?: "[]"
        val list = mutableListOf<NovaMemory>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NovaMemory(
                        id = obj.optString("id"),
                        type = obj.optString("type", "CHAT"),
                        title = obj.optString("title"),
                        content = obj.optString("content"),
                        updatedAt = obj.optLong("updatedAt")
                    )
                )
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.updatedAt }
    }

    private fun saveLocal(memory: NovaMemory) {
        val current = getLocalMemories().toMutableList()
        current.removeAll { it.id == memory.id }
        current.add(0, memory)
        saveAllLocal(current)
    }

    private fun deleteLocal(id: String) {
        val current = getLocalMemories().toMutableList()
        current.removeAll { it.id == id }
        saveAllLocal(current)
    }

    private fun saveAllLocal(memories: List<NovaMemory>) {
        val array = JSONArray()
        memories.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("type", m.type)
                put("title", m.title)
                put("content", m.content)
                put("updatedAt", m.updatedAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("memories_json", array.toString()).apply()
    }
}
