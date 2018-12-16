package toliner.discord.siritori

import kotlinx.serialization.list
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

object SiritoriLogger {
    private val logs: MutableSet<SiritoriLog>
    private val loggedWords: MutableSet<String>
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
        loggedWords = logs.map { it.word }.toMutableSet()
        // 10000ms = 10sごとにログ保存
        timer.schedule(timerTask { save() }, config.savePeriod, config.savePeriod)
    }

    fun addLog(log: SiritoriLog) {
        logs += log
        loggedWords += log.word
    }

    fun contains(log: SiritoriLog) = loggedWords.contains(log.word)

    fun clear() = logs.clear()

    fun save() {
        logFile.writeBytes(ProtoBuf.dump(serializer, synchronized(logs) { logs.toList() }))
    }

    operator fun plusAssign(log: SiritoriLog) = addLog(log)
}