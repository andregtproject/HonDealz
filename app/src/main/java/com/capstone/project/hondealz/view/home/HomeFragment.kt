package com.capstone.project.hondealz.view.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.FragmentHomeBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.adapter.HistoryAdapter
import com.capstone.project.hondealz.view.login.LoginActivity
import com.capstone.project.hondealz.view.main.MainActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHistoryRecyclerView()

        homeViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel.isLogin) {
                homeViewModel.fetchUserData()
                homeViewModel.fetchRecentHistories()
            }
        }

        observeUserData()
        observeRecentHistories()
        setupActions()
        playAnimation()
    }

    private fun playAnimation() {
        val ivProfile = ObjectAnimator.ofFloat(binding.ivProfile, View.ALPHA, 1f).setDuration(100)
        val tvTagline =
            ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 1f).setDuration(100)
        val tvSubTagline =
            ObjectAnimator.ofFloat(binding.tvSubtagline, View.ALPHA, 1f).setDuration(100)
        val cvEstimasiInstan =
            ObjectAnimator.ofFloat(binding.cvEstimasiInstan, View.ALPHA, 1f).setDuration(100)
        val layoutHistory =
            ObjectAnimator.ofFloat(binding.layoutHistory, View.ALPHA, 1f).setDuration(100)
        val tvEmptyState =
            ObjectAnimator.ofFloat(binding.tvEmptyState, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                ivProfile,
                tvTagline,
                tvSubTagline,
                cvEstimasiInstan,
                layoutHistory,
                tvEmptyState
            )
            startDelay = 100
        }.start()
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun observeUserData() {
        homeViewModel.userData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvGreeting.visibility = View.GONE
                }

                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE
                    val username = result.data.user?.username
                    binding.tvGreeting.text = getString(R.string.greetings, username)
                }

                is ResultState.Error -> {
                    Log.e(
                        "HomeFragment",
                        "Error: ${result.error}, Status Code: ${result.statusCode}"
                    )

                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE

                    if (result.statusCode == 401 || result.statusCode == 403) {
                        showTokenExpiredDialog()
                    }
                }
            }
        }
    }

    private fun observeRecentHistories() {
        homeViewModel.recentHistories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.rvHistory.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.GONE
                }

                is ResultState.Success -> {
                    val histories = result.data.histories
                    if (histories.isNullOrEmpty()) {
                        binding.rvHistory.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvHistory.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                        historyAdapter.submitList(histories.filterNotNull())
                    }
                }

                is ResultState.Error -> {
                    binding.rvHistory.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupActions() {
        binding.estimasiButton.setOnClickListener {
            (activity as? MainActivity)?.navigateToScanFragment()
        }

        binding.tvSeeAll.setOnClickListener {
            (activity as? MainActivity)?.navigateToHistoryFragment()
        }

        binding.ivProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToProfileFragment()
        }
    }

    private fun showTokenExpiredDialog() {
        activity?.runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.session_expired))
                .setMessage(getString(R.string.your_session_has_expired_please_login_again))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    homeViewModel.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setCancelable(false)
                .show()
        }
    }
}