package com.android.dreamguard.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.capstone.dreamguard.databinding.ActivityHomeBinding
import com.capstone.dreamguard.R
import com.android.dreamguard.ui.prediction.ActivityPrediction
import com.android.dreamguard.ui.settings.ActivitySettings
import com.android.dreamguard.utils.CustomBottomNavigation
import android.view.LayoutInflater
import com.android.dreamguard.data.remote.models.HomePageData
import com.android.dreamguard.ui.history.PredictionHistoryActivity
import com.android.dreamguard.ui.schedule.list.SleepSchedulerActivity
import com.bumptech.glide.Glide
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setContent {
            HomeScreen()
        }

        viewModel.fetchHomePageData()
    }

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    val binding = ActivityHomeBinding.inflate(LayoutInflater.from(context))

                    // Observe ViewModel data and update binding
                    observeViewModel(binding)

                    binding.predictionHistoryButton.setOnClickListener { navigateToHistory() }
                    binding.sleepSchedulerButton.setOnClickListener { navigateToScheduler() }

                    binding.root
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CustomBottomNavigation(
                    currentTab = "Home",
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            "Home" -> {}
                            "Predict" -> navigateToPredict()
                            "Profile" -> navigateToProfile()
                        }
                    }
                )
            }
        }
    }

    private fun observeViewModel(binding: ActivityHomeBinding) {
        viewModel.homePageData.observe(this) { response ->
            response?.data?.let { data ->
                Log.d("response", data.toString())
                updateUI(binding, data)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(binding: ActivityHomeBinding, data: HomePageData) {
        // Update profile section
        data.profilePicture?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.profilePicture)
        }
        binding.greetingText.text = "Good Morning, ${data.name ?: "User"}"
        binding.dateText.text = data.todaysDate ?: getCurrentDate()

        // Update last prediction card
        data.lastPredictionCard?.let {
            binding.predictionTitle.text = "Your last prediction result is"
            binding.predictionResult.text = it.predictionResultText
            binding.lastPredictionDate.text = it.createdAt
        }

        // Update average cards
        data.avgSleepTimeCard?.let {
            binding.sleepTimeValue.text = it.avgSleepTime
            binding.sleepGoal.text = "Sleep goal: ${it.sleepGoal}"
            binding.sleepDeficit.text = it.difference
        }

        data.avgStressLevelCard?.let {
            binding.stressLevelValue.text = it.avgStressLevel.toString()
            binding.stressLevelDescription.text = it.expression
        }

        data.avgActivityLevelCard?.let {
            binding.activityLevelValue.text = it.avgActivityLevel.toString()
            binding.activityLevelDescription.text = it.expression
        }

        data.avgSleepQualityCard?.let {
            binding.sleepQualityValue.text = it.avgSleepQuality.toString()
            binding.sleepQualityDescription.text = it.expression
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }

    private fun navigateToPredict() {
        val intent = Intent(this, ActivityPrediction::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ActivitySettings::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToHistory() {
        val intent = Intent(this, PredictionHistoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToScheduler() {
        val intent = Intent(this, SleepSchedulerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}