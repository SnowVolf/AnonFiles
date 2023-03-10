package ru.svolf.anonfiles.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.databinding.ActivityMainBinding
import ru.svolf.anonfiles.util.ConnectionObserver
import ru.svolf.anonfiles.util._string

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()
    private val navHostController by lazy { Navigation.findNavController(this, R.id.fragment_container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            setSupportActionBar(it.toolbar)
        }
        NavigationUI.setupActionBarWithNavController(this, navHostController)
        viewModel.connectionState.onEach { state ->
            when(state) {
                ConnectionObserver.State.Available -> {
                    binding.toolbar.subtitle = null
                    binding.cardContainer.visibility = View.VISIBLE
                }
                else -> {
                    binding.toolbar.subtitle = getString(_string.msg_network_unavaiable)
                    binding.cardContainer.visibility = View.GONE
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}