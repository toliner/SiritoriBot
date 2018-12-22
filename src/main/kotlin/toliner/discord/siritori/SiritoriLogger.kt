package toliner.discord.siritori

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

@Serializable
object SiritoriLogger {
    @SerialId(1)
    private val logs: MutableSet<SiritoriLog>
    private val loggedWords: MutableSet<String>
    private val logFile = File("siritori.log.bin")
    private val serializer = SiritoriLogger.`$serializer`
    private val timer = Timer()

    init {
        logs = if (logFile.exists()) {
            ProtoBuf.load(serializer, logFile.inputStream().use { it.readAllBytes() }).logs
        } else {
            logFile.createNewFile()
            mutableSetOf()
        }
        loggedWords = logs.map { it.word }.toMutableSet()
        // 10000ms = 10sごとにログ保存
        timer.schedule(timerTask { save() }, config.savePeriod, config.savePeriod)
    }

    fun addLog(log: SiritoriLog) {
        logger.info("Word Accepted: ${log.word}(${log.yomi}) by ${jda.getUserById(log.owner)}")
        logs += log
        loggedWords += log.word
    }

    fun contains(word: String) = loggedWords.contains(word)

    fun clear() = logs.clear()

    fun save() {
        synchronized(this) {
            logFile.writeBytes(ProtoBuf.dump(serializer, this))
            logger.info("Word log save succeed.")
        }
    }

    fun getLast(): SiritoriLog? = logs.lastOrNull()

    operator fun plusAssign(log: SiritoriLog) = addLog(log)
}