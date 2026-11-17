package com.dormly.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DormlyDatabase(context: Context) :
    SQLiteOpenHelper(context, "dormly.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE requests (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                detail TEXT NOT NULL,
                resident TEXT NOT NULL,
                status TEXT NOT NULL,
                assigned_to TEXT NOT NULL,
                note TEXT NOT NULL
            )"""
        )
        db.execSQL(
            """CREATE TABLE announcements (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )"""
        )

        insertTicket(db, DormlyTicket(101, "Leaking bathroom faucet", "Plumbing",
            "Water continues to drip from the bathroom faucet.", "Alex Rivera · Room 204",
            "In Progress", "Marco Santos", "Technician scheduled to inspect the faucet."))
        insertTicket(db, DormlyTicket(102, "Hallway light not working", "Electrical",
            "The light outside the room is not turning on.", "Jamie Cruz · Room 108",
            "Pending"))
        insertTicket(db, DormlyTicket(103, "Wi-Fi connection issue", "Internet",
            "The room connection keeps disconnecting.", "Taylor Reyes · Room 305",
            "Completed", "Marco Santos", "Router reset and connection tested."))

        val welcome = ContentValues().apply {
            put("title", "Welcome to Dormly")
            put("message", "Use Dormly to report maintenance concerns and keep track of updates for your home.")
        }
        db.insert("announcements", null, welcome)
        val reminder = ContentValues().apply {
            put("title", "Maintenance reminder")
            put("message", "Please keep shared hallways clear and report facility concerns through a maintenance request.")
        }
        db.insert("announcements", null, reminder)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version 1 is the initial classroom prototype schema.
    }

    private fun ticketValues(ticket: DormlyTicket) = ContentValues().apply {
        put("id", ticket.id)
        put("title", ticket.title)
        put("category", ticket.category)
        put("detail", ticket.detail)
        put("resident", ticket.resident)
        put("status", ticket.status)
        put("assigned_to", ticket.assignedTo)
        put("note", ticket.note)
    }

    private fun insertTicket(db: SQLiteDatabase, ticket: DormlyTicket) {
        db.insertWithOnConflict("requests", null, ticketValues(ticket), SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getTickets(): List<DormlyTicket> {
        val result = mutableListOf<DormlyTicket>()
        readableDatabase.query("requests", null, null, null, null, null, "id DESC").use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    DormlyTicket(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        detail = cursor.getString(cursor.getColumnIndexOrThrow("detail")),
                        resident = cursor.getString(cursor.getColumnIndexOrThrow("resident")),
                        status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        assignedTo = cursor.getString(cursor.getColumnIndexOrThrow("assigned_to")),
                        note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
                    )
                )
            }
        }
        return result
    }

    fun saveTickets(tickets: List<DormlyTicket>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("requests", null, null)
            tickets.forEach { insertTicket(db, it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun addAnnouncement(title: String, message: String) {
        val values = ContentValues().apply {
            put("title", title)
            put("message", message)
        }
        writableDatabase.insert("announcements", null, values)
    }

    fun getAnnouncements(): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        readableDatabase.query("announcements", arrayOf("title", "message"),
            null, null, null, null, "id DESC").use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    cursor.getString(0) to cursor.getString(1)
                )
            }
        }
        return result
    }
}
