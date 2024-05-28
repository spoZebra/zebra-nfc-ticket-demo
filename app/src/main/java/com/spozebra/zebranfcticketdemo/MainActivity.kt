package com.spozebra.zebranfcticketdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zebra.nfcvas.AppleVasConfig
import com.zebra.nfcvas.IZebraServiceConnection
import com.zebra.nfcvas.Pass
import com.zebra.nfcvas.SmartTapVasConfig
import com.zebra.nfcvas.ZebraNfcVas
import java.nio.charset.StandardCharsets





class MainActivity : AppCompatActivity(), IZebraServiceConnection {

    private lateinit var confirmationImageView: ImageView
    private lateinit var errorImageView: ImageView
    private lateinit var statusTextView: TextView
    private lateinit var zebraNfcVas: ZebraNfcVas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        confirmationImageView = findViewById(R.id.confirmation_icon)
        errorImageView = findViewById(R.id.error_icon)
        statusTextView = findViewById(R.id.status_message)

        // Init Zebra NFC Ticket SDK
        zebraNfcVas = ZebraNfcVas(this);
        zebraNfcVas.init();
    }

    private fun configreAndStartNfcVas() {
        // --- BEGIN Apple Vas Configuration ---
        val appleVasConfig = AppleVasConfig()

        // Private key to be used in order to decrypt the pass
        // Add the raw content of your private key in the local.properties file as follow:
        // APPLEVAS_PRIVATE_KEY="-----BEGIN EC PRIVATE KEY----- etc."
        // NOTE: Do not include BEGIN EC PARAMETERS section

        appleVasConfig.setPrivateKey(BuildConfig.APPLEVAS_PRIVATE_KEY)
        // The pass type id to be read.
        // This field is mandatory
        appleVasConfig.passTypeIds = arrayOf("pass.com.gowento.store-card")

        // --- END Apple Vas Configuration ---

        // --- BEGIN Google SmartTap Configuration ---
        val googleSmartTap = SmartTapVasConfig()

        // Add the raw content of your private key, its version and CollectorID in the local.properties file as follow:
        // GOOGLESMARTAPP_PRIVATE_KEY="-----BEGIN EC PRIVATE KEY----- etc."
        // GOOGLESMARTAPP_KEY_VERSION=1
        // GOOGLESMARTAPP_COLLECTOR_ID=XXXXXXXX
        googleSmartTap.setPrivateKey(BuildConfig.GOOGLESMARTAPP_PRIVATE_KEY, BuildConfig.GOOGLESMARTAPP_KEY_VERSION.toInt())
        googleSmartTap.setCollectorID(BuildConfig.GOOGLESMARTAPP_COLLECTOR_ID)

        // --- END Google SmartTap Configuration ---

        zebraNfcVas.createZebraNfcVasConfig()
        zebraNfcVas.setVasConfigs(listOf(appleVasConfig, googleSmartTap))

        // Start reading
        zebraNfcVas.connectToReader()
        zebraNfcVas.enableReadPassMode()
    }


    override fun onServiceConnection() {
        configreAndStartNfcVas()
        statusTextView.text = getString(R.string.ready_read_nfc_pass)
    }

    override fun onServiceDisconnection() {
        TODO("Not yet implemented")
    }

    override fun onPassDetected(pass: Pass?) {
        if (pass == null || pass.passType == null) return

        when (pass.resultCode) {
            Pass.Result.Success -> {
                val value = String(pass.payloadMessage, StandardCharsets.UTF_8)
                operationSuccess(value)
            }
            Pass.Result.Failure,
            Pass.Result.NotVas,
            Pass.Result.IncorrectPrivateKey,
            Pass.Result.NoPrivateKey,
            Pass.Result.VasNotActive,
            Pass.Result.DecryptionFailure,
            Pass.Result.TagLostFailure -> {
                operationFailure(pass.resultCode.toString())
            }
        }
    }

    private fun operationSuccess(message: String) {
        showAndHideAfterDelay(confirmationImageView, message)
    }

    private fun operationFailure(message: String) {
        showAndHideAfterDelay(errorImageView, message)
    }

    private fun showAndHideAfterDelay(icon: ImageView, message: String) {
        runOnUiThread {
            icon.visibility = View.VISIBLE
            statusTextView.text = message

            // Load and start the animation
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            icon.startAnimation(scaleAnimation)
        }
        // Use a handler to delay the disappearance
        Handler(Looper.getMainLooper()).postDelayed({
            val scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down)
            scaleDownAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation) {
                    icon.visibility = View.GONE
                    statusTextView.text = getString(R.string.ready_read_nfc_pass)
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            })
            icon.startAnimation(scaleDownAnimation)
        }, 5000)
    }

    override fun onResume() {
        super.onResume()
        zebraNfcVas.enableReadPassMode()
    }

    override fun onPause() {
        super.onPause()
        zebraNfcVas.disableReadPassMode()
    }

    override fun onStop() {
        super.onStop()
        zebraNfcVas.disableReadPassMode()
    }
}