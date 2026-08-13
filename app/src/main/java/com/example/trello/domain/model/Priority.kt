package com.example.trello.domain.model

/* Card priority. [sortWeight] lets the UI sort "highest priority first". */
enum class Priority(val label: String, val sortWeight: Int) {
    NONE("None", 0),
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun fromNameOrDefault(name: String): Priority =
            entries.firstOrNull { it.name == name } ?: NONE
    }
}
