package toliner.discord.siritori

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

object SiritoriLogger {
    private val logs: MutableList<SiritoriLog>
    private val loggedWords: MutableSet<String>
    private val logFile = File("siritori.log.bin")
    private val serializer = SiritoriLogWrapper.serializer()
    private val timer = Timer()

    init {
        logs = if (logFile.exists()) {
            ProtoBuf.load(serializer, logFile.inputStream().use { it.readBytes() }).logs.toMutableList()
        } else {
            logFile.createNewFile()
            mutableListOf()
        }
        loggedWords = logs.map { it.word }.toMutableSet()
        timer.schedule(timerTask { save() }, config.savePeriod, config.savePeriod)
    }

    fun addLog(log: SiritoriLog) {
        logger.info("Word Accepted: ${log.word}(${log.yomi}) by ${jda.getUserById(log.owner)}")
        logs += log
        loggedWords += log.word
    }

    fun contains(word: String) = loggedWords.contains(word)

    fun clear() {
        logs.clear()
        loggedWords.clear()
    }

    fun save() {
        synchronized(this) {
            logFile.writeBytes(ProtoBuf.dump(serializer, SiritoriLogWrapper(logs.toList())))
            logger.info("Word log save succeed.")
        }
    }

    fun getLast(): SiritoriLog? = logs.lastOrNull()

    fun getLastYomi(): String? = getLast()?.yomi?.filterNot { it == 'ー' }

    operator fun plusAssign(log: SiritoriLog) = addLog(log)

    @Serializable
    data class SiritoriLogWrapper(@SerialId(1) val logs: List<SiritoriLog>)
}