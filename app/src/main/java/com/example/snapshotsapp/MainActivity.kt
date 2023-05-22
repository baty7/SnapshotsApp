package com.example.snapshotsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.snapshotsapp.databinding.ActivityMainBinding
import com.example.snapshotsapp.fragments.inicioFragment
import com.example.snapshotsapp.fragments.perfilFragment
import com.example.snapshotsapp.fragments.subirImagenFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var activeFragment: Fragment
    private var auth: FirebaseAuth? = null
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    companion object {
        const val PATH_SNAPSHOTS = "snapshots"
    }

    private val authResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Bienvenido a Snapshots", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configuracionFireBaseAuth()
        configuracionNavigationBar()


    }

    private val proveedores = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    private fun configuracionFireBaseAuth() {
        auth = FirebaseAuth.getInstance()
        authListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                authResult.launch(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(proveedores)
                        .setTheme(R.style.Theme_SnapshotsApp_)
                        .build()
                )

            }
        }
    }


    private fun configuracionNavigationBar() {
        fragmentManager = supportFragmentManager

        val inicioFragment = inicioFragment()
        val perfilFragment = perfilFragment()
        val subirImagenFragment = subirImagenFragment()

        activeFragment = inicioFragment

        fragmentManager.beginTransaction()
            .add(R.id.FragmentPrincipal, perfilFragment, perfilFragment::class.java.name)
            .hide(perfilFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.FragmentPrincipal, subirImagenFragment, subirImagenFragment::class.java.name)
            .hide(subirImagenFragment).commit()

        fragmentManager.beginTransaction()
            .add(R.id.FragmentPrincipal, inicioFragment, inicioFragment::class.java.name)
            .commit()

        binding.btnNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.inicio -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(inicioFragment)
                        .commit()
                    activeFragment = inicioFragment
                    true
                }
                R.id.subirImagen -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(subirImagenFragment)
                        .commit()
                    activeFragment = subirImagenFragment
                    true
                }
                R.id.perfilUsuario -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(perfilFragment)
                        .commit()
                    activeFragment = perfilFragment
                    true
                }
                else -> false
            }
        }
        binding.btnNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.inicio -> (inicioFragment as InicioAux).goToTop()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        auth?.addAuthStateListener(authListener)
    }

    override fun onPause() {
        super.onPause()
        auth?.removeAuthStateListener(authListener)
    }

    /* override fun onStart() {
         super.onStart()
         auth.addAuthStateListener(authListener)
     }*/


}