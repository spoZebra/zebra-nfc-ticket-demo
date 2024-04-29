package com.spozebra.zebranfcticketdemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zebra.nfcvas.AppleVasConfig
import com.zebra.nfcvas.IZebraServiceConnection
import com.zebra.nfcvas.Pass
import com.zebra.nfcvas.SmartTapVasConfig
import com.zebra.nfcvas.ZebraNfcVas


class MainActivity : AppCompatActivity(), IZebraServiceConnection {

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

        // Init Zebra NFC Ticket SDK
        zebraNfcVas = ZebraNfcVas(this);
        zebraNfcVas.init();
    }

    private fun configreAndStartNfcVas() {
        // Apple Vas Configuration
        val appleVasConfig = AppleVasConfig()
        appleVasConfig.setPrivateKey(keyApple)
        // Google SmartTap Configuration
        val googleSmartTap = SmartTapVasConfig()
        googleSmartTap.setCollectorID("-")
        googleSmartTap.setPrivateKey(keyGoogle, keyV)

        zebraNfcVas.setVasConfigs(listOf(appleVasConfig, googleSmartTap))

        // Start reading
        zebraNfcVas.connectToReader()
        zebraNfcVas.enableReadPassMode()
    }


    override fun onServiceConnection() {
        configreAndStartNfcVas()
    }

    override fun onServiceDisconnection() {
        TODO("Not yet implemented")
    }

    override fun onPassDetected(p0: Pass?) {
        TODO("Not yet implemented")
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