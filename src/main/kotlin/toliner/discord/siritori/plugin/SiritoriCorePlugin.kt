package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result
import net.moraleboost.mecab.impl.StandardTagger
import toliner.discord.siritori.SiritoriLogger

class SiritoriCorePlugin : ISiritoriCheckerPlugin {
    override val name: String
        get() = "core"
    private val regex = """[\p{IsHan}\p{IsHiragana}\p{IsKatakana}ー]+""".toRegex()
    private val tagger = StandardTagger("-Oyomi")

    override fun check(word: SiritoriWord): Result<SiritoriWord, SiritoriIllegalWordException> {
        if (!regex.matches(word.word)) {
            return Result.error(SiritoriIllegalWordException("日本語(ひらがな・かたかな・漢字・ー)のみが使用可能です。"))
        }
        if (SiritoriLogger.contains(word.word)) {
            return Result.error(SiritoriIllegalWordException("その単語はすでに使われています。"))
        }
        val yomi = getYomi(word.word)
        val last = SiritoriLogger.lastYomi
        return if (last.isEmpty() || last.last() == yomi.first()) {
            SiritoriLogger.lastYomiTemp = yomi
            Result.of { SiritoriWord(word.word, yomi) }
        } else {
            Result.error(
                SiritoriIllegalWordException(
                    "単語\"$word\"の読み\"$yomi\"の最初の文字\'${yomi.last()}\'は、直前の単語\"${SiritoriLogger.getLast()}\"の読み\"$last\"の最後の文字\'${last.last()}\'と一致しません。"
                )
            )
        }
    }

    override fun loadConfig(blackboard: Map<String, String>) {
        val lastWord = SiritoriLogger.getLast()
        if (lastWord != null && SiritoriLogger.lastYomi.isEmpty()) {
            SiritoriLogger.lastYomi = getYomi(lastWord.word)
        }
    }

    override fun saveConfig(blackboard: MutableMap<String, String>) {
        tagger.destroy()
    }

    private fun getYomi(word: String): String {
        val lattice = tagger.createLattice()
        lattice.setSentence(word)
        tagger.parse(lattice)
        var node = lattice.bosNode()
        return buildString {
            while (node != null) {
                append(node.feature().split(',').last())
                node = node.next()
            }
        }.filterNot { it == '*' }
    }
}
