package dev.leaderboard.sample.hub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.leaderboard.sdk.hub.LeaderboardHubSDK
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvUser: TextView
    private lateinit var tvLog: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var btnLogout: Button
    private lateinit var btnGetGames: Button
    private lateinit var btnGetScores: Button
    private lateinit var btnProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        tvStatus = findViewById(R.id.tvStatus)
        tvUser = findViewById(R.id.tvUser)
        tvLog = findViewById(R.id.tvLog)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        btnGetGames = findViewById(R.id.btnGetGames)
        btnGetScores = findViewById(R.id.btnGetScores)
        btnProfile = findViewById(R.id.btnProfile)

        // Initialize SDK
        LeaderboardHubSDK.init(this)
        log("SDK initialized")

        // Try auto-restore for returning users
        lifecycleScope.launch {
            val user = LeaderboardHubSDK.tryAutoRestore()
            if (user != null) {
                updateUI()
                log("Auto-restored: Welcome back, ${user.username}!")
            } else {
                log("No saved session - please login or register")
            }
        }

        // Register
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                log("Please enter email and password")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val username = "User_${Random.nextInt(1000, 9999)}"
                    log("Registering: $email")
                    val response = LeaderboardHubSDK.register(
                        email = email,
                        password = password,
                        username = username
                    )
                    updateUI()
                    log("Registered successfully!")
                    log("Username: ${response.user.username}")
                    log("Token saved securely")
                } catch (e: HttpException) {
                    when (e.code()) {
                        400 -> log("Invalid email or password format")
                        409 -> log("Email already registered - try logging in")
                        else -> log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                log("Please enter email and password")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    log("Logging in: $email")
                    val response = LeaderboardHubSDK.login(email, password)
                    updateUI()
                    log("Logged in: ${response.user.username}")
                } catch (e: HttpException) {
                    when (e.code()) {
                        401 -> log("Invalid email or password")
                        else -> log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Logout
        btnLogout.setOnClickListener {
            LeaderboardHubSDK.logout()
            updateUI()
            log("Logged out")
        }

        // Get My Games
        btnGetGames.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = LeaderboardHubSDK.getUserGames()
                    log("--- My Games (${response.total_games}) ---")
                    if (response.games.isEmpty()) {
                        log("No games played yet!")
                        log("Play some games to see your stats here")
                    } else {
                        response.games.forEach { game ->
                            log("${game.app.name}: ${game.total_scores} scores")
                            game.best_scores.forEach { best ->
                                log("  ${best.leaderboard.name}: ${best.score}")
                            }
                        }
                    }
                } catch (e: HttpException) {
                    when (e.code()) {
                        401 -> log("Please login first")
                        else -> log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IllegalStateException) {
                    log("Error: ${e.message}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Get All Scores
        btnGetScores.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = LeaderboardHubSDK.getUserScores(limit = 20)
                    log("--- Recent Scores (${response.total_scores} total) ---")
                    if (response.scores.isEmpty()) {
                        log("No scores submitted yet!")
                    } else {
                        response.scores.forEach { entry ->
                            val gameName = entry.app?.name ?: "Unknown Game"
                            val leaderboardName = entry.leaderboard?.name ?: "Leaderboard"
                            log("$gameName / $leaderboardName: ${entry.score.score}")
                        }
                    }
                } catch (e: HttpException) {
                    when (e.code()) {
                        401 -> log("Please login first")
                        else -> log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IllegalStateException) {
                    log("Error: ${e.message}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // View Profile
        btnProfile.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val user = LeaderboardHubSDK.getUserProfile()
                    log("--- Profile ---")
                    log("Username: ${user.username}")
                    log("Email: ${user.email}")
                    log("Created: ${user.created_at}")
                    if (user.avatar_url != null) {
                        log("Avatar: ${user.avatar_url}")
                    }
                } catch (e: HttpException) {
                    when (e.code()) {
                        401 -> log("Please login first")
                        else -> log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IllegalStateException) {
                    log("Error: ${e.message}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }
    }

    private fun updateUI() {
        if (LeaderboardHubSDK.isLoggedIn()) {
            val user = LeaderboardHubSDK.getCurrentUser()
            tvUser.text = "User: ${user?.username ?: "logged in"}"
        } else {
            tvUser.text = "User: Not logged in"
        }
    }

    private fun log(message: String) {
        val current = tvLog.text.toString()
        tvLog.text = "$current\n$message"
    }
}
