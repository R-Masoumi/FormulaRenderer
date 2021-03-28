package masoumi.formularenderer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import masoumi.formularenderer.databinding.ActivityMainBinding
import masoumi.formularenderer.util.MessageUtils
import masoumi.formularenderer.util.MessageUtils.BR_MESSAGE
import masoumi.formularenderer.util.MessageUtils.EXTRA_MESSAGE

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var msgReceiver: BroadcastReceiver
    private var isSnackbarShowing = false
    private var snackbarMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        setupMessageReceiver()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupMessageReceiver() {
        msgReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val msg = intent.getStringExtra(EXTRA_MESSAGE) ?: return
                val type = MessageUtils.MessageType.detachFrom(intent)
                showMessage(msg, type)
            }
        }
    }

    private fun showMessage(msg: String, type: MessageUtils.MessageType){

        if (msg != snackbarMessage || !isSnackbarShowing) {
            val snackbar = Snackbar
                .make(binding.coordinator, msg, Snackbar.LENGTH_INDEFINITE)
            val snackbarView = snackbar.view
            val tv = snackbarView
                .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            var bgColor = Color.BLACK
            var txtColor = Color.WHITE
            var duration = Snackbar.LENGTH_INDEFINITE
            when (type) {
                MessageUtils.MessageType.ERROR -> {
                    bgColor = Color.RED
                    duration = Snackbar.LENGTH_LONG
                }
                MessageUtils.MessageType.WARN -> {
                    bgColor = Color.YELLOW
                    txtColor = Color.BLACK
                    duration = Snackbar.LENGTH_SHORT
                }
                MessageUtils.MessageType.SUCCESS -> {
                    bgColor = Color.GREEN
                    txtColor = Color.BLACK
                    duration = Snackbar.LENGTH_SHORT
                }
                MessageUtils.MessageType.PLAIN -> {
                    bgColor = Color.BLACK
                    txtColor = Color.WHITE
                    duration = Snackbar.LENGTH_INDEFINITE
                }
            }
            snackbarView.setBackgroundColor(bgColor)
            tv.setTextColor(txtColor)
            snackbar.duration = duration
            snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    isSnackbarShowing = false
                }

                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    isSnackbarShowing = true
                }
            })
            snackbar.setAction(getString(R.string.lbl_dismiss)) {
            }
            snackbar.setActionTextColor(Color.GRAY)
            snackbarMessage = msg
            snackbar.show()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            msgReceiver,
            IntentFilter(BR_MESSAGE)
        )
        invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver)
    }
}