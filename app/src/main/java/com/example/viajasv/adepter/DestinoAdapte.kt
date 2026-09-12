package com.example.viajasv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.viajasv.R
import com.example.viajasv.model.Destino
import java.io.File

class DestinoAdapter(
    private val destinos: MutableList<Destino>,
    private val onEditar: (Destino) -> Unit,
    private val onEliminar: (Destino) -> Unit
) : RecyclerView.Adapter<DestinoAdapter.DestinoViewHolder>() {

    class DestinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imagen: ImageView = itemView.findViewById(R.id.ivDestino)
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val pais: TextView = itemView.findViewById(R.id.tvPais)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)

        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destino, parent, false)

        return DestinoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DestinoViewHolder,
        position: Int
    ) {

        val destino = destinos[position]

        holder.nombre.text = destino.nombre
        holder.pais.text = destino.pais
        holder.precio.text = String.format("$%.2f", destino.precio)
        holder.descripcion.text = destino.descripcion

        val imagen = if (destino.imagen.startsWith("http")) {
            destino.imagen
        } else {
            File(holder.itemView.context.filesDir, destino.imagen)
        }

        Glide.with(holder.itemView.context)
            .load(imagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imagen)

        holder.btnEditar.setOnClickListener {
            onEditar(destino)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(destino)
        }
    }

    override fun getItemCount(): Int {
        return destinos.size
    }
}