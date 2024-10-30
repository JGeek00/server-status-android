package com.jgeek00.ServerStatus.services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jgeek00.ServerStatus.constants.AppConfig
import com.jgeek00.ServerStatus.models.ServerModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val DB_VERSION = 1

class DatabaseService @Inject constructor(context: Context): SQLiteOpenHelper(context, AppConfig.DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE servers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "method TEXT NOT NULL,"
                + "ipDomain TEXT NOT NULL,"
                + "port INTEGER,"
                + "path TEXT,"
                + "useBasicAuth INTEGER,"
                + "basicAuthUser TEXT,"
                + "basicAuthPassword TEXT)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    suspend fun getServers(): List<ServerModel>? {
        val db = this.readableDatabase
        return withContext(Dispatchers.IO) {
            try {
                val items = mutableListOf<ServerModel>()
                val cursor = db.rawQuery("SELECT * FROM servers", null)
                if (cursor.moveToFirst()) {
                    do {
                        items.add(
                            ServerModel(
                                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                method = cursor.getString(cursor.getColumnIndexOrThrow("method")),
                                ipDomain = cursor.getString(cursor.getColumnIndexOrThrow("ipDomain")),
                                port = if (cursor.getInt(cursor.getColumnIndexOrThrow("port")) == 0) null else cursor.getInt(cursor.getColumnIndexOrThrow("port")),
                                path = cursor.getString(cursor.getColumnIndexOrThrow("path")),
                                useBasicAuth = cursor.getInt(cursor.getColumnIndexOrThrow("useBasicAuth")) == 1,
                                basicAuthUser = cursor.getString(cursor.getColumnIndexOrThrow("basicAuthUser")),
                                basicAuthPassword = cursor.getString(cursor.getColumnIndexOrThrow("basicAuthPassword"))
                            )
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
                db.close()
                items
            } catch (e: Exception) {
                println(e.localizedMessage)
                null
            }
        }
    }

    suspend fun createServer(name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Long? {
        val db = this.writableDatabase
        return withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues().apply {
                    put("name", name)
                    put("method", method)
                    put("ipDomain", ipDomain)
                    if (port != null) put("port", port)
                    if (path != null) put("path", path)
                    put("useBasicAuth", if (useBasicAuth) 1 else 0)
                    if (basicAuthUser != null) put("basicAuthUser", basicAuthUser)
                    if (basicAuthPassword != null) put("basicAuthPassword", basicAuthPassword)
                }
                val query = db.insert("servers", null, contentValues)
                db.close()
                query
            } catch (e: Exception) {
                println(e.localizedMessage)
                null
            }
        }
    }

    suspend fun updateServer(id: Int, name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Int? {
        val db = this.writableDatabase
        return withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues().apply {
                    put("name", name)
                    put("method", method)
                    put("ipDomain", ipDomain)
                    if (port != null) put("port", port)
                    if (path != null) put("path", path)
                    put("useBasicAuth", if (useBasicAuth) 1 else 0)
                    if (basicAuthUser != null) put("basicAuthUser", basicAuthUser)
                    if (basicAuthPassword != null) put("basicAuthPassword", basicAuthPassword)
                }
                val query = db.update("servers", contentValues, "id = ?", arrayOf(id.toString()))
                db.close()
                query
            } catch (e: Exception) {
                println(e.localizedMessage)
                null
            }
        }
    }

    suspend fun deleteServer(serverId: Int): Boolean {
        val db = this.writableDatabase
        return withContext(Dispatchers.IO) {
            try {
                db.delete("servers", "id = ?", arrayOf(serverId.toString()))
                db.close()
                true
            } catch (e: Exception) {
                println(e.localizedMessage)
                false
            }
        }
    }
}