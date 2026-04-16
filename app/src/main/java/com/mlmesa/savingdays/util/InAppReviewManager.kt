package com.mlmesa.savingdays.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Manager for handling Google Play In-App Reviews
 */
class InAppReviewManager(private val context: Context) {

    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    /**
     * Launch the in-app review flow
     * @param activity The activity to launch the review flow from
     * @param onComplete Called when the review flow is complete (success or failure)
     */
    fun requestReviewFlow(activity: Activity, onComplete: (Boolean) -> Unit = {}) {
        Log.d(TAG, "Requesting review flow...")
        val request = reviewManager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Review flow request successful")
                val reviewInfo: ReviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)

                flow.addOnCompleteListener {
                    Log.d(TAG, "Review flow completed")
                    onComplete(true)
                }
            } else {
                Log.e(TAG, "Review flow request failed: ${task.exception?.message}", task.exception)
                onComplete(false)
            }
        }
    }

    /**
     * Check if we should attempt to show review based on completed days and previous attempts
     * @param completedDays Number of days user has completed
     * @param attempts Number of previous review attempts
     * @return true if we should attempt review, false otherwise
     */
    fun shouldAttemptReview(completedDays: Int, attempts: Int): Boolean {
        // Max 4 attempts
        if (attempts >= MAX_ATTEMPTS) {
            Log.d(TAG, "Should attempt review: NO - max attempts reached ($attempts)")
            return false
        }

        // Check if we're at a review threshold
        val threshold = getReviewThreshold(attempts)
        val shouldAttempt = completedDays >= threshold

        Log.d(TAG, "Should attempt review: $shouldAttempt (days=$completedDays, attempts=$attempts, threshold=$threshold)")
        return shouldAttempt
    }

    /**
     * Get the next review threshold based on number of attempts
     * Attempt 0: 5 days
     * Attempt 1: 10 days
     * Attempt 2: 30 days
     * Attempt 3: 50 days
     */
    private fun getReviewThreshold(attempts: Int): Int {
        return when (attempts) {
            0 -> REVIEW_THRESHOLD_1
            1 -> REVIEW_THRESHOLD_2
            2 -> REVIEW_THRESHOLD_3
            3 -> REVIEW_THRESHOLD_4
            else -> Int.MAX_VALUE // No more attempts
        }
    }

    /**
     * Get the next review threshold in a human-readable format
     */
    fun getNextThresholdInfo(attempts: Int): String {
        val nextThreshold = getReviewThreshold(attempts)
        return "Attempt ${attempts + 1}/$MAX_ATTEMPTS at $nextThreshold days completed"
    }

    companion object {
        // Review attempt thresholds (in completed days)
        const val REVIEW_THRESHOLD_1 = 5   // 1st attempt
        const val REVIEW_THRESHOLD_2 = 10  // 2nd attempt
        const val REVIEW_THRESHOLD_3 = 30  // 3rd attempt
        const val REVIEW_THRESHOLD_4 = 50  // 4th attempt (final)
        const val MAX_ATTEMPTS = 4
        
        private const val TAG = "InAppReviewManager"
    }
}
