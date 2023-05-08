package com.example.snapshotsapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.snapshotsapp.MainActivity.Companion.PATH_SNAPSHOTS
import com.example.snapshotsapp.Snapshots
import com.example.snapshotsapp.databinding.ElementosSnapshotsBinding
import com.example.snapshotsapp.databinding.FragmentInicioBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth


class inicioFragment : Fragment() {

    private lateinit var binding: FragmentInicioBinding
    private lateinit var adapter: FirebaseRecyclerAdapter<Snapshots, SnapshotsViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Configurando consultas y crando instancia
        val query = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS)
        val options =
            FirebaseRecyclerOptions.Builder<Snapshots>().setQuery(query, Snapshots::class.java)
                .build()

        //Configurando adaptador
        adapter = object : FirebaseRecyclerAdapter<Snapshots, SnapshotsViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotsViewHolder {
                val binding = ElementosSnapshotsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return SnapshotsViewHolder(binding.root)

            }

            override fun onBindViewHolder(holder: SnapshotsViewHolder, position: Int, model: Snapshots
            ) {
                val snapshot = getItem(position)
                with(holder) {
                    setListener(snapshot)

                    binding.tvTitulo.text = snapshot.titulo
                    Glide.with(this@inicioFragment)
                        .load(snapshot.fotoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.ivFoto)
                }

            }

            //Sobrescribimos este metodo para ocultar el progress bar a la hora de hacer un cambio en el fragment
            override fun onDataChanged() {
                super.onDataChanged()
                binding.pbItems.visibility = View.GONE
                notifyDataSetChanged()
            }

        }
        //Configurando el recyclerview
        binding.rvItems.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.context)
        binding.rvItems.layoutManager = linearLayoutManager
        binding.rvItems.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    //ViewHolder
    inner class SnapshotsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ElementosSnapshotsBinding.bind(view)

        fun setListener(snapshot: Snapshots) {
            //Boton delete para eliminar la foto
            //binding.btnBorrar.setOnClickListener // Metodo que borra la foto de firebaseStorage{ deleteSnapshot(snapshot) }
            //Boton de me gusta en la foto
            /*binding.cbLike.setOnCheckedChangeListener { compoundButton, checked ->
                setLike(snapshot, checked)
            }*/
        }
    }

}