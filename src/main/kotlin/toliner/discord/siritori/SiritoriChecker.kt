package toliner.discord.siritori

import com.github.kittinunf.result.Result
import toliner.discord.siritori.plugin.ISiritoriCheckerPlugin
import toliner.discord.siritori.plugin.SiritoriIllegalWordException

class SiritoriChecker(private val plugins: List<ISiritoriCheckerPlugin>) {
    fun check(word: String): Result<String, SiritoriIllegalWordException> {
        return plugins.fold(Result.of { word }) { acc, plugin ->
            acc.fold({
                plugin.check(it)
            }, {
                acc
            })
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