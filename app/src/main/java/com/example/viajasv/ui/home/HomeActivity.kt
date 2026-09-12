package com.example.viajasv.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viajasv.R
import com.example.viajasv.adapter.DestinoAdapter
import com.example.viajasv.model.Destino
import com.example.viajasv.ui.destinations.AddDestinoActivity
import com.example.viajasv.ui.destinations.EditDestinoActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DestinoAdapter

    private val destinos = mutableListOf<Destino>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvDestinos)

        adapter = DestinoAdapter(
            destinos,
            onEditar = { destino ->
                editarDestino(destino)
            },
            onEliminar = { destino ->
                confirmarEliminacion(destino)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val btnAgregarDestino =
            findViewById<Button>(R.id.btnAgregarDestino)

        btnAgregarDestino.setOnClickListener {
            startActivity(
                Intent(this, AddDestinoActivity::class.java)
            )
        }

        cargarDestinos()
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            cargarDestinos()
        }
    }

    private fun cargarDestinos() {

        db.collection("destinos")
            .get()
            .addOnSuccessListener { documentos ->

                destinos.clear()

                for (documento in documentos) {

                    val destino =
                        documento.toObject(Destino::class.java)

                    destino.id = documento.id

                    destinos.add(destino)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    getString(
                        R.string.error_cargar_destinos,
                        error.message ?: ""
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun editarDestino(destino: Destino) {

        val intent = Intent(
            this,
            EditDestinoActivity::class.java
        )

        intent.putExtra("destinoId", destino.id)
        intent.putExtra("nombre", destino.nombre)
        intent.putExtra("pais", destino.pais)
        intent.putExtra("precio", destino.precio)
        intent.putExtra("descripcion", destino.descripcion)
        intent.putExtra("imagen", destino.imagen)

        startActivity(intent)
    }

    private fun confirmarEliminacion(destino: Destino) {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar))
            .setMessage(
                getString(
                    R.string.confirmar_eliminacion,
                    destino.nombre
                )
            )
            .setNegativeButton(
                getString(R.string.cancelar),
                null
            )
            .setPositiveButton(
                getString(R.string.eliminar)
            ) { _, _ ->
                eliminarDestino(destino)
            }
            .show()
    }

    private fun eliminarDestino(destino: Destino) {

        db.collection("destinos")
            .document(destino.id)
            .delete()
            .addOnSuccessListener {

                if (!destino.imagen.startsWith("http")) {

                    val archivo =
                        File(filesDir, destino.imagen)

                    if (archivo.exists()) {
                        archivo.delete()
                    }
                }

                Toast.makeText(
                    this,
                    getString(R.string.destino_eliminado),
                    Toast.LENGTH_SHORT
                ).show()

                cargarDestinos()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    getString(R.string.error_eliminar_destino),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}