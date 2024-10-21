package com.tifd.projectcomposed

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class AuthActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthScreen(auth, loginOnClick = { email, password -> loginWithEmail(email, password) })

                }
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "Login Success, navigating to List Activity")
                    navigateToListActivity()
                } else {
                    Log.e("MainActivity", "Login failed: ${task.exception?.message}")
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToListActivity() {
        try {
            Log.d("AuthActivity", "Navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("AuthActivity", "Navigasi ke MainActivity gagal: ${e.message}")
        }
    }

}


@Composable
fun AuthScreen(auth: FirebaseAuth, loginOnClick: (String, String) -> Unit) {

    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    val isButtonEnabled = remember(emailText, passwordText) {
        emailText.isNotBlank() && passwordText.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Login title text
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF673AB7), // Ungu untuk judul
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email TextField
        TextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Sesuaikan lebar TextField dengan gambar
                .border(1.dp, Color.Gray), // Border abu-abu seperti di gambar
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = "Person Icon")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        TextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Sesuaikan lebar TextField dengan gambar
                .border(1.dp, Color.Gray), // Border abu-abu seperti di gambar
            leadingIcon = {
                Icon(Icons.Outlined.Info, contentDescription = "Password Icon")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { loginOnClick(emailText, passwordText) },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ukuran button lebih kecil
                .height(48.dp), // Tinggi button yang lebih besar sesuai gambar
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F51B5), // Warna button biru tua
                contentColor = Color.White // Teks berwarna putih
            ),
            shape = MaterialTheme.shapes.medium // Bentuk button bulat
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
