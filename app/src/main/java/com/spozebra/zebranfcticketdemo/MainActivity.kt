package com.spozebra.zebranfcticketdemo

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.zebra.nfcvas.VasType
import com.zebra.nfcvas.ZebraNfcVas
import java.nio.charset.StandardCharsets
import java.util.Arrays


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
        statusTextView.text = getString(R.string.vas_service_not_found)
    }

    override fun onPassDetected(pass: Pass?) {
        if (pass == null || pass.passType == null) return

        when (pass.resultCode) {
            Pass.Result.Success -> {
                val value = String(pass.payloadMessage, StandardCharsets.UTF_8)

                if(pass.passType == VasType.APPLE_VAS)
                    operationSuccess("Apple VAS: ${value}")
                else
                    operationSuccess("Smart Tap: ${value}")

            }
            Pass.Result.NotVas -> {
                // Generic NFC Tag
                readNotVas(pass)
            }
            Pass.Result.Failure,
            Pass.Result.IncorrectPrivateKey,
            Pass.Result.NoPrivateKey,
            Pass.Result.VasNotActive,
            Pass.Result.DecryptionFailure,
            Pass.Result.TagLostFailure -> {
                operationFailure(pass.resultCode.toString())
            }
        }
    }

    private fun readNotVas(pass: Pass){

        if (pass.tag != null) {
            val techList = listOf(*pass.tag.techList)

            if (techList.contains(NfcA::class.java.name)) {
                val nfcA = NfcA.get(pass.tag)
                nfcA.connect()
                val atqa = nfcA.atqa
                val sak = nfcA.sak
                nfcA.close()
                operationSuccess("NfcA ATQA: ${atqa.contentToString()}, SAK: $sak")
            }

            if (techList.contains(NfcB::class.java.name)) {
                val nfcB = NfcB.get(pass.tag)
                nfcB.connect()
                val applicationData = nfcB.applicationData
                val protocolInfo = nfcB.protocolInfo
                nfcB.close()
                operationSuccess("NfcB Application Data: ${applicationData.contentToString()}, Protocol Info: ${protocolInfo.contentToString()}")
            }

            if (techList.contains(NfcF::class.java.name)) {
                val nfcF = NfcF.get(pass.tag)
                nfcF.connect()
                val manufacturer = nfcF.manufacturer
                val systemCode = nfcF.systemCode
                nfcF.close()
                operationSuccess("NfcF Manufacturer: ${manufacturer.contentToString()}, System Code: ${systemCode.contentToString()}")
            }

            if (techList.contains(NfcV::class.java.name)) {
                val nfcV = NfcV.get(pass.tag)
                nfcV.connect()
                val dsfId = nfcV.dsfId
                val responseFlags = nfcV.responseFlags
                nfcV.close()
                operationSuccess("NfcV DSF ID: $dsfId, Response Flags: $responseFlags")
            }

            if (techList.contains(IsoDep::class.java.name)) {
                val isoDep = IsoDep.get(pass.tag)
                isoDep.connect()
                val hiLayerResponse = isoDep.hiLayerResponse
                val historicalBytes = isoDep.historicalBytes
                isoDep.close()
                operationSuccess("IsoDep HiLayer Response: ${hiLayerResponse?.contentToString()}, Historical Bytes: ${historicalBytes?.contentToString()}")
            }

            if (techList.contains(MifareClassic::class.java.name)) {
                val mifareClassic = MifareClassic.get(pass.tag)
                mifareClassic.connect()
                val sectorCount = mifareClassic.sectorCount
                val blockCount = mifareClassic.blockCount
                val size = mifareClassic.size
                mifareClassic.close()
                operationSuccess("MifareClassic Sector Count: $sectorCount, Block Count: $blockCount, Size: $size")
            }

            if (techList.contains(MifareUltralight::class.java.name)) {
                val mifareUltralight = MifareUltralight.get(pass.tag)
                mifareUltralight.connect()
                val type = mifareUltralight.type
                mifareUltralight.close()
                operationSuccess("MifareUltralight Type: $type")
            }

            if (techList.contains(Ndef::class.java.name)) {
                val ndef = Ndef.get(pass.tag)
                ndef.connect()
                val ndefMessage = ndef.cachedNdefMessage
                ndef.close()
                if (ndefMessage != null) {
                    for (record in ndefMessage.records) {
                        val payload = String(record.payload)
                        operationSuccess("NDEF Payload: $payload")
                    }
                }
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
        }, 3000)
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