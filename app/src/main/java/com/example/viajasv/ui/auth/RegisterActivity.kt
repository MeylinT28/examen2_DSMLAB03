package com.example.viajasv.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.viajasv.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etConfirmarContrasena: EditText
    private lateinit var btnCrearCuenta: Button
    private lateinit var btnVolverLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etRegistroCorreo)
        etContrasena = findViewById(R.id.etRegistroContrasena)
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)

        btnCrearCuenta.setOnClickListener {
            registrarUsuario()
        }

        btnVolverLogin.setOnClickListener {
            finish()
        }
    }

    private fun registrarUsuario() {

        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString()
        val confirmarContrasena = etConfirmarContrasena.text.toString()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese su nombre"
            etNombre.requestFocus()
            return
        }

        if (correo.isEmpty()) {
            etCorreo.error = "Ingrese su correo"
            etCorreo.requestFocus()
            return
        }

        if (contrasena.isEmpty()) {
            etContrasena.error = "Ingrese una contraseña"
            etContrasena.requestFocus()
            return
        }

        if (contrasena.length < 6) {
            etContrasena.error = "La contraseña debe tener al menos 6 caracteres"
            etContrasena.requestFocus()
            return
        }

        if (confirmarContrasena.isEmpty()) {
            etConfirmarContrasena.error = "Confirme su contraseña"
            etConfirmarContrasena.requestFocus()
            return
        }

        if (contrasena != confirmarContrasena) {
            etConfirmarContrasena.error = "Las contraseñas no coinciden"
            etConfirmarContrasena.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val perfil = userProfileChangeRequest {
                        displayName = nombre
                    }

                    auth.currentUser?.updateProfile(perfil)

                    Toast.makeText(
                        this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "No se pudo crear la cuenta: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}