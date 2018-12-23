package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result
import toliner.discord.siritori.AnalyzeResult

class SiritoriIllegalWordException(message: String): RuntimeException(message)

data class SiritoriWord(val word: String, val analyzeResult: AnalyzeResult)

interface ISiritoriCheckerPlugin {
    val name: String
    fun check(word: SiritoriWord): Result<SiritoriWord, SiritoriIllegalWordException>
    fun loadConfig(blackboard: Map<String, String>)
    fun saveConfig(blackboard: MutableMap<String, String>)
}

fun ISiritoriCheckerPlugin.toPair() = name to this