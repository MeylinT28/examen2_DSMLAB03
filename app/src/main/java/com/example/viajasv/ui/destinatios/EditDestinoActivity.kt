package com.example.viajasv.ui.destinations

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.viajasv.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class EditDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var ivImagen: ImageView
    private lateinit var btnImagen: Button
    private lateinit var btnActualizar: Button

    private var imagenUri: Uri? = null

    private lateinit var destinoId: String
    private var imagenActual: String = ""

    private val db = FirebaseFirestore.getInstance()

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                imagenUri = uri
                ivImagen.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_destino)

        etNombre = findViewById(R.id.etNombre)
        spinnerPais = findViewById(R.id.spinnerPais)
        etPrecio = findViewById(R.id.etPrecio)
        etDescripcion = findViewById(R.id.etDescripcion)
        ivImagen = findViewById(R.id.ivImagen)
        btnImagen = findViewById(R.id.btnImagen)
        btnActualizar = findViewById(R.id.btnActualizar)

        destinoId = intent.getStringExtra("destinoId") ?: ""
        imagenActual = intent.getStringExtra("imagen") ?: ""

        val paises = resources.getStringArray(R.array.paises)

        spinnerPais.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            paises
        )

        cargarDatos()

        btnImagen.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        btnActualizar.setOnClickListener {
            actualizarDestino()
        }
    }

    private fun cargarDatos() {

        etNombre.setText(
            intent.getStringExtra("nombre") ?: ""
        )

        etPrecio.setText(
            intent.getDoubleExtra("precio", 0.0).toString()
        )

        etDescripcion.setText(
            intent.getStringExtra("descripcion") ?: ""
        )

        val pais = intent.getStringExtra("pais") ?: ""

        val paises = resources.getStringArray(R.array.paises)

        val posicion = paises.indexOf(pais)

        if (posicion >= 0) {
            spinnerPais.setSelection(posicion)
        }

        if (
            imagenActual.isNotEmpty() &&
            !imagenActual.startsWith("http")
        ) {

            val archivo = File(filesDir, imagenActual)

            if (archivo.exists()) {
                ivImagen.setImageURI(Uri.fromFile(archivo))
            }
        }
    }

    private fun actualizarDestino() {

        val nombre = etNombre.text.toString().trim()
        val pais = spinnerPais.selectedItem.toString()
        val precioTexto = etPrecio.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = getString(R.string.ingrese_nombre)
            return
        }

        if (pais == getString(R.string.seleccionar_pais)) {
            Toast.makeText(
                this,
                getString(R.string.seleccione_pais),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (precioTexto.isEmpty()) {
            etPrecio.error = getString(R.string.ingrese_precio)
            return
        }

        val precio = precioTexto.toDoubleOrNull()

        if (precio == null || precio <= 0) {
            etPrecio.error = getString(R.string.precio_mayor_cero)
            return
        }

        if (descripcion.length < 20) {
            etDescripcion.error = getString(R.string.descripcion_minima)
            return
        }

        btnActualizar.isEnabled = false

        if (imagenUri != null) {

            try {

                val nombreArchivo =
                    "destino_${System.currentTimeMillis()}.jpg"

                val archivo = File(filesDir, nombreArchivo)

                contentResolver.openInputStream(imagenUri!!)?.use { input ->

                    archivo.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                guardarCambios(
                    nombre,
                    pais,
                    precio,
                    descripcion,
                    nombreArchivo
                )

            } catch (e: Exception) {

                btnActualizar.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.error_guardar_imagen),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            guardarCambios(
                nombre,
                pais,
                precio,
                descripcion,
                imagenActual
            )
        }
    }

    private fun guardarCambios(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String,
        imagen: String
    ) {

        val datos = hashMapOf<String, Any>(
            "nombre" to nombre,
            "pais" to pais,
            "precio" to precio,
            "descripcion" to descripcion,
            "imagen" to imagen
        )

        db.collection("destinos")
            .document(destinoId)
            .update(datos)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    getString(R.string.destino_actualizado),
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                btnActualizar.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.error_actualizar_destino),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}