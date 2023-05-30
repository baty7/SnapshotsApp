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
import com.example.snapshotsapp.InicioAux
import com.example.snapshotsapp.MainActivity.Companion.PATH_SNAPSHOTS
import com.example.snapshotsapp.R
import com.example.snapshotsapp.Snapshots
import com.example.snapshotsapp.databinding.ElementosSnapshotsBinding
import com.example.snapshotsapp.databinding.FragmentInicioBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class inicioFragment : Fragment(), InicioAux {

    private lateinit var binding: FragmentInicioBinding
    private lateinit var firebaseAdapter: FirebaseRecyclerAdapter<Snapshots, SnapshotsViewHolder>
    private lateinit var recyclerLayout: RecyclerView.LayoutManager


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
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //val query = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS)
        val query =
            FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS).child(currentUser)
        val options =
            FirebaseRecyclerOptions.Builder<Snapshots>().setQuery(query) {
                val snapshot = it.getValue(Snapshots::class.java)
                snapshot!!.id = it.key!!
                snapshot
            }
                .build()

        //Configurando adaptador
        firebaseAdapter =
            object : FirebaseRecyclerAdapter<Snapshots, SnapshotsViewHolder>(options) {
                private lateinit var contexto: Context

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): SnapshotsViewHolder {
                    contexto = parent.context
                    val view = LayoutInflater.from(contexto)
                        .inflate(R.layout.elementos_snapshots, parent, false)
                    return SnapshotsViewHolder(view)

                }

                override fun onBindViewHolder(
                    holder: SnapshotsViewHolder, position: Int, model: Snapshots
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
        recyclerLayout = LinearLayoutManager(context)
        binding.rvItems.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayout
            adapter = firebaseAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        firebaseAdapter.stopListening()
    }

    //ViewHolder

    private fun borrarFoto(snapshot: Snapshots) {
        confirmacionBorrar(snapshot) {
            //Borramos de la base de datos
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = FirebaseDatabase.getInstance().reference
                .child("snapshots/")
                .child(currentUser)
            //Borramos del Storage
            val storageReference =
                FirebaseStorage.getInstance().reference
                    .child("snapshots/")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(snapshot.id)
            storageReference.delete().addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    databaseReference.child(snapshot.id).removeValue()
                } else {
                    Snackbar.make(
                        binding.root, "Error,No se pudo eliminar la foto", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun confirmacionBorrar(snapshot: Snapshots, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Desea eliminar esta foto?")
            .setPositiveButton("Aceptar") { _, _ ->
                onConfirm()

            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    inner class SnapshotsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ElementosSnapshotsBinding.bind(view)
        fun setListener(snapshot: Snapshots) {
            //Boton delete para eliminar la foto
            binding.btnBorrar.setOnClickListener { borrarFoto(snapshot) }
        }
    }

    override fun goToTop() {
        binding.rvItems.smoothScrollToPosition(0)
    }


}