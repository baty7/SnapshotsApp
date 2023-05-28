package com.example.snapshotsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snapshotsapp.FragmentAux
import com.example.snapshotsapp.R
import com.example.snapshotsapp.databinding.ActivityMainBinding
import com.example.snapshotsapp.databinding.FragmentPerfilBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class perfilFragment : Fragment(), FragmentAux {
    private lateinit var binding: FragmentPerfilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
        cerrarSesionDialog()
    }

    private fun cerrarSesionDialog() {
        binding.btnLogOut.setOnClickListener {
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle("¿Deseas cerrar sesión?")
                    .setPositiveButton("Cerrar") { _, _ ->
                        cerrarSesion()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun cerrarSesion() {
        context?.let {
            AuthUI.getInstance().signOut(it)
                .addOnCompleteListener { task ->
                    Toast.makeText(context, "Hasta pronto", Toast.LENGTH_SHORT).show()

                    (activity?.findViewById(R.id.btn_nav) as? BottomNavigationView)?.selectedItemId =
                        R.id.inicio
                }
        }

    }
    override fun refresh() {
        with(binding) {
            tvName.text = FirebaseAuth.getInstance().currentUser?.displayName
            tvEmail.text = FirebaseAuth.getInstance().currentUser?.email
        }
    }
}
