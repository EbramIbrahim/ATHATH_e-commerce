package com.example.athath.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.athath.R
import com.example.athath.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showBottomNavigation() {
    val bottomNavigation =
        (activity as ShoppingActivity)
            .findViewById<BottomNavigationView>(R.id.bottomNavigationShopping)

    bottomNavigation.visibility = View.VISIBLE
}

fun Fragment.hideBottomNavigation() {
    val bottomNavigation =
        (activity as ShoppingActivity)
            .findViewById<BottomNavigationView>(R.id.bottomNavigationShopping)

    bottomNavigation.visibility = View.GONE
}