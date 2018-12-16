package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result

class SiritoriIllegalWordException(message: String): RuntimeException(message)

interface ISiritoriCheckerPlugin {
    val name: String
    fun check(word: String): Result<String, SiritoriIllegalWordException>
    fun loadConfig(blackboard: Map<String, String>)
    fun saveConfig(blackboard: MutableMap<String, String>)
}