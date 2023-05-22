package com.example.snapshotsapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import com.example.snapshotsapp.MainActivity.Companion.PATH_SNAPSHOTS
import com.example.snapshotsapp.Snapshots
import com.example.snapshotsapp.databinding.FragmentSubirImagenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class subirImagenFragment : Fragment() {
    private lateinit var binding: FragmentSubirImagenBinding
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private var imagen: Uri? = null

    private var resultadoGaleria =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imagen = result.data?.data
                binding.ivPhoto.setImageURI(imagen)
                binding.tilTitle.visibility = View.VISIBLE
                binding.tvMessage.text = "Por favor añada un titulo"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubirImagenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibSelect.setOnClickListener { abrirGaleria() }
        binding.btnPost.setOnClickListener { publicarFoto() }
        configuracionFirebase()

    }

    private fun configuracionFirebase() {
        storageReference = FirebaseStorage.getInstance().reference.child(PATH_SNAPSHOTS)
        databaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS)
    }

    @SuppressLint("IntentReset")
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        resultadoGaleria.launch(intent)

    }

    @SuppressLint("SetTextI18n")
    private fun publicarFoto() {
        binding.progressBar.visibility = View.VISIBLE
        val key = databaseReference.push().key!!
        val storageReference = storageReference
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(key)
        if (imagen != null) {
            storageReference.putFile(imagen!!)
                .addOnProgressListener {
                    val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                    binding.progressBar.progress = progress.toInt()
                    binding.tvMessage.text = "$progress%"
                }
                .addOnCompleteListener {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                .addOnSuccessListener {
                    ocultarTeclado()
                    Snackbar.make(
                        binding.root, "Foto publicada correctamente", Snackbar.LENGTH_LONG
                    ).show()
                    storageReference.downloadUrl.addOnSuccessListener {
                        guardarFotoBaseDatos(key, it.toString(), binding.etTitle.text.toString().trim())
                    }
                }
                .addOnFailureListener {
                    Snackbar.make(
                        binding.root, "Error al publicar la foto", Snackbar.LENGTH_LONG
                    ).show()
                }

        }
    }

    private fun ocultarTeclado() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun guardarFotoBaseDatos(key: String, url: String, titulo: String) {
        val foto = Snapshots(titulo = titulo,  fotoUrl= url)
        databaseReference.child(key).setValue(foto)

    }

}


