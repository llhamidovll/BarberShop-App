package com.barbershop.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.barbershop.model.Appointment
import com.barbershop.model.Barber
import com.barbershop.model.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * LoginScreen - Anmelde- und Registrierungsbildschirm
 * 
 * Ermöglicht Benutzern:
 * - Sich mit Email und Passwort zu registrieren
 * - Sich mit bestehenden Zugangsdaten anzumelden
 * - Zwischen Login und Registrierung zu wechseln
 * 
 * Verwendet Firebase Authentication für die Benutzerverwaltung.
 * 
 * @param navController Navigation Controller für Seitenwechsel
 */
@Composable
fun LoginScreen(navController: NavController) {
    // State-Variablen für Eingabefelder
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) } // Toggle zwischen Login/Register
    
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp), 
        verticalArrangement = Arrangement.Center, 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titel: "Registrieren" oder "Login"
        Text(
            text = if (isRegistering) "Registrieren" else "Login", 
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Email-Eingabefeld
        TextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Passwort-Eingabefeld (versteckt)
        TextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Passwort") }, 
            visualTransformation = PasswordVisualTransformation(), 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Haupt-Button (Anmelden oder Registrieren)
        Button(onClick = {
            // Validierung: Beide Felder müssen ausgefüllt sein
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Bitte Email und Passwort eingeben", Toast.LENGTH_SHORT).show()
                return@Button
            }
            
            if (isRegistering) {
                // REGISTRIERUNG: Neuen Benutzer erstellen
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show()
                        // Navigiere zu Home und entferne Login aus dem Back-Stack
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    }
                    .addOnFailureListener { e ->
                        // Fehlerbehandlung mit spezifischen Meldungen
                        val errorMsg = when {
                            e.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                                "Firebase nicht konfiguriert. Bitte Firebase Authentication in der Console aktivieren!"
                            e.message?.contains("network") == true -> 
                                "Netzwerkfehler. Bitte Internetverbindung prüfen."
                            else -> "Fehler: ${e.message}"
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
            } else {
                // LOGIN: Bestehenden Benutzer anmelden
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Login erfolgreich!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    }
                    .addOnFailureListener { e ->
                        val errorMsg = when {
                            e.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                                "Firebase nicht konfiguriert. Bitte Firebase Authentication in der Console aktivieren!"
                            e.message?.contains("network") == true -> 
                                "Netzwerkfehler. Bitte Internetverbindung prüfen."
                            else -> "Login fehlgeschlagen: ${e.message}"
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isRegistering) "Konto erstellen" else "Anmelden")
        }
        
        // Toggle-Button zwischen Login und Registrierung
        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(if (isRegistering) "Bereits ein Konto? Login" else "Noch kein Konto? Registrieren")
        }
    }
}

/**
 * HomeScreen - Friseur-Auswahlbildschirm
 * 
 * Zeigt eine Liste aller verfügbaren Friseure aus der Firestore-Datenbank.
 * Ermöglicht dem Benutzer:
 * - Einen Friseur auszuwählen (navigiert zu ServiceScreen)
 * - Sich abzumelden (Logout-Button in der TopBar)
 * - Zu seinen Terminen zu navigieren (Floating Action Button)
 * 
 * Lädt Daten aus Firestore Collection: "barbers"
 * 
 * @param navController Navigation Controller für Seitenwechsel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var barbers by remember { mutableStateOf(listOf<Barber>()) }
    val auth = FirebaseAuth.getInstance()

    // Lädt alle Friseure beim ersten Aufruf des Screens
    LaunchedEffect(Unit) {
        db.collection("barbers").get().addOnSuccessListener { result ->
            barbers = result.toObjects(Barber::class.java)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wähle deinen Friseur") },
                actions = {
                    // Logout-Button in der TopBar
                    IconButton(onClick = {
                        auth.signOut()
                        // Navigiere zu Login und lösche kompletten Back-Stack
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            // Schnellzugriff auf "Meine Termine"
            ExtendedFloatingActionButton(onClick = { navController.navigate("my_appointments") }) {
                Text("Meine Termine")
            }
        }
    ) { padding ->
        // Fallback-Nachricht wenn keine Friseure vorhanden
        if (barbers.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Keine Friseure gefunden. Bitte in Firestore 'barbers' anlegen.")
            }
        }
        
        // Liste aller Friseure
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(barbers) { barber ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                    // Navigiere zu ServiceScreen mit Friseur-Informationen
                    navController.navigate("services/${barber.id}/${barber.name}")
                }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = barber.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = barber.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/**
 * ServiceScreen - Dienstleistungs-Auswahlbildschirm
 * 
 * Zeigt alle verfügbaren Dienstleistungen (z.B. Herrenhaarschnitt, Damenhaarschnitt)
 * für den ausgewählten Friseur.
 * 
 * Jeder Service zeigt:
 * - Name des Services
 * - Dauer in Minuten
 * - Preis in Euro
 * 
 * Lädt Daten aus Firestore Collection: "services"
 * 
 * @param navController Navigation Controller für Seitenwechsel
 * @param barberId ID des ausgewählten Friseurs
 * @param barberName Name des ausgewählten Friseurs (für Anzeige)
 */
