package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON

class WordSizeCounter : ISiritoriCheckerPlugin {

    private lateinit var config: WordSizeCountConfig

    override val name: String
        get() = "word-count"

    override fun check(word: String): Result<String, SiritoriIllegalWordException> {
        return if (word.length in config.min..config.max) {
            Result.of { word }
        } else {
            Result.error(SiritoriIllegalWordException("文字数は${config.min}以上${config.max}以下でなければなりません。"))
        }
    }

    override fun loadConfig(blackboard: Map<String, String>) {
        val json = blackboard[name]
        config = if (json != null) {
            JSON.parse(WordSizeCountConfig.serializer(), json)
        } else {
            WordSizeCountConfig()
        }
    }

    override fun saveConfig(blackboard: MutableMap<String, String>) {
        blackboard[name] = JSON.stringify(WordSizeCountConfig.serializer(), config)
    }

    @Serializable
    private data class WordSizeCountConfig(var min: Int = 0, var max: Int = Int.MAX_VALUE)
}