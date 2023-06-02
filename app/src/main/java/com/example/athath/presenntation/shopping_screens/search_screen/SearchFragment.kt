package com.example.athath.presenntation.shopping_screens.search_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.databinding.SearchFragmentBinding
import com.example.athath.presenntation.categories.main_category_screen.BestProductsAdapter
import com.example.athath.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment: Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()
    private val searchAdapter by lazy { BestProductsAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater)
        setUpSearchRecyclerView()
        collectSearchState()


        binding.apply {

            searchIv.setOnClickListener {
                viewModel.getProductsBySearch(binding.edSearch.text.trim().toString())
            }

            searchAdapter.onItemClick = {
                val action = SearchFragmentDirections
                    .actionSearchFragmentToProductDetailsFragment(it)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }


    private fun collectSearchState() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.searchState.collectLatest { product ->
                    when(product) {
                        is Resource.Loading -> {
                            binding.pbSearch.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.pbSearch.visibility = View.INVISIBLE
                            searchAdapter.differ.submitList(product.data)
                        }
                        is Resource.Error -> {
                            binding.pbSearch.visibility = View.INVISIBLE
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setUpSearchRecyclerView() {
        binding.rvSearch.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = searchAdapter
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}



