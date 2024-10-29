package com.jgeek00.ServerStatus.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jgeek00.ServerStatus.constants.AppConfig
import com.jgeek00.ServerStatus.models.ServerModel

private const val DB_VERSION = 1

class DatabaseService(context: Context?): SQLiteOpenHelper(context, AppConfig.DATABASE_NAME, null, DB_VERSION) {
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

    fun getServers(): List<ServerModel>? {
        try {
            val db = this.readableDatabase
            val items = mutableListOf<ServerModel>()
            val cursor = db.rawQuery("SELECT * FROM servers", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val method = cursor.getString(cursor.getColumnIndexOrThrow("method"))
                    val ipDomain = cursor.getString(cursor.getColumnIndexOrThrow("ipDomain"))
                    val port = cursor.getInt(cursor.getColumnIndexOrThrow("port"))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow("path"))
                    val useBasicAuth = cursor.getInt(cursor.getColumnIndexOrThrow("useBasicAuth"))
                    val basicAuthUser = cursor.getString(cursor.getColumnIndexOrThrow("basicAuthUser"))
                    val basicAuthPassword = cursor.getString(cursor.getColumnIndexOrThrow("basicAuthPassword"))
                    items.add(
                        ServerModel(
                            id,
                            name,
                            method,
                            ipDomain,
                            port,
                            path,
                            useBasicAuth = useBasicAuth == 1,
                            basicAuthUser,
                            basicAuthPassword
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return items
        } catch (e: Exception) {
            println(e.localizedMessage)
            return null
        }
    }
}