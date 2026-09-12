package com.example.viajasv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.viajasv.ui.auth.RegisterActivity
import com.example.viajasv.ui.home.HomeActivity
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        btnIniciarSesion.setOnClickListener {
            iniciarSesion()
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion() {

        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString()

        if (correo.isEmpty()) {
            etCorreo.error = getString(R.string.ingrese_correo)
            etCorreo.requestFocus()
            return
        }

        if (contrasena.isEmpty()) {
            etContrasena.error = getString(R.string.ingrese_contrasena)
            etContrasena.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        getString(R.string.inicio_sesion_exitoso),
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        getString(
                            R.string.error_iniciar_sesion,
                            task.exception?.message ?: ""
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}