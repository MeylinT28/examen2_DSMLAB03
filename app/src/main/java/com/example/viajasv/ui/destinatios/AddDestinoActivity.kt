package com.example.viajasv.ui.destinations

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.viajasv.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class AddDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var ivImagen: ImageView
    private lateinit var btnImagen: Button
    private lateinit var btnGuardar: Button

    private var imagenUri: Uri? = null

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
        setContentView(R.layout.activity_add_destino)

        etNombre = findViewById(R.id.etNombre)
        spinnerPais = findViewById(R.id.spinnerPais)
        etPrecio = findViewById(R.id.etPrecio)
        etDescripcion = findViewById(R.id.etDescripcion)
        ivImagen = findViewById(R.id.ivImagen)
        btnImagen = findViewById(R.id.btnImagen)
        btnGuardar = findViewById(R.id.btnGuardar)

        val paises = resources.getStringArray(R.array.paises)

        spinnerPais.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            paises
        )

        btnImagen.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            guardarDestino()
        }
    }

    private fun guardarDestino() {

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

        if (imagenUri == null) {
            Toast.makeText(
                this,
                getString(R.string.debe_seleccionar_imagen),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        btnGuardar.isEnabled = false

        guardarImagenLocal(
            imagenUri!!,
            nombre,
            pais,
            precio,
            descripcion
        )
    }

    private fun guardarImagenLocal(
        uri: Uri,
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        try {

            val nombreArchivo =
                "destino_${System.currentTimeMillis()}.jpg"

            val archivo = File(filesDir, nombreArchivo)

            contentResolver.openInputStream(uri)?.use { input ->

                archivo.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val destino = hashMapOf(
                "nombre" to nombre,
                "pais" to pais,
                "precio" to precio,
                "descripcion" to descripcion,
                "imagen" to nombreArchivo
            )

            db.collection("destinos")
                .add(destino)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        getString(R.string.destino_guardado),
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener {

                    btnGuardar.isEnabled = true

                    Toast.makeText(
                        this,
                        getString(R.string.error_guardar_destino),
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } catch (e: Exception) {

            btnGuardar.isEnabled = true

            Toast.makeText(
                this,
                getString(R.string.error_guardar_imagen),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}