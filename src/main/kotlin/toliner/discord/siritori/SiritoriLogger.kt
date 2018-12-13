package toliner.discord.siritori

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.list
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext

object SiritoriLogger : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private val logs: MutableSet<SiritoriLog>
    private val logFile = File("siritori.log.bin")
    private val serializer = SiritoriLog.serializer().list
    private val timer = Timer()

    init {
        logs = if (logFile.exists()) {
            ProtoBuf.load(serializer, logFile.inputStream().use { it.readAllBytes() }).toMutableSet()
        } else {
            logFile.createNewFile()
            mutableSetOf()
        }
        // 10000ms = 10sごとにログ保存
        timer.schedule(timerTask {
            launch {
                logFile.outputStream().buffered().use {
                    it.write(ProtoBuf.dump(serializer, logs.toList()))
                }
            }
        }, 10_000L, 10_000L)
    }

    fun addLog(log: SiritoriLog) {
        logs += log
    }

    fun contains(log: SiritoriLog): Boolean {
        return logs.any {
            it.word == log.word
        }
    }

    fun clear() {
        logs.clear()
    }
}