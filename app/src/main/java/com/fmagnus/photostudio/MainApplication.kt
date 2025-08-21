package com.fmagnus.photostudio

import android.app.Application
import com.cloudpayments.sdk.CloudPayments

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the CloudPayments SDK
        // The user will need to replace these with their actual credentials.
        CloudPayments.setup(
            false, // Use `true` for production, `false` for sandbox/testing.
            "YOUR_TENANT_ID",
            "https://api.cloudpayments.com/", // This is the production endpoint.
            "YOUR_API_KEY"
        )
    }
}
