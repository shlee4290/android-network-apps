package com.example.presentation.conferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.Data
import com.example.domain.usecase.GetConferencesUseCase
import com.example.presentation.LoadingPopUp
import com.example.presentation.R
import com.example.presentation.conferencedetail.ConferenceDetailFragment
import com.example.presentation.conferences.adapter.ConferenceListAdapter
import com.example.presentation.databinding.FragmentConferencesBinding
import kotlinx.coroutines.launch

class ConferencesFragment : Fragment() {

    private val binding: FragmentConferencesBinding by lazy {
        FragmentConferencesBinding.inflate(layoutInflater)
    }

    private val adapter: ConferenceListAdapter by lazy {
        ConferenceListAdapter {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ConferenceDetailFragment.newInstance(it))
                .addToBackStack(null)
                .commit()
        }
    }

    private val viewModel: ConferencesViewModel by lazy {
        ViewModelProvider(
            this,
            ConferencesViewModelFactory(GetConferencesUseCase(Data.conferenceRepository))
        )[ConferencesViewModel::class.java]
    }

    private val loadingPopUp: LoadingPopUp by lazy {
        LoadingPopUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.conferencesRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.conferenceUiState.observe(viewLifecycleOwner) {
            if (it.isLoading) {
                loadingPopUp.show(parentFragmentManager, "loading_popup")
            } else {
                loadingPopUp.dismiss()
                adapter.submitList(it.conferenceList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadConferencesErrorEvent
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                ).collect {
                    Toast.makeText(requireContext(), "컨퍼런스 정보를 받아올 수 없습니다.", Toast.LENGTH_LONG)
                        .show()
                    requireActivity().finish()
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ConferencesFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}