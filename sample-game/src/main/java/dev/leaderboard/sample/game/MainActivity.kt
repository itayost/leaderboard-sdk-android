package dev.leaderboard.sample.game

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.leaderboard.sdk.game.LeaderboardGameSDK
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvAuthStatus: TextView
    private lateinit var tvPlayer: TextView
    private lateinit var tvLog: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegisterAccount: Button
    private lateinit var btnLogin: Button
    private lateinit var btnLogout: Button
    private lateinit var btnRegister: Button
    private lateinit var btnSubmitScore: Button
    private lateinit var btnGetScores: Button

    // TODO: Replace with your actual API key and leaderboard ID
    private val apiKey = "YOUR_API_KEY"
    private val leaderboardId = "YOUR_LEADERBOARD_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Startup check - fail fast if placeholders not replaced
        require(apiKey != "YOUR_API_KEY") {
            "Please replace YOUR_API_KEY in MainActivity.kt with your actual API key"
        }
        require(leaderboardId != "YOUR_LEADERBOARD_ID") {
            "Please replace YOUR_LEADERBOARD_ID in MainActivity.kt with your actual leaderboard ID"
        }

        // Find views
        tvStatus = findViewById(R.id.tvStatus)
        tvAuthStatus = findViewById(R.id.tvAuthStatus)
        tvPlayer = findViewById(R.id.tvPlayer)
        tvLog = findViewById(R.id.tvLog)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegisterAccount = findViewById(R.id.btnRegisterAccount)
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        btnRegister = findViewById(R.id.btnRegister)
        btnSubmitScore = findViewById(R.id.btnSubmitScore)
        btnGetScores = findViewById(R.id.btnGetScores)

        // Initialize SDK
        LeaderboardGameSDK.init(this, apiKey)
        log("SDK initialized")

        // Try auto-restore (for returning logged-in users)
        lifecycleScope.launch {
            val player = LeaderboardGameSDK.tryAutoRestore()
            if (player != null) {
                updateUI()
                log("Auto-restored: ${player.username} (logged in)")
            } else {
                // Try to restore anonymous player
                try {
                    val anonPlayer = LeaderboardGameSDK.restorePlayer()
                    updateUI()
                    log("Restored anonymous player: ${anonPlayer.username}")
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        log("No existing player found")
                    } else {
                        log("HTTP Error: ${e.code()}")
                    }
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Register account (email/password)
        btnRegisterAccount.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                log("Please enter email and password")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val username = "Player_${Random.nextInt(1000, 9999)}"
                    val authResponse = LeaderboardGameSDK.register(email, password, username)
                    updateUI()
                    log("Account registered: ${authResponse.user.email}")
                    log("Player linked: ${LeaderboardGameSDK.getCurrentPlayer()?.username}")
                } catch (e: HttpException) {
                    log("HTTP Error: ${e.code()}")
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
                    val authResponse = LeaderboardGameSDK.login(email, password)
                    updateUI()
                    log("Logged in: ${authResponse.user.email}")
                    val player = LeaderboardGameSDK.getCurrentPlayer()
                    if (player != null) {
                        log("Player restored: ${player.username}")
                    } else {
                        log("No player in this game yet - create one!")
                    }
                } catch (e: HttpException) {
                    log("HTTP Error: ${e.code()}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Logout
        btnLogout.setOnClickListener {
            LeaderboardGameSDK.logout()
            updateUI()
            log("Logged out")
        }

        // Create anonymous player
        btnRegister.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val username = "Player_${Random.nextInt(1000, 9999)}"
                    val player = LeaderboardGameSDK.registerPlayer(username)
                    updateUI()
                    log("Created player: ${player.username} (${player._id})")
                } catch (e: HttpException) {
                    log("HTTP Error: ${e.code()}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Submit score
        btnSubmitScore.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val score = Random.nextLong(100, 10000)
                    val result = LeaderboardGameSDK.submitScore(leaderboardId, score)
                    log("Score submitted: ${result.score}")
                } catch (e: HttpException) {
                    log("HTTP Error: ${e.code()}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }

        // Get top scores
        btnGetScores.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = LeaderboardGameSDK.getTopScores(leaderboardId, 10)
                    log("--- Top Scores ---")
                    response.scores.forEach { entry ->
                        log("#${entry.rank} ${entry.player.username}: ${entry.score.score}")
                    }
                } catch (e: HttpException) {
                    log("HTTP Error: ${e.code()}")
                } catch (e: IOException) {
                    log("Network error: ${e.message}")
                } catch (e: Exception) {
                    log("Error: ${e.message}")
                }
            }
        }
    }

    private fun updateUI() {
        // Update auth status
        if (LeaderboardGameSDK.isLoggedIn()) {
            val user = LeaderboardGameSDK.getCurrentUser()
            tvAuthStatus.text = "Auth: Logged in as ${user?.email ?: "user"}"
        } else {
            tvAuthStatus.text = "Auth: Not logged in"
        }

        // Update player status
        val player = LeaderboardGameSDK.getCurrentPlayer()
        if (player != null) {
            val status = if (player.isLinked) "(linked)" else "(anonymous)"
            tvPlayer.text = "Player: ${player.username} $status"
        } else {
            tvPlayer.text = "Player: Not registered"
        }
    }

    private fun log(message: String) {
        val current = tvLog.text.toString()
        tvLog.text = "$current\n$message"
    }
}
