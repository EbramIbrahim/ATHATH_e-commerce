package com.example.athath.presenntation.shopping_screens.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.athath.R
import com.example.athath.databinding.HomeFragmentBinding
import com.example.athath.presenntation.categories.base_category_screens.AccessoryFragment
import com.example.athath.presenntation.categories.base_category_screens.ChairFragment
import com.example.athath.presenntation.categories.base_category_screens.CupboardFragment
import com.example.athath.presenntation.categories.base_category_screens.FurnitureFragment
import com.example.athath.presenntation.categories.base_category_screens.TableFragment
import com.example.athath.presenntation.categories.main_category_screen.MainCategoryFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)

        binding.viewPager.isUserInputEnabled = false
        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment(),
        )

        val viewPager2Adapter = ViewPagerHomeAdapter(
            categoriesFragment, childFragmentManager, lifecycle
        )
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()

        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }


        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}