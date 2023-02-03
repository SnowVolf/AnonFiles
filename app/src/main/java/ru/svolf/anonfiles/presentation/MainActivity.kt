package ru.svolf.anonfiles.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.api.AnonResponse
import ru.svolf.anonfiles.api.ApiClient
import ru.svolf.anonfiles.databinding.ActivityMainBinding
import ru.svolf.anonfiles.presentation.info.Repository

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setSupportActionBar(binding.toolbar)
        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupActionBarWithNavController(this, navController)


        Repository.getData("Eeg9s3scnf").observe(this) {
            val file = it.data?.file
            MaterialAlertDialogBuilder(this@MainActivity).setMessage(
                "File name: ${file?.metadata?.name}\nFile size: ${file?.metadata?.size?.readable}"
            ).show()
        }

    }
}