package toliner.discord.siritori

import toliner.discord.siritori.plugin.ISiritoriCheckerPlugin
import com.github.kittinunf.result.Result
import toliner.discord.siritori.plugin.SiritoriIllegalWordException

class SiritoriChecker(private val plugins: List<ISiritoriCheckerPlugin>) {
    fun check(word: String): Result<Unit, SiritoriIllegalWordException> {
        return plugins.asSequence()
            .map {
                it.check(word)
            }.filter {
                it is Result.Failure
            }.firstOrNull() ?: Result.of { Unit }
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