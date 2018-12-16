package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result

class WordSizeCounter: ISiritoriCheckerPlugin {
    override val name: String
        get() = "word-count"

    override fun check(word: String): Result<String, SiritoriIllegalWordException> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}