@Composable
fun ServiceScreen(navController: NavController, barberId: String, barberName: String) {
    val db = FirebaseFirestore.getInstance()
    var services by remember { mutableStateOf(listOf<Service>()) }

    // Lädt alle verfügbaren Services beim ersten Aufruf
    LaunchedEffect(Unit) {
        db.collection("services").get().addOnSuccessListener { result ->
            services = result.toObjects(Service::class.java)
        }
    }

    Scaffold(topBar = { 
        Text(
            "Service bei $barberName wählen", 
            modifier = Modifier.padding(16.dp), 
            style = MaterialTheme.typography.headlineSmall
        ) 
    }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(services) { service ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { 
                    // Navigiere zu AvailabilityScreen mit allen relevanten Informationen
                    navController.navigate("availability/$barberId/$barberName/${service.id}/${service.name}") 
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp), 
                        horizontalArrangement = Arrangement.SpaceBetween, 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Linke Seite: Service-Name und Dauer
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${service.durationMinutes} Min.", 
                                style = MaterialTheme.typography.bodySmall, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Rechte Seite: Preis
                        Text(
                            text = "${service.price}€", 
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * AvailabilityScreen - Verfügbarkeits- und Terminauswahlbildschirm
 * 
 * Ermöglicht dem Benutzer:
 * - Ein Datum aus den nächsten 7 Tagen auszuwählen
 * - Einen verfügbaren Zeitslot zu wählen (30-Minuten-Intervalle)
 * - Bereits gebuchte Slots werden ausgegraut angezeigt
 * 
 * Features:
 * - Wochenansicht (7 Tage voraus)
 * - Dynamische Slot-Verfügbarkeit (prüft gebuchte Termine in Firestore)
 * - Slots von 09:00 bis 18:00 Uhr in 30-Minuten-Schritten
 * 
 * @param navController Navigation Controller für Seitenwechsel
 * @param barberId ID des ausgewählten Friseurs
 * @param barberName Name des ausgewählten Friseurs
 * @param serviceId ID des ausgewählten Services
 * @param serviceName Name des ausgewählten Services
 */
@Composable
fun AvailabilityScreen(navController: NavController, barberId: String, barberName: String, serviceId: String, serviceName: String) {
    val db = FirebaseFirestore.getInstance()
    
    // Alle möglichen Zeitslots (30-Minuten-Intervalle von 09:00 bis 18:00)
    val allSlots = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", 
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    )
    
    // Set der bereits gebuchten Slots für das gewählte Datum
    var bookedSlots by remember { mutableStateOf(setOf<String>()) }
    
    // Generiere die nächsten 7 Tage (heute + 6 weitere Tage)
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format für Firestore
    val displayFormat = SimpleDateFormat("EEE, dd.MM", Locale.GERMAN) // Format für Anzeige
    val weekDates = (0..6).map { 
        val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, it) }
        Pair(dateFormat.format(date.time), displayFormat.format(date.time))
    }
    
    // Aktuell ausgewähltes Datum (Standard: heute)
    var selectedDate by remember { mutableStateOf(weekDates[0].first) }
    var selectedDateDisplay by remember { mutableStateOf(weekDates[0].second) }

    // Lädt gebuchte Slots für das ausgewählte Datum und den Friseur
    LaunchedEffect(selectedDate, barberId) {
        db.collection("appointments")
            .whereEqualTo("barberId", barberId)
            .whereEqualTo("date", selectedDate)
            .get()
            .addOnSuccessListener { result ->
                // Extrahiere alle bereits gebuchten Zeitslots
                bookedSlots = result.documents.mapNotNull { it.getString("timeSlot") }.toSet()
            }
    }

    Scaffold(topBar = { 
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Termin bei $barberName", style = MaterialTheme.typography.headlineSmall)
            Text("Service: $serviceName", style = MaterialTheme.typography.bodyMedium)
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // TEIL 1: Datumsauswahl (oberer Bereich - 30% des Bildschirms)
            Text("Wähle ein Datum:", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(0.3f)) {
                items(weekDates) { (date, display) ->
                    val isSelected = date == selectedDate
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                selectedDate = date
                                selectedDateDisplay = display
                            },
                        // Hervorheben des ausgewählten Datums
                        colors = if (isSelected) 
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) 
                        else 
                            CardDefaults.cardColors()
                    ) {
                        Text(
                            text = display,
                            modifier = Modifier.padding(16.dp),
                            style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // TEIL 2: Zeitslot-Auswahl (unterer Bereich - 70% des Bildschirms)
            Text("Verfügbare Zeiten für $selectedDateDisplay:", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(0.7f)) {
                items(allSlots) { slot ->
                    val isBooked = bookedSlots.contains(slot)
                    Button(
                        onClick = { 
                            // Navigiere zur Bestätigungsseite mit allen Informationen
                            navController.navigate("confirm/$barberId/$barberName/$serviceId/$serviceName/$selectedDate/$slot") 
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        enabled = !isBooked, // Deaktiviert wenn bereits gebucht
                        colors = if (isBooked) 
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) 
                        else 
                            ButtonDefaults.buttonColors()
                    ) {
                        Text(if (isBooked) "$slot (Belegt)" else slot)
                    }
                }
            }
        }
    }
}

/**
 * ConfirmScreen - Buchungsbestätigungsbildschirm
 * 
 * Zeigt eine Zusammenfassung aller ausgewählten Optionen und ermöglicht
 * die finale Buchung des Termins.
 * 
 * Features:
 * - Zeigt alle Termin-Details (Friseur, Service, Datum, Zeit)
 * - Race Condition Prevention: Prüft vor Buchung nochmals ob Slot verfügbar
 * - Loading State während der Buchung (verhindert Doppelbuchungen)
 * - Speichert Termin in Firestore
 * - Navigiert nach Buchung zu "Meine Termine"
 * 
 * @param navController Navigation Controller für Seitenwechsel
 * @param barberId ID des ausgewählten Friseurs
 * @param barberName Name des ausgewählten Friseurs
 * @param serviceId ID des ausgewählten Services
 * @param serviceName Name des ausgewählten Services
 * @param date Ausgewähltes Datum (Format: YYYY-MM-DD)
 * @param slot Ausgewählter Zeitslot (Format: HH:mm)
 */
@Composable
fun ConfirmScreen(navController: NavController, barberId: String, barberName: String, serviceId: String, serviceName: String, date: String, slot: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var isBooking by remember { mutableStateOf(false) } // Verhindert Doppelklicks

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        // Zusammenfassungs-Karte
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Zusammenfassung", style = MaterialTheme.typography.headlineMedium)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Friseur: $barberName")
                Text("Service: $serviceName")
                Text("Datum: $date")
                Text("Zeit: $slot")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        // Buchungs-Button
        Button(
            onClick = {
                isBooking = true
                
                // SICHERHEITSPRÜFUNG: Prüfe nochmals ob der Slot noch frei ist
                // Dies verhindert Race Conditions wenn mehrere Nutzer gleichzeitig buchen
                db.collection("appointments")
                    .whereEqualTo("barberId", barberId)
                    .whereEqualTo("date", date)
                    .whereEqualTo("timeSlot", slot)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.isEmpty) {
                            // Slot ist noch frei - Buchung durchführen
                            val appointment = Appointment(
                                userId = auth.currentUser?.uid ?: "",
                                barberId = barberId,
                                barberName = barberName,
                                serviceId = serviceId,
                                serviceName = serviceName,
                                date = date,
                                timeSlot = slot
                            )
                            
                            // Speichere Termin in Firestore
                            db.collection("appointments").add(appointment).addOnSuccessListener {
                                Toast.makeText(context, "Termin gebucht!", Toast.LENGTH_SHORT).show()
                                // Navigiere zu "Meine Termine" und entferne Buchungsflow aus Back-Stack
                                navController.navigate("my_appointments") {
                                    popUpTo("home")
                                }
                            }
                        } else {
                            // Slot wurde in der Zwischenzeit gebucht
                            isBooking = false
                            Toast.makeText(context, "Dieser Slot wurde gerade eben belegt!", Toast.LENGTH_LONG).show()
                            navController.popBackStack() // Zurück zur Zeitauswahl
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isBooking // Button deaktivieren während Buchung läuft
        ) {
            if (isBooking) {
                // Zeige Ladeanimation während der Buchung
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Jetzt verbindlich buchen")
            }
        }
    }
}

/**
 * MyAppointmentsScreen - Terminübersichtsbildschirm
 * 
 * Zeigt alle gebuchten Termine des aktuell eingeloggten Benutzers an.
 * 
 * Features:
 * - Lädt nur Termine des aktuellen Benutzers (gefiltert nach userId)
 * - Zeigt Termin-Details (Service, Friseur, Datum, Uhrzeit)
 * - Ermöglicht Stornierung mit Bestätigungsdialog
 * - Loading State während des Ladens
 * - Zeigt Fallback-Nachricht wenn keine Termine vorhanden
 * - Aktualisiert Liste automatisch nach Stornierung
 * 
 * @param navController Navigation Controller für Seitenwechsel
 */
@Composable
fun MyAppointmentsScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    
    // State-Variablen
    var appointments by remember { mutableStateOf(listOf<Appointment>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }

    /**
     * Lädt alle Termine des aktuellen Benutzers aus Firestore
     * Wird initial und nach jeder Stornierung aufgerufen
     */
    fun loadAppointments() {
        isLoading = true
        db.collection("appointments")
            .whereEqualTo("userId", auth.currentUser?.uid) // Nur eigene Termine
            .get()
            .addOnSuccessListener { result ->
                // Konvertiere Firestore Dokumente zu Appointment-Objekten
                appointments = result.documents.map { doc ->
                    doc.toObject(Appointment::class.java)!!.copy(id = doc.id)
                }
                isLoading = false
            }
    }

    // Lädt Termine beim ersten Aufruf des Screens
    LaunchedEffect(Unit) { loadAppointments() }

    // Bestätigungsdialog für Stornierung
    if (showCancelDialog && appointmentToCancel != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Termin stornieren?") },
            text = { 
                Text("Möchten Sie den Termin am ${appointmentToCancel!!.date} um ${appointmentToCancel!!.timeSlot} wirklich stornieren?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lösche Termin aus Firestore
                        db.collection("appointments").document(appointmentToCancel!!.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Termin erfolgreich storniert", Toast.LENGTH_SHORT).show()
                                loadAppointments() // Liste aktualisieren
                            }
                        showCancelDialog = false
                        appointmentToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ja, stornieren")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCancelDialog = false
                    appointmentToCancel = null
                }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    Scaffold(topBar = { 
        Text("Meine Termine", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall) 
    }) { padding ->
        // Loading State
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                CircularProgressIndicator() 
            }
        } 
        // Keine Termine vorhanden
        else if (appointments.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                Text("Du hast noch keine Termine.") 
            }
        } 
        // Termine anzeigen
        else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(appointments) { appt ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Termin-Informationen
                            Text("${appt.serviceName}", style = MaterialTheme.typography.titleLarge)
                            Text("bei ${appt.barberName}")
                            Text("Am ${appt.date} um ${appt.timeSlot}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Stornieren-Button
                            Button(
                                onClick = {
                                    appointmentToCancel = appt
                                    showCancelDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Stornieren")
                            }
                        }
                    }
                }
            }
        }
    }
}
