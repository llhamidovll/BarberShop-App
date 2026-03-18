package com.barbershop.model

import com.google.firebase.firestore.DocumentId

/**
 * Datenmodell für einen Friseur
 * 
 * @property id Die eindeutige ID des Friseurs (automatisch von Firestore generiert)
 * @property name Der Name des Friseurs (z.B. "Anna Schmidt")
 * @property description Beschreibung der Spezialisierung (z.B. "Expertin für Damenhaarschnitte")
 */
data class Barber(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = ""
)

/**
 * Datenmodell für eine Dienstleistung (Service)
 * 
 * @property id Die eindeutige ID des Services (automatisch von Firestore generiert)
 * @property name Name des Services (z.B. "Herrenhaarschnitt")
 * @property price Preis in Euro (z.B. 25.0)
 * @property durationMinutes Dauer in Minuten (Standard: 30 Minuten)
 */
data class Service(
    @DocumentId val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val durationMinutes: Int = 30
)

/**
 * Datenmodell für einen gebuchten Termin
 * 
 * @property id Die eindeutige ID des Termins (automatisch von Firestore generiert)
 * @property userId Die ID des Kunden, der den Termin gebucht hat
 * @property barberId Die ID des ausgewählten Friseurs
 * @property barberName Name des Friseurs (für schnellere Anzeige ohne Lookup)
 * @property serviceId Die ID des gebuchten Services
 * @property serviceName Name des Services (für schnellere Anzeige ohne Lookup)
 * @property date Das Datum im Format YYYY-MM-DD (z.B. "2026-02-10")
 * @property timeSlot Die Uhrzeit im Format HH:mm (z.B. "14:30")
 * @property timestamp Unix-Timestamp für Sortierung und Tracking
 */
data class Appointment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val barberId: String = "",
    val barberName: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val date: String = "", // Format: YYYY-MM-DD
    val timeSlot: String = "", // Format: HH:mm
    val timestamp: Long = System.currentTimeMillis()
)
