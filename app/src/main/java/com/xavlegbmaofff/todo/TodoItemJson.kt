package com.xavlegbmaofff.todo

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.time.Instant

val TodoItem.json: JSONObject
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val obj = JSONObject()
        obj.put("uid", uid)
        obj.put("text", text)
        if (importance != Importance.NORMAL) {
            obj.put("importance", importance.value)
        }
        if (color != Color.WHITE) {
            obj.put("color", color)
        }
        deadline?.let { obj.put("deadline", it.toEpochMilli()) }
        obj.put("isDone", isDone)
        return obj
    }

@RequiresApi(Build.VERSION_CODES.O)
fun JSONObject.parseTodoItem(): TodoItem? = try {
    val uid = getString("uid")
    val text = getString("text")
    val importance = if (has("importance")) {
        Importance.fromValue(getString("importance"))
    } else Importance.NORMAL
    val color = if (has("color")) getInt("color") else Color.WHITE
    val deadline = if (has("deadline")) {
        Instant.ofEpochMilli(getLong("deadline"))
    } else null
    val isDone = optBoolean("isDone", false)
    TodoItem(uid, text, importance, color, deadline, isDone)
} catch (e: Exception) {
    null
}
