package com.barbershop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.barbershop.ui.*
import com.barbershop.ui.theme.BarberShopTheme
import com.google.firebase.auth.FirebaseAuth

/**
 * MainActivity - Einstiegspunkt der Friseur-Termin-App
 * 
 * Diese Activity ist die Hauptaktivität der Anwendung und verwaltet
 * die gesamte Navigation zwischen den verschiedenen Screens.
 */
class MainActivity : ComponentActivity() {
    /**
     * Wird beim Start der Activity aufgerufen
     * Setzt den UI-Content mit Jetpack Compose
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarberShopTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * AppNavigation - Zentrale Navigation der App
 * 
 * Verwaltet alle Screen-Routen und deren Parameter.
 * Prüft beim Start, ob ein User eingeloggt ist und navigiert entsprechend.
 * 
 * Navigation-Flow:
 * 1. Login/Register → 2. Home (Friseur wählen) → 3. Services (Service wählen)
 * → 4. Availability (Datum & Zeit wählen) → 5. Confirm (Buchung bestätigen)
 * → 6. MyAppointments (Termine verwalten)
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Prüft, ob bereits ein User eingeloggt ist
    // Wenn ja: direkt zu Home, wenn nein: zu Login
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        
        // Route: Login/Registrierungs-Screen
        composable("login") { LoginScreen(navController) }
        
        // Route: Home-Screen (Friseur-Auswahl)
        composable("home") { HomeScreen(navController) }
        
        // Route: Service-Screen (Service-Auswahl für gewählten Friseur)
        // Parameter: barberId, barberName
        composable(
            "services/{barberId}/{barberName}",
            arguments = listOf(
                navArgument("barberId") { type = NavType.StringType },
                navArgument("barberName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barberId = backStackEntry.arguments?.getString("barberId") ?: ""
            val barberName = backStackEntry.arguments?.getString("barberName") ?: ""
            ServiceScreen(navController, barberId, barberName)
        }
        
        // Route: Availability-Screen (Datums- und Zeitauswahl)
        // Parameter: barberId, barberName, serviceId, serviceName
        composable(
            "availability/{barberId}/{barberName}/{serviceId}/{serviceName}",
            arguments = listOf(
                navArgument("barberId") { type = NavType.StringType },
                navArgument("barberName") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("serviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barberId = backStackEntry.arguments?.getString("barberId") ?: ""
            val barberName = backStackEntry.arguments?.getString("barberName") ?: ""
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            AvailabilityScreen(navController, barberId, barberName, serviceId, serviceName)
        }
        
        // Route: Confirm-Screen (Buchungsbestätigung)
        // Parameter: barberId, barberName, serviceId, serviceName, date, slot
        composable(
            "confirm/{barberId}/{barberName}/{serviceId}/{serviceName}/{date}/{slot}",
            arguments = listOf(
                navArgument("barberId") { type = NavType.StringType },
                navArgument("barberName") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("serviceName") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("slot") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barberId = backStackEntry.arguments?.getString("barberId") ?: ""
            val barberName = backStackEntry.arguments?.getString("barberName") ?: ""
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val slot = backStackEntry.arguments?.getString("slot") ?: ""
            ConfirmScreen(navController, barberId, barberName, serviceId, serviceName, date, slot)
        }
        
        // Route: MyAppointments-Screen (Übersicht eigener Termine)
        composable("my_appointments") { MyAppointmentsScreen(navController) }
    }
}
