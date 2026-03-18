# Friseur-Termin-App - Ausführliche Dokumentation

## Projektinformationen

**Student:**
- Name: Abdulhamid Suliman
- Matrikelnummer: 1817917
- Studiengang: Wirtschaftsinformatik

**Modul:**
- Modulname: App Entwicklung mit Android
- Dozent: Holger Zimmermann

**Datum:** 9. Februar 2026

---

## Inhaltsverzeichnis
1. [Projektübersicht](#projektübersicht)
2. [Technologie-Stack](#technologie-stack)
3. [Architektur](#architektur)
4. [Features & Funktionen](#features--funktionen)
5. [Datenmodelle](#datenmodelle)
6. [Screen-Beschreibungen](#screen-beschreibungen)
7. [Firebase-Konfiguration](#firebase-konfiguration)
8. [Installation & Setup](#installation--setup)
9. [Verwendung](#verwendung)
10. [Projektstruktur](#projektstruktur)

---

## Projektübersicht

### Beschreibung
Die **Friseur-Termin-App** ist eine Android-Anwendung, die es Kunden ermöglicht, online Termine bei verschiedenen Friseuren zu buchen. Die App bietet eine intuitive Benutzeroberfläche zur Auswahl von Friseuren, Dienstleistungen und verfügbaren Zeitslots.

### Zielgruppe
- **Kunden**: Personen, die einen Friseurtermin buchen möchten
- **Mehrere Friseure**: Die App unterstützt mehrere Friseure in einem Salon

### Hauptziele
- Vereinfachung des Buchungsprozesses
- Vermeidung von Terminüberschneidungen
- Benutzerfreundliche Oberfläche
- Echtzeitaktualisierung verfügbarer Termine

---

## Technologie-Stack

### Frontend
- **Kotlin**: Primäre Programmiersprache
- **Jetpack Compose**: Modernes UI-Framework für Android
- **Material Design 3**: Design-System für konsistente UI

### Backend & Datenbank
- **Firebase Authentication**: Benutzerverwaltung (Email/Passwort)
- **Cloud Firestore**: NoSQL-Datenbank für Echtzeit-Datensynchronisation

### Build & Dependencies
- **Gradle (Kotlin DSL)**: Build-System
- **Android Studio**: Entwicklungsumgebung
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 35
- **Compile SDK**: 35

### Verwendete Libraries
```kotlin
// Firebase
firebase-bom: 34.8.0
firebase-analytics
firebase-auth
firebase-firestore

// Jetpack Compose
compose-bom: 2024.09.00
compose-material3
compose-ui
navigation-compose: 2.8.3

// Kotlin
kotlin: 2.0.21
core-ktx: 1.10.1
```

---

## Architektur

### Architekturmuster
Die App folgt einem **komponentenbasierten Ansatz** mit klarer Trennung von:
- **UI Layer**: Jetpack Compose Screens
- **Data Layer**: Firebase Firestore
- **Navigation Layer**: Jetpack Navigation

### Architektur-Diagramm

```
┌─────────────────────────────────────────┐
│          MainActivity                    │
│    (Single Activity Architecture)        │
└─────────────────┬───────────────────────┘
                  │
         ┌────────▼────────┐
         │  AppNavigation  │
         │  (NavHost)      │
         └────────┬────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼──────┐    ┌───────▼──────────┐
│  UI Screens  │    │   Data Models    │
│              │    │                  │
│ - LoginScreen│    │ - Barber         │
│ - HomeScreen │    │ - Service        │
│ - etc.       │    │ - Appointment    │
└───────┬──────┘    └───────┬──────────┘
        │                   │
        └─────────┬─────────┘
                  │
         ┌────────▼────────┐
         │     Firebase    │
         │                 │
         │ - Auth          │
         │ - Firestore     │
         └─────────────────┘
```

### Navigation-Flow

```
Start
  │
  ├── Nicht eingeloggt → LoginScreen
  │                         │
  │                         ├─ Registrieren → HomeScreen
  │                         └─ Anmelden → HomeScreen
  │
  └── Bereits eingeloggt → HomeScreen
                              │
                              ├─ Friseur wählen → ServiceScreen
                              │                      │
                              │                      └─ Service wählen → AvailabilityScreen
                              │                                             │
                              │                                             └─ Datum & Zeit → ConfirmScreen
                              │                                                                  │
                              │                                                                  └─ Buchen → MyAppointmentsScreen
                              │
                              └─ "Meine Termine" → MyAppointmentsScreen
                                                       │
                                                       └─ Termin stornieren (mit Bestätigung)
```

---

## Features & Funktionen

### 1. Authentifizierung
**Login/Registrierung**
- Email/Passwort-basierte Authentifizierung
- Toggle zwischen Login und Registrierung
- Umfassende Fehlerbehandlung
- Automatische Navigation nach erfolgreicher Anmeldung

**Sicherheit**
- Passwörter werden von Firebase verschlüsselt gespeichert
- Session-Management durch Firebase Authentication
- Logout-Funktion mit vollständigem Session-Reset

### 2. Friseur-Auswahl
**Features**
- Liste aller verfügbaren Friseure
- Name und Beschreibung jedes Friseurs
- Klickbare Karten für intuitive Auswahl
- Echtzeit-Laden aus Firestore

### 3. Service-Auswahl
**Features**
- Alle verfügbaren Dienstleistungen
- Anzeige von:
  - Servicename (z.B. "Herrenhaarschnitt")
  - Dauer in Minuten
  - Preis in Euro
- Übersichtliches Layout mit Preis rechts

### 4. Terminbuchung

**Wochenansicht**
- Auswahl aus den nächsten 7 Tagen
- Deutsches Datumsformat (z.B. "Mo, 03.02")
- Hervorhebung des ausgewählten Datums

**Zeitslot-Auswahl**
- 30-Minuten-Intervalle von 09:00 bis 18:00 Uhr
- Insgesamt 18 Slots pro Tag
- Bereits gebuchte Slots werden ausgegraut
- Echtzeit-Verfügbarkeitsprüfung

**Buchungsbestätigung**
- Übersicht aller gewählten Optionen
- Race Condition Prevention (Doppelbuchungsschutz)
- Loading State während der Buchung
- Erfolgsbenachrichtigung

### 5. Terminverwaltung

**Meine Termine**
- Liste aller gebuchten Termine
- Anzeige von:
  - Servicename
  - Friseurname
  - Datum und Uhrzeit
- Stornierungsfunktion mit Bestätigungsdialog
- Automatische Aktualisierung nach Stornierung

### 6. UI/UX Features
- **Material Design 3**: Moderne, konsistente Benutzeroberfläche
- **Responsive Layout**: Anpassung an verschiedene Bildschirmgrößen
- **Toast-Benachrichtigungen**: Feedback bei Aktionen
- **Loading States**: Ladeanimationen bei Netzwerkoperationen
- **Error Handling**: Benutzerfreundliche Fehlermeldungen

---

## Datenmodelle

### 1. Barber (Friseur)
```kotlin
data class Barber(
    @DocumentId val id: String = "",        // Firestore-generierte ID
    val name: String = "",                   // Name des Friseurs
    val description: String = ""             // Beschreibung/Spezialisierung
)
```

**Firestore Collection**: `barbers`

**Beispiel-Dokument**:
```json
{
  "name": "Anna Schmidt",
  "description": "Spezialistin für Damenhaarschnitte"
}
```

### 2. Service (Dienstleistung)
```kotlin
data class Service(
    @DocumentId val id: String = "",        // Firestore-generierte ID
    val name: String = "",                   // Name des Services
    val price: Double = 0.0,                 // Preis in Euro
    val durationMinutes: Int = 30            // Dauer in Minuten
)
```

**Firestore Collection**: `services`

**Beispiel-Dokument**:
```json
{
  "name": "Herrenhaarschnitt",
  "price": 25.0,
  "durationMinutes": 30
}
```

### 3. Appointment (Termin)
```kotlin
data class Appointment(
    @DocumentId val id: String = "",        // Firestore-generierte ID
    val userId: String = "",                 // ID des Kunden (Firebase Auth)
    val barberId: String = "",               // ID des Friseurs
    val barberName: String = "",             // Name des Friseurs (denormalisiert)
    val serviceId: String = "",              // ID des Services
    val serviceName: String = "",            // Name des Services (denormalisiert)
    val date: String = "",                   // Datum (Format: YYYY-MM-DD)
    val timeSlot: String = "",               // Zeitslot (Format: HH:mm)
    val timestamp: Long = System.currentTimeMillis()  // Buchungszeitpunkt
)
```

**Firestore Collection**: `appointments`

**Beispiel-Dokument**:
```json
{
  "userId": "abc123xyz...",
  "barberId": "HezCOtQ5O5K4iWI...",
  "barberName": "Anna Schmidt",
  "serviceId": "Nij2c1AmcqtTIZ8...",
  "serviceName": "Damenhaarschnitt",
  "date": "2026-02-10",
  "timeSlot": "14:30",
  "timestamp": 1738684323000
}
```

**Indexierung in Firestore**:
- Index auf `userId` für schnelles Laden eigener Termine
- Composite Index auf `barberId` + `date` für Verfügbarkeitsprüfung

---

## Screen-Beschreibungen

### 1. LoginScreen

**Zweck**: Authentifizierung von Benutzern

**UI-Elemente**:
- Email-Eingabefeld
- Passwort-Eingabefeld (versteckt)
- "Anmelden" / "Konto erstellen" Button
- Toggle-Link zwischen Login und Registrierung

**Funktionalität**:
- Validierung: Beide Felder müssen ausgefüllt sein
- Firebase Authentication Integration
- Fehlerbehandlung mit spezifischen Meldungen
- Automatische Navigation nach erfolgreichem Login

**Navigation**:
- Bei Erfolg → `HomeScreen`

---

### 2. HomeScreen

**Zweck**: Auswahl eines Friseurs

**UI-Elemente**:
- TopBar mit Titel und Logout-Button
- Liste aller Friseure (LazyColumn)
- Floating Action Button "Meine Termine"

**Funktionalität**:
- Lädt Friseure aus Firestore
- Klick auf Friseur → Navigation zu ServiceScreen
- Logout → Zurück zu LoginScreen
- Schnellzugriff auf eigene Termine

**Navigation**:
- Friseur wählen → `ServiceScreen`
- FAB → `MyAppointmentsScreen`
- Logout → `LoginScreen`

---

### 3. ServiceScreen

**Zweck**: Auswahl einer Dienstleistung

**UI-Elemente**:
- TopBar mit Friseurname
- Liste aller Services (LazyColumn)
- Jede Karte zeigt: Name, Dauer, Preis

**Funktionalität**:
- Lädt alle Services aus Firestore
- Klick auf Service → Navigation zu AvailabilityScreen

**Navigation**:
- Service wählen → `AvailabilityScreen`

---

### 4. AvailabilityScreen

**Zweck**: Auswahl von Datum und Zeitslot

**UI-Elemente**:
- TopBar mit Friseur- und Service-Info
- Datumsauswahl (oberer Bereich, 30% des Screens)
  - 7 auswählbare Tage
  - Hervorhebung des gewählten Datums
- Zeitslot-Auswahl (unterer Bereich, 70% des Screens)
  - 18 Slots (09:00 - 18:00, 30-Min-Intervalle)
  - Gebuchte Slots ausgegraut

**Funktionalität**:
- Generiert nächste 7 Tage dynamisch
- Lädt gebuchte Termine für gewähltes Datum und Friseur
- Aktualisiert Verfügbarkeit bei Datumswechsel
- Klick auf freien Slot → Navigation zu ConfirmScreen

**Navigation**:
- Slot wählen → `ConfirmScreen`

---

### 5. ConfirmScreen

**Zweck**: Finale Buchungsbestätigung

**UI-Elemente**:
- Zusammenfassungs-Karte mit allen Details
- "Jetzt verbindlich buchen" Button
- Loading Indicator während Buchung

**Funktionalität**:
- **Race Condition Prevention**:
  1. Prüft vor Buchung erneut ob Slot frei ist
  2. Nur wenn frei: Buchung durchführen
  3. Wenn belegt: Fehlermeldung und Zurück
- Speichert Termin in Firestore
- Loading State verhindert Doppelklicks

**Navigation**:
- Bei Erfolg → `MyAppointmentsScreen`
- Bei Fehler → Zurück zu `AvailabilityScreen`

---

### 6. MyAppointmentsScreen

**Zweck**: Übersicht und Verwaltung eigener Termine

**UI-Elemente**:
- TopBar mit Titel
- Loading Indicator (beim Laden)
- Fallback-Text wenn keine Termine
- Liste der Termine (LazyColumn)
- "Stornieren" Button pro Termin
- Bestätigungsdialog

**Funktionalität**:
- Lädt nur Termine des aktuellen Benutzers
- Zeigt alle Termin-Details
- Stornierung mit Bestätigungsdialog
- Automatisches Neuladen nach Stornierung

**Navigation**:
- Keine (End-Screen)

---

## Firebase-Konfiguration

### 1. Projekt-Setup

**Firebase Console**: https://console.firebase.google.com

**Projektinformationen**:
- Projektname: `barbershop-498b1`
- Package Name: `com.barbershop`

### 2. Authentication Setup

**Schritte**:
1. Firebase Console → Build → Authentication
2. "Get started" klicken
3. Sign-in method → "Email/Password"
4. Enable → Save

**Registrierte Benutzer**:
- Sichtbar unter: Authentication → Users Tab
- Zeigt Email und UID

### 3. Firestore Database Setup

**Datenbank-Modus**: Test Mode (für Entwicklung)

**Collections**:
```
firestore
├── barbers/
│   ├── [auto-id]/
│   │   ├── name: string
│   │   └── description: string
│   └── ...
├── services/
│   ├── [auto-id]/
│   │   ├── name: string
│   │   ├── price: number
│   │   └── durationMinutes: number
│   └── ...
└── appointments/
    ├── [auto-id]/
    │   ├── userId: string
    │   ├── barberId: string
    │   ├── barberName: string
    │   ├── serviceId: string
    │   ├── serviceName: string
    │   ├── date: string
    │   ├── timeSlot: string
    │   └── timestamp: number
    └── ...
```

### 4. Security Rules

**Produktions-Rules**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Barbers und Services: Jeder kann lesen, nur Admin schreiben
    match /barbers/{barberId} {
      allow read: if true;
      allow write: if false;  // Nur via Console/Backend
    }
    
    match /services/{serviceId} {
      allow read: if true;
      allow write: if false;  // Nur via Console/Backend
    }
    
    // Appointments: Authentifizierte Nutzer
    match /appointments/{appointmentId} {
      allow read: if request.auth != null && 
                     resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
                       request.resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null && 
                       resource.data.userId == request.auth.uid;
    }
  }
}
```

### 5. google-services.json

**Speicherort**: `app/google-services.json`

**Wichtig**: Diese Datei wird automatisch von Firebase generiert und enthält:
- API Keys
- Project ID
- App ID

---

## Installation & Setup

### Voraussetzungen
- Android Studio (neueste Version)
- JDK 11 oder höher
- Android SDK (Min: API 30, Target: API 35)
- Firebase-Konto

### Schritt-für-Schritt Installation

#### 1. Projekt klonen/öffnen
```bash
# Projekt in Android Studio öffnen
File → Open → BarberShop Ordner auswählen
```

#### 2. Firebase-Projekt erstellen
1. Gehe zu https://console.firebase.google.com
2. "Projekt hinzufügen"
3. Projektname eingeben (z.B. "Friseur-App")
4. Analytics aktivieren (optional)

#### 3. Android-App zu Firebase hinzufügen
1. Firebase Console → Projekteinstellungen
2. "App hinzufügen" → Android-Icon
3. Package Name: `com.barbershop`
4. App registrieren
5. `google-services.json` herunterladen
6. Datei in `app/` Ordner kopieren

#### 4. Firebase Authentication aktivieren
1. Firebase Console → Build → Authentication
2. "Get started"
3. Sign-in method → Email/Password → Enable → Save

#### 5. Firestore Database erstellen
1. Firebase Console → Build → Firestore Database
2. "Create database"
3. Start in test mode
4. Region auswählen (z.B. europe-west3)
5. Enable

#### 6. Testdaten in Firestore anlegen

**Barbers Collection**:
```
Collection ID: barbers

Document 1:
  name: "Anna Schmidt"
  description: "Spezialistin für Damenhaarschnitte"

Document 2:
  name: "Max Müller"
  description: "Experte für Herrenhaarschnitte"

Document 3:
  name: "Lisa Wagner"
  description: "Stylistin für modernes Färben"
```

**Services Collection**:
```
Collection ID: services

Document 1:
  name: "Herrenhaarschnitt"
  price: 25
  durationMinutes: 30

Document 2:
  name: "Damenhaarschnitt"
  price: 35
  durationMinutes: 45

Document 3:
  name: "Bart trimmen"
  price: 15
  durationMinutes: 20
```

#### 7. Projekt bauen
```
Android Studio:
Build → Rebuild Project
```

#### 8. App ausführen
```
Android Studio:
Run → Run 'app' (Grüner Play-Button)
```

---

## Verwendung

### Erste Schritte

#### 1. Registrierung
1. App öffnen
2. "Noch kein Konto? Registrieren" klicken
3. Email und Passwort eingeben
4. "Konto erstellen" klicken

#### 2. Termin buchen
1. Friseur aus Liste wählen
2. Gewünschten Service wählen
3. Datum aus den nächsten 7 Tagen wählen
4. Verfügbaren Zeitslot wählen
5. Buchung in der Zusammenfassung bestätigen
6. "Jetzt verbindlich buchen" klicken

#### 3. Termine verwalten
1. "Meine Termine" Button (Floating Action Button) klicken
2. Übersicht aller gebuchten Termine
3. Termin stornieren: "Stornieren" Button → Bestätigen

#### 4. Logout
1. Home-Screen öffnen
2. Logout-Icon (oben rechts) klicken

### Typische Nutzungsszenarien

**Szenario 1: Kunde bucht erstmalig**
```
1. Registrierung → 2. Friseur wählen → 3. Service wählen → 
4. Datum wählen → 5. Zeit wählen → 6. Bestätigen
```

**Szenario 2: Wiederholungskunde**
```
1. Login → 2. Direkter Zugriff auf bekannte Friseure → 
3. Schnelle Buchung
```

**Szenario 3: Terminänderung**
```
1. "Meine Termine" öffnen → 2. Alten Termin stornieren → 
3. Neuen Termin buchen
```

---

## Projektstruktur

```
BarberShop/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/barbershop/
│   │   │   │   ├── MainActivity.kt           # Haupt-Activity mit Navigation
│   │   │   │   ├── model/
│   │   │   │   │   └── Models.kt             # Datenmodelle (Barber, Service, Appointment)
│   │   │   │   └── ui/
│   │   │   │       ├── Screens.kt            # Alle UI-Screens
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt          # Farbdefinitionen
│   │   │   │           ├── Theme.kt          # App-Theme
│   │   │   │           └── Type.kt           # Typografie
│   │   │   ├── res/                          # Android-Ressourcen
│   │   │   └── AndroidManifest.xml           # App-Manifest
│   │   └── test/                             # Unit-Tests
│   ├── build.gradle.kts                      # App-Level Build-Konfiguration
│   └── google-services.json                  # Firebase-Konfiguration
├── gradle/
│   └── libs.versions.toml                    # Dependency-Versionen
├── build.gradle.kts                          # Project-Level Build-Konfiguration
├── settings.gradle.kts                       # Gradle-Settings
└── DOKUMENTATION.md                          # Diese Dokumentation
```

### Wichtige Dateien erklärt

**MainActivity.kt**
- Single Activity Architecture
- AppNavigation mit NavHost
- Routing zwischen Screens

**Screens.kt** (1250+ Zeilen)
- Alle 6 UI-Screens
- Jetpack Compose UI-Komponenten
- Firebase-Integration

**Models.kt**
- Datenmodelle mit Firestore-Annotations
- Data Classes für Typsicherheit

**build.gradle.kts (app)**
- Dependencies (Firebase, Compose)
- SDK-Versionen
- Google Services Plugin

**google-services.json**
- Firebase-Projektkonfiguration
- API-Keys und IDs

---

## Best Practices & Design-Entscheidungen

### 1. Race Condition Prevention
**Problem**: Zwei Nutzer buchen gleichzeitig denselben Slot

**Lösung**: Doppelte Prüfung in ConfirmScreen
```kotlin
// Prüfe VOR der Buchung nochmals ob Slot frei ist
db.collection("appointments")
    .whereEqualTo("barberId", barberId)
    .whereEqualTo("date", date)
    .whereEqualTo("timeSlot", slot)
    .get()
    .addOnSuccessListener { result ->
        if (result.isEmpty) {
            // Nur wenn frei: Buchung durchführen
        } else {
            // Slot belegt: Fehlermeldung
        }
    }
```

### 2. Denormalisierung
**Warum**: `barberName` und `serviceName` werden in Appointments gespeichert

**Vorteile**:
- Schnellere Anzeige (kein JOIN nötig)
- Weniger Firestore-Reads (Kosten!)
- Funktioniert auch wenn Barber/Service gelöscht wird

**Nachteil**:
- Redundanz (akzeptabel für Read-Heavy Apps)

### 3. Loading States
**Implementierung**: Überall wo Netzwerk-Calls stattfinden
- Verhindert Benutzerverwirrung
- Gibt Feedback
- Verbessert UX

### 4. Error Handling
**Strategie**: Spezifische Fehlermeldungen
```kotlin
val errorMsg = when {
    e.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
        "Firebase nicht konfiguriert..."
    e.message?.contains("network") == true -> 
        "Netzwerkfehler..."
    else -> "Fehler: ${e.message}"
}
```

### 5. State Management
**Verwendung**: `remember` und `mutableStateOf`
- Einfach und effektiv für diese App-Größe
- Keine externe State-Management Library nötig

---

## Erweiterungsmöglichkeiten

### Kurzfristig (Quick Wins)
1. **Profilbilder**: Für Friseure
2. **Benachrichtigungen**: Push-Notifications für Erinnerungen
3. **Bewertungen**: Friseure bewerten
4. **Favoriten**: Lieblingsfriseure speichern
5. **Filter**: Services nach Preis/Dauer filtern

### Mittelfristig
1. **Admin-Panel**: Web-Interface für Salonbesitzer
2. **Kalender-Integration**: Export zu Google Calendar
3. **Warteliste**: Bei ausgebuchten Tagen
4. **Multi-Language**: Internationalisierung
5. **Dark Mode**: Automatisch oder manuell

### Langfristig
1. **Zahlungsintegration**: Online-Bezahlung
2. **Video-Chat**: Vorab-Beratung
3. **KI-Empfehlungen**: Personalisierte Service-Vorschläge
4. **Treueprogramm**: Punkte sammeln
5. **Social Features**: Termine mit Freunden teilen

---

## Häufige Probleme & Lösungen

### Problem 1: "CONFIGURATION_NOT_FOUND"
**Ursache**: Firebase Authentication nicht aktiviert

**Lösung**:
1. Firebase Console → Authentication
2. Sign-in method → Email/Password → Enable

### Problem 2: Keine Friseure sichtbar
**Ursache**: Firestore Rules oder leere Collection

**Lösung**:
1. Prüfe Firestore Rules (allow read: if true)
2. Erstelle Testdaten in "barbers" Collection

### Problem 3: App crasht beim Start
**Ursache**: google-services.json fehlt

**Lösung**:
1. Firebase Console → Projekteinstellungen
2. google-services.json herunterladen
3. In app/ Ordner kopieren
4. Rebuild Project

### Problem 4: Gradle Sync Fehler
**Ursache**: Dependency-Konflikte

**Lösung**:
```bash
File → Invalidate Caches → Invalidate and Restart
```

---

## Performance-Optimierungen

### Firestore Queries
- **Index**: Composite Index auf `barberId` + `date`
- **Limit**: Nur nötige Felder laden
- **Caching**: Offline-Persistenz aktiviert

### UI Performance
- **LazyColumn**: Effizientes Scrolling
- **remember**: Verhindert unnötige Recompositions
- **Minimal State**: Nur notwendiger State

### Netzwerk
- **Batch Reads**: Mehrere Dokumente in einem Call
- **Denormalisierung**: Reduziert Anzahl der Reads

---

## Testing

### Manuelle Tests
- [x] Registrierung mit Email/Passwort
- [x] Login mit bestehenden Credentials
- [x] Friseur-Auswahl
- [x] Service-Auswahl
- [x] Datums- und Zeit-Auswahl
- [x] Termin-Buchung
- [x] Termin-Anzeige
- [x] Termin-Stornierung
- [x] Logout

### Edge Cases
- [x] Gleichzeitige Buchung (Race Condition)
- [x] Netzwerkfehler
- [x] Leere Datenbank
- [x] Ungültige Eingaben

---

## Projektdetails

### Akademische Informationen
- **Student**: Abdulhamid Suliman
- **Matrikelnummer**: 1817917
- **Studiengang**: Wirtschaftsinformatik
- **Modul**: App Entwicklung mit Android
- **Dozent**: Prof. Holger Zimmermann

### Technische Informationen
- **Projektname**: Friseur-Termin-App (BarberShop)
- **Version**: 1.0
- **Package Name**: com.barbershop
- **Entwicklungsumgebung**: Android Studio
- **Programmiersprache**: Kotlin
- **Framework**: Jetpack Compose
- **Backend**: Firebase (Authentication & Firestore)


**Ende der Dokumentation**

