package toliner.discord.siritori

import com.github.kittinunf.result.Result
import toliner.discord.siritori.plugin.ISiritoriCheckerPlugin
import toliner.discord.siritori.plugin.SiritoriIllegalWordException
import toliner.discord.siritori.plugin.SiritoriWord

class SiritoriChecker(private val plugins: List<ISiritoriCheckerPlugin>) {
    fun check(word: String): Result<SiritoriWord, SiritoriIllegalWordException> {
        return plugins.fold(Result.of { SiritoriWord(word, word) }) { acc, plugin ->
            acc.fold({
                plugin.check(it)
            }, {
                acc
            })
        }
    }

    fun saveConfig(blackboard: MutableMap<String, String>) {
        plugins.forEach {
            it.saveConfig(blackboard)
        }
    }

    class Builder {
        private val plugins = mutableListOf<ISiritoriCheckerPlugin>()

        fun applyPlugin(plugin: ISiritoriCheckerPlugin) {
            plugins += plugin
        }

        operator fun plusAssign(plugin: ISiritoriCheckerPlugin) = applyPlugin(plugin)

        fun build(): SiritoriChecker = SiritoriChecker(plugins)
    }
}