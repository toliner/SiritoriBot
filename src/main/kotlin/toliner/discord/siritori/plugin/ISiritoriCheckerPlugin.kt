package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result

class SiritoriIllegalWordException(message: String): RuntimeException(message)

interface ISiritoriCheckerPlugin {
    fun check(word: String): Result<String, SiritoriIllegalWordException>
}