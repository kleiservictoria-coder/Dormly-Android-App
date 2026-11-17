package com.dormly.app

import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

data class DormlyTicket(
    val id: Int,
    val title: String,
    val category: String,
    val detail: String,
    val resident: String,
    var status: String,
    var assignedTo: String = "Unassigned",
    var note: String = ""
)

class MainActivity : AppCompatActivity() {
    private val navy = Color.rgb(20, 43, 74)
    private val blue = Color.rgb(40, 100, 165)
    private val pale = Color.rgb(234, 242, 250)
    private val ink = Color.rgb(28, 41, 56)
    private val muted = Color.rgb(113, 128, 150)
    private val line = Color.rgb(229, 234, 240)
    private lateinit var content: LinearLayout
    private lateinit var bottom: LinearLayout
    private var role = ""
    private var email = ""
    private var currentTab = "Home"
    private var nextId = 104
    private lateinit var database: DormlyDatabase
    private val tickets = mutableListOf<DormlyTicket>()

    private val residents = listOf("Alex Rivera · Room 204", "Jamie Cruz · Room 108", "Taylor Reyes · Room 305", "Morgan Lee · Room 210")
    private val units = listOf("Room 108 · Occupied", "Room 204 · Occupied", "Room 210 · Occupied", "Room 305 · Occupied", "Room 401 · Available")
    private val staff = listOf("Marco Santos", "Jordan Flores", "Casey Mendoza")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.content)
        bottom = findViewById(R.id.bottomNav)
        database = DormlyDatabase(this)
        tickets.addAll(database.getTickets())
        nextId = (tickets.maxOfOrNull { it.id } ?: 100) + 1
        showLogin()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun text(value: String, size: Float = 14f, color: Int = ink, bold: Boolean = false): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(dp(2), dp(2), dp(2), dp(2))
        }
    }

    private fun shape(color: Int, radius: Int = 18): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }

    private fun button(label: String, onClick: () -> Unit, filled: Boolean = true): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 13f
            setTextColor(if (filled) Color.WHITE else navy)
            background = shape(if (filled) blue else pale, 12)
            setPadding(dp(12), dp(5), dp(12), dp(5))
            setOnClickListener { onClick() }
        }
    }

    private fun addGap(parent: LinearLayout, height: Int = 10) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(14), dp(16), dp(14))
        background = shape(Color.WHITE, 18)
        elevation = dp(2).toFloat()
    }

    private fun addCard(parent: LinearLayout, child: View) {
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.bottomMargin = dp(12)
        parent.addView(child, lp)
    }

    private fun heading(title: String, subtitle: String? = null) {
        content.addView(text(title, 23f, navy, true))
        if (subtitle != null) {
            content.addView(text(subtitle, 13f, muted))
        }
        addGap(content, 14)
    }

    private fun showLogin() {
        role = ""
        currentTab = "Home"
        bottom.removeAllViews()
        content.removeAllViews()
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(8), dp(34), dp(8), dp(24))
        }
        wrap.addView(text("Welcome to Dormly", 26f, navy, true))
        wrap.addView(text("A simpler way to manage your home.", 14f, muted))
        addGap(wrap, 24)
        val panel = card()
        panel.addView(text("Sign in to your workspace", 18f, navy, true))
        addGap(panel, 14)
        panel.addView(text("Email address", 12f, muted, true))
        val emailInput = EditText(this).apply {
            hint = "name@dormly.com"
            textSize = 14f
            setSingleLine(true)
            inputType = 33
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = shape(pale, 10)
        }
        panel.addView(emailInput, LinearLayout.LayoutParams(-1, dp(50)))
        addGap(panel, 10)
        panel.addView(text("Password", 12f, muted, true))
        val passInput = EditText(this).apply {
            hint = "Enter password"
            textSize = 14f
            setSingleLine(true)
            inputType = 129
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = shape(pale, 10)
        }
        panel.addView(passInput, LinearLayout.LayoutParams(-1, dp(50)))
        addGap(panel, 16)
        panel.addView(button("Sign In", {
            val mail = emailInput.text.toString().trim().lowercase()
            val pass = passInput.text.toString()
            val selected = when (mail) {
                "admin@dormly.com" -> "Admin"
                "resident@dormly.com" -> "Resident"
                "maintenance@dormly.com" -> "Maintenance"
                else -> ""
            }
            if (selected.isNotEmpty() && pass == "dormly123") {
                role = selected
                email = mail
                currentTab = "Home"
                render()
            } else {
                Toast.makeText(this, "Check your demo email and password.", Toast.LENGTH_LONG).show()
            }
        }))
        addGap(panel, 16)
        panel.addView(text("DEMO ACCESS", 11f, blue, true))
        panel.addView(text("Admin  ·  admin@dormly.com", 12f, muted))
        panel.addView(text("Resident  ·  resident@dormly.com", 12f, muted))
        panel.addView(text("Maintenance  ·  maintenance@dormly.com", 12f, muted))
        panel.addView(text("Password for all: dormly123", 12f, navy, true))
        wrap.addView(panel, LinearLayout.LayoutParams(-1, -2))
        content.addView(wrap)
    }

    private fun render() {
        content.removeAllViews()
        bottom.removeAllViews()
        when (currentTab) {
            "Home" -> showDashboard()
            "Requests" -> showRequests()
            "People" -> showPeople()
            "Updates" -> showUpdates()
        }
        val tabs = when (role) {
            "Admin" -> listOf("Home", "Requests", "People", "Updates")
            "Resident" -> listOf("Home", "Requests", "Updates")
            else -> listOf("Home", "Requests", "Updates")
        }
        tabs.forEach { tab ->
            val b = TextView(this).apply {
                text = (if (currentTab == tab) "●  " else "") + tab
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(if (currentTab == tab) blue else muted)
                setPadding(dp(7), dp(12), dp(7), dp(12))
                setOnClickListener { currentTab = tab; render() }
            }
            bottom.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
        }
        val logout = TextView(this).apply {
            text = "Exit"
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(182, 66, 66))
            setPadding(dp(7), dp(12), dp(7), dp(12))
            setOnClickListener { showLogin() }
        }
        bottom.addView(logout, LinearLayout.LayoutParams(0, -2, 0.8f))
    }

    private fun showDashboard() {
        val open = tickets.count { it.status != "Completed" }
        heading("Good day!", role + " workspace  ·  " + email)
        val welcome = card()
        welcome.setBackgroundColor(navy)
        welcome.background = shape(navy, 18)
        welcome.addView(text("Your home, better managed.", 18f, Color.WHITE, true))
        welcome.addView(text("Keep track of people, spaces, and maintenance in one place.", 12f, Color.rgb(220, 232, 245)))
        addCard(content, welcome)
        when (role) {
            "Admin" -> {
                statsRow("Residents", "24", "Housing units", "18")
                statsRow("Open requests", open.toString(), "Completed", tickets.count { it.status == "Completed" }.toString())
                heading("Needs your attention", "Recent maintenance activity")
                tickets.take(3).forEach { ticketCard(it, true) }
                addCard(content, card().apply {
                    addView(text("Quick action", 16f, navy, true))
                    addGap(this, 6)
                    addView(button("Create announcement", { announcementDialog() }))
                })
            }
            "Resident" -> {
                statsRow("My room", "204", "Open requests", tickets.count { it.resident.startsWith("Alex Rivera") && it.status != "Completed" }.toString())
                heading("My home", "Room and maintenance overview")
                addCard(content, card().apply {
                    addView(text("Room 204", 18f, navy, true))
                    addView(text("Building A  ·  Active resident", 13f, muted))
                    addGap(this, 8)
                    addView(text("Your room information and service requests are available here.", 13f, ink))
                })
                heading("My recent requests")
                tickets.filter { it.resident.startsWith("Alex Rivera") }.forEach { ticketCard(it, false) }
                addCard(content, card().apply {
                    addView(text("Need something fixed?", 16f, navy, true))
                    addView(text("Send a request to your property administrator.", 13f, muted))
                    addGap(this, 8)
                    addView(button("Submit maintenance request", { requestDialog() }))
                })
            }
            else -> {
                statsRow("Assigned to me", tickets.count { it.assignedTo != "Unassigned" }.toString(), "In progress", tickets.count { it.assignedTo != "Unassigned" && it.status == "In Progress" }.toString())
                heading("My assigned work", "Maintenance team work queue")
                tickets.filter { it.assignedTo != "Unassigned" }.forEach { ticketCard(it, true) }
            }
        }
    }

    private fun statsRow(label1: String, value1: String, label2: String, value2: String) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(label1 to value1, label2 to value2).forEach { pair ->
            val box = card()
            box.addView(text(pair.second, 24f, navy, true))
            box.addView(text(pair.first, 12f, muted))
            row.addView(box, LinearLayout.LayoutParams(0, -2, 1f).apply {
                marginEnd = dp(6); marginStart = dp(6)
            })
        }
        content.addView(row)
        addGap(content, 14)
    }

    private fun ticketCard(t: DormlyTicket, adminActions: Boolean) {
        val box = card()
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        top.addView(text("#" + t.id + "  " + t.title, 15f, navy, true), LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(statusChip(t.status))
        box.addView(top)
        addGap(box, 5)
        box.addView(text(t.category + "  ·  " + t.resident, 12f, muted))
        box.addView(text(t.detail, 13f, ink))
        if (t.assignedTo != "Unassigned") box.addView(text("Assigned to " + t.assignedTo, 12f, blue, true))
        if (t.note.isNotBlank()) box.addView(text("Work note: " + t.note, 12f, muted))
        if (adminActions && role == "Admin" && t.status != "Completed") {
            addGap(box, 8)
            box.addView(button("Assign / update", { adminTicketDialog(t) }))
        } else if (role == "Maintenance" && t.assignedTo != "Unassigned" && t.status != "Completed") {
            addGap(box, 8)
            box.addView(button("Update task", { maintenanceDialog(t) }))
        }
        addCard(content, box)
    }

    private fun statusChip(status: String): TextView {
        val c = when (status) {
            "Completed" -> Color.rgb(225, 243, 234)
            "In Progress" -> Color.rgb(255, 242, 220)
            else -> pale
        }
        val tc = when (status) {
            "Completed" -> Color.rgb(35, 132, 91)
            "In Progress" -> Color.rgb(169, 107, 19)
            else -> blue
        }
        return text(status, 10f, tc, true).apply {
            setPadding(dp(8), dp(5), dp(8), dp(5))
            background = shape(c, 20)
        }
    }

    private fun showRequests() {
        heading(if (role == "Resident") "My requests" else if (role == "Maintenance") "Assigned requests" else "All requests",
            "Follow each maintenance request from submission to completion.")
        val visible = when (role) {
            "Resident" -> tickets.filter { it.resident.startsWith("Alex Rivera") }
            "Maintenance" -> tickets.filter { it.assignedTo == "Marco Santos" }
            else -> tickets
        }
        if (visible.isEmpty()) content.addView(text("No requests yet.", 14f, muted))
        visible.forEach { ticketCard(it, role == "Admin") }
        if (role == "Resident") {
            addCard(content, button("＋  New maintenance request", { requestDialog() }))
        }
    }

    private fun showPeople() {
        heading("People & spaces", "Directory overview for this property.")
        addCard(content, card().apply {
            addView(text("Residents", 17f, navy, true))
            addGap(this, 8)
            residents.forEach { addView(text("•  " + it, 13f, ink)); addGap(this, 4) }
        })
        addCard(content, card().apply {
            addView(text("Housing units", 17f, navy, true))
            addGap(this, 8)
            units.forEach { addView(text("⌂  " + it, 13f, ink)); addGap(this, 4) }
        })
        addCard(content, card().apply {
            addView(text("Maintenance team", 17f, navy, true))
            addGap(this, 8)
            staff.forEach { addView(text("•  " + it, 13f, ink)); addGap(this, 4) }
        })
    }

    private fun showUpdates() {
        heading("Community updates", "Announcements from your property team.")
        database.getAnnouncements().forEach { announcement ->
            addCard(content, card().apply {
                addView(text(announcement.first, 17f, navy, true))
                addView(text(announcement.second, 13f, ink))
                addGap(this, 6)
                addView(text("Property Administration  ·  Posted announcement", 11f, muted))
            })
        }
        if (database.getAnnouncements().isEmpty()) {
            content.addView(text("No announcements have been posted yet.", 13f, muted))
        }
        if (role == "Admin") addCard(content, button("＋  New announcement", { announcementDialog() }))
    }

    private fun requestDialog() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val title = EditText(this).apply { hint = "e.g. Leaking kitchen sink" }
        val categories = Spinner(this)
        categories.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Plumbing", "Electrical", "Internet", "Appliance", "Cleaning", "Other"))
        val detail = EditText(this).apply { hint = "Describe the issue and where it is located"; minLines = 3; gravity = Gravity.TOP }
        box.addView(text("Request title", 12f, muted, true)); box.addView(title)
        box.addView(text("Category", 12f, muted, true)); box.addView(categories)
        box.addView(text("Description", 12f, muted, true)); box.addView(detail)
        AlertDialog.Builder(this).setTitle("New maintenance request").setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Submit") { _, _ ->
                if (title.text.isNotBlank() && detail.text.isNotBlank()) {
                    tickets.add(0, DormlyTicket(nextId++, title.text.toString(), categories.selectedItem.toString(),
                        detail.text.toString(), "Alex Rivera · Room 204", "Pending"))
                    database.saveTickets(tickets)
                    Toast.makeText(this, "Request submitted successfully.", Toast.LENGTH_SHORT).show()
                    currentTab = "Requests"; render()
                } else Toast.makeText(this, "Please complete the title and description.", Toast.LENGTH_SHORT).show()
            }.show()
    }

    private fun adminTicketDialog(t: DormlyTicket) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val staffSpinner = Spinner(this)
        staffSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, staff)
        val statusSpinner = Spinner(this)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Pending", "In Progress", "Completed"))
        val note = EditText(this).apply { hint = "Optional instruction or admin note"; setText(t.note) }
        box.addView(text("Assign maintenance staff", 12f, muted, true)); box.addView(staffSpinner)
        box.addView(text("Request status", 12f, muted, true)); box.addView(statusSpinner)
        box.addView(text("Note", 12f, muted, true)); box.addView(note)
        AlertDialog.Builder(this).setTitle("Manage request #" + t.id).setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                t.assignedTo = staffSpinner.selectedItem.toString()
                t.status = statusSpinner.selectedItem.toString()
                t.note = note.text.toString()
                database.saveTickets(tickets)
                Toast.makeText(this, "Request updated.", Toast.LENGTH_SHORT).show()
                render()
            }.show()
    }

    private fun maintenanceDialog(t: DormlyTicket) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val statusSpinner = Spinner(this)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("In Progress", "Completed"))
        val note = EditText(this).apply { hint = "Add work performed or completion note"; setText(t.note); minLines = 2 }
        box.addView(text("Update status", 12f, muted, true)); box.addView(statusSpinner)
        box.addView(text("Work remarks", 12f, muted, true)); box.addView(note)
        AlertDialog.Builder(this).setTitle("Update task #" + t.id).setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save update") { _, _ ->
                t.status = statusSpinner.selectedItem.toString()
                t.note = note.text.toString()
                database.saveTickets(tickets)
                Toast.makeText(this, "Task progress saved.", Toast.LENGTH_SHORT).show()
                render()
            }.show()
    }

    private fun announcementDialog() {
        val box = EditText(this).apply { hint = "Write an announcement for residents"; minLines = 4; gravity = Gravity.TOP }
        AlertDialog.Builder(this).setTitle("New announcement").setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Post") { _, _ ->
                val message = box.text.toString().trim()
                if (message.isNotBlank()) {
                    database.addAnnouncement("Property announcement", message)
                    Toast.makeText(this, "Announcement posted.", Toast.LENGTH_LONG).show()
                    currentTab = "Updates"
                    render()
                } else {
                    Toast.makeText(this, "Please enter an announcement.", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }
}
