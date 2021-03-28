package masoumi.formularenderer.util

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

object MessageUtils {
    enum class MessageType {
        PLAIN, ERROR, WARN, SUCCESS;
        fun attachTo(intent: Intent) {
            intent.putExtra(MessageType::class.simpleName, ordinal)
        }

        companion object {
            fun detachFrom(intent: Intent): MessageType {
                if (!intent.hasExtra(MessageType::class.simpleName)) return PLAIN
                val type = values()[intent.getIntExtra(MessageType::class.simpleName, -1)]
                intent.removeExtra(MessageType::class.simpleName)
                return type
            }
        }
    }

    const val BR_MESSAGE = "local.message"
    const val EXTRA_MESSAGE = "msg"

    fun sendMessageBroadcast(context: Context, message: String, type: MessageType) {
        val intent = Intent(BR_MESSAGE)
        intent.putExtra(EXTRA_MESSAGE, message)
        type.attachTo(intent)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}