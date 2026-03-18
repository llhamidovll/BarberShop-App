# BarberShop

## Student
- Name: [DEIN NAME]
- Matrikelnummer: [DEINE MATRIKELNUMMER]
- Kurs / Modul: [KURS ODER MODUL]

## Projektbeschreibung
BarberShop ist eine Android-App zur Terminbuchung für einen Friseursalon. Benutzer können sich registrieren oder anmelden, einen Barber auswählen, einen Service auswählen, freie Zeitslots anzeigen lassen, einen Termin buchen und eigene Termine wieder stornieren.

## Verwendete Technologien
- Kotlin
- Jetpack Compose
- Jetpack Compose Navigation
- Firebase Authentication
- Cloud Firestore
- Android Studio

## Hauptfunktionen
1. Registrierung und Login mit E-Mail und Passwort
2. Auswahl eines Barbers
3. Auswahl eines Services mit Preis und Dauer
4. Anzeige freier Zeitslots
5. Terminbuchung
6. Anzeige eigener Termine
7. Stornierung von Terminen

## Projektstruktur
- `MainActivity.kt`  
  Einstiegspunkt der App und Navigation mit `NavHost`
- `Screens.kt`  
  Enthält alle Screens und die Hauptlogik
- `Models.kt`  
  Datenmodelle für Barber, Service und Appointment
- `app/google-services.json`  
  Firebase-Konfiguration
- `docs/`  
  Enthält Ablaufdiagramm und weitere Abgabeunterlagen

## Navigation / Screen Flow
- LoginScreen
- HomeScreen
- ServiceScreen
- AvailabilityScreen
- ConfirmScreen
- MyAppointmentsScreen

## Datenbank
Die App verwendet Cloud Firestore mit den Collections:
- `barbers`
- `services`
- `appointments`

Ein Appointment speichert u. a.:
- `userId`
- `barberId`
- `barberName`
- `serviceId`
- `serviceName`
- `date`
- `timeSlot`

## Login
Der Login erfolgt mit Firebase Authentication über E-Mail und Passwort.

## Zeitslot-Logik
Die App nutzt eine feste Liste möglicher Zeitslots. Für den ausgewählten Barber und das gewählte Datum werden aus Firestore alle bereits gebuchten Slots geladen. Diese werden in der UI als belegt markiert und deaktiviert.

## APK
Die APK-Datei befindet sich im Projekt / Repository und kann direkt auf einem Android-Gerät installiert werden.

## Ablaufdiagramm
Das Ablaufdiagramm liegt im Ordner `docs/` als:
- `BarberShop_Ablaufdiagramm.drawio`
- `BarberShop_Ablaufdiagramm.png`
- `BarberShop_Ablaufdiagramm.pdf`

## Starten des Projekts
1. Projekt in Android Studio öffnen
2. Gradle synchronisieren
3. Emulator oder Android-Gerät auswählen
4. App starten

## Hinweise
- Für die Ausführung wird eine funktionierende Firebase-Konfiguration benötigt.
- Die App verwendet Jetpack Compose und keine XML-Layouts.
- Die Navigation erfolgt vollständig über Jetpack Compose Navigation.

