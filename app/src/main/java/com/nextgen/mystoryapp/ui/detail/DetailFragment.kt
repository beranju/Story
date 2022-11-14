package com.nextgen.mystoryapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.story.remote.dto.DetailResponse
import com.nextgen.mystoryapp.databinding.FragmentDetailBinding
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import com.nextgen.mystoryapp.infra.utils.DateFormatter
import com.nextgen.mystoryapp.ui.common.extention.loadOriImage
import com.nextgen.mystoryapp.ui.common.extention.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString(ID_STORY)

        getDetailStory(id)
        observe()


    }

    private fun observe() {
        viewModel.mState.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: DetailState) {
        when (state) {
            is DetailState.Init -> Unit
            is DetailState.IsLoading -> handleLoading(state.isLoading)
            is DetailState.Error -> handleError(state.rawResponse)
            is DetailState.Success -> handleSucces(state.storyEntity)
        }
    }

    private fun handleSucces(storyEntity: StoryEntity) {
        binding.tvName.text = storyEntity.name
        binding.tvDescription.text = storyEntity.description
        binding.tvCreatedAt.text =
            DateFormatter.formatDate(storyEntity.createdAt.toString(), TimeZone.getDefault().id)
        binding.ivFoto.loadOriImage(storyEntity.photoUrl)
        if (storyEntity.lat == null && storyEntity.lon == null) {
            binding.goToMap.setOnClickListener {
                context?.showAlertDialog("Story ini tidak memiliki lokasi")
            }
        } else {
            binding.goToMap.setOnClickListener {
                val mBundle = Bundle()
                mBundle.putParcelable("storyEntity", storyEntity)
                findNavController().navigate(R.id.action_detailFragment_to_mapsFragment, mBundle)
            }

        }
    }

    private fun handleError(rawResponse: DetailResponse) {
        context?.showAlertDialog(rawResponse.message.toString())
    }

    private fun handleLoading(loading: Boolean) {
        binding.pbDetail.apply {
            isIndeterminate = loading
            if (!loading) {
                visibility = View.GONE
                progress = 0
            } else {
                visibility = View.VISIBLE

            }
        }
    }

    private fun getDetailStory(id: String?) {
        viewModel.getStoryById(id!!)
    }

    companion object {
        const val ID_STORY = "id_story"
    }
}