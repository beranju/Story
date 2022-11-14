package com.nextgen.mystoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.databinding.FragmentHomeBinding
import com.nextgen.mystoryapp.infra.utils.SharedPrefs
import com.nextgen.mystoryapp.ui.adapter.ListStoriesAdapter
import com.nextgen.mystoryapp.ui.adapter.LoadingStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject

@WithFragmentBindings
@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var prefs: SharedPrefs

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel: HomeViewModel by viewModels()
    private val mAdapter = ListStoriesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.toolbar?.title = getString(R.string.app_name)
        _binding?.toolbar?.inflateMenu(R.menu.item_menu)
        _binding?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    prefs.clear()
                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    true
                }
                R.id.language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true

                }
                else -> false
            }
        }

        if (prefs.getToken().isEmpty()) {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }




        getStories()
        observe()
        _binding?.btnAddStory?.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }

        _binding?.rvItem?.layoutManager = LinearLayoutManager(this.context)
        _binding?.rvItem?.adapter = mAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                mAdapter.retry()
            }
        )
        _binding?.btnToMap?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
        }
    }

    private fun observe() {
        viewModel.story.observe(viewLifecycleOwner) {
            mAdapter.submitData(lifecycle, it)
        }
    }

    private fun getStories() {
        viewModel.getStories()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}