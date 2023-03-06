package ru.svolf.anonfiles.presentation.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.adapter.ViewPagerAdapter
import ru.svolf.anonfiles.databinding.ActivityMainBinding
import ru.svolf.anonfiles.presentation.about.AboutDialogFragment
import ru.svolf.anonfiles.presentation.info.InfoFragment
import ru.svolf.anonfiles.presentation.settings.SettingsFragment
import ru.svolf.anonfiles.presentation.upload.UploadFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var mediator: TabLayoutMediator
    private val tabLayout by lazy { binding.tabLayout }
    private val viewPager by lazy { binding.viewPager }
    private val pagerAdapter by lazy {
        ViewPagerAdapter(this).apply {
            addFragment(InfoFragment(), R.drawable.ic_download)
            addFragment(UploadFragment(), R.drawable.ic_upload)
            addFragment(SettingsFragment(), R.drawable.ic_settings)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            setSupportActionBar(it.toolbar)
        }
        viewModel.networkState.observe(this) {state ->
            when(state) {
                false -> {
                    binding.toolbar.subtitle = getString(R.string.msg_network_unavaiable)
                    binding.cardContainer.visibility = View.GONE
                }
                true -> {
                    binding.toolbar.subtitle = null
                    binding.cardContainer.visibility = View.VISIBLE
                }
            }
        }

        addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_info -> {
                        AboutDialogFragment().show(supportFragmentManager)
                        return true
                    }
                }
                return false
            }

        }, this)
        viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(pagerAdapter.getIcon(position))
        }.also {
            it.attach()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediator.isAttached)
            mediator.detach()
        _binding = null
    }

}