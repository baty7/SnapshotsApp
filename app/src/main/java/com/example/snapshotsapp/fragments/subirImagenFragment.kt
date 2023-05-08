package com.example.snapshotsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapshotsapp.R
import com.example.snapshotsapp.databinding.FragmentSubirImagenBinding


class subirImagenFragment : Fragment() {
    private lateinit var binding: FragmentSubirImagenBinding
    val PICK_IMAGE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSubirImagenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibSelect.setOnClickListener { abrirGaleria() }

    }
    // TODO: implementar resultado de la galeria
    private var resuladoGaleria = registerForActivityResult()


    private fun abrirGaleria() {
        // TODO: falta establecer que solo se puede seleccionar imagenes de la galeria con el media en el intent
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resuladoGaleria.launch(intent)
    }

}