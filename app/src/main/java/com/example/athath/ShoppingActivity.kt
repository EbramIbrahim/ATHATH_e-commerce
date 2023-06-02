package com.example.athath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.athath.databinding.ActivityShoppingBinding
import com.example.athath.presenntation.shopping_screens.cart_screen.CartViewModel
import com.example.athath.utils.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentShoppingContainer) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationShopping)
        bottomNavView.setupWithNavController(navController)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest { value ->
                    when (value) {
                        is Resource.Success -> {
                            val itemCount = value.data?.size ?: 0
                            bottomNavView.getOrCreateBadge(R.id.cartFragment).apply {
                                number = itemCount
                                backgroundColor = ContextCompat.getColor(
                                    this@ShoppingActivity,
                                    R.color.g_blue
                                )
                            }

                        }

                        else -> Unit
                    }
                }
            }
        }

    }


}
