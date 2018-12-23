package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result
import toliner.discord.siritori.SiritoriLogger

class SiritoriCorePlugin : ISiritoriCheckerPlugin {
    override val name: String
        get() = "core"
    private val regex = """[\p{IsHan}\p{IsHiragana}\p{IsKatakana}ーｰﾟﾞ]+""".toRegex()
    private val lowerCase = "ァィゥェォヵㇰヶㇱㇲッㇳㇴㇵㇶㇷㇷㇸㇹㇺャュョㇻㇼㇽㇾㇿヮ"

    override fun check(word: SiritoriWord): Result<SiritoriWord, SiritoriIllegalWordException> {
        if (!regex.matches(word.word)) {
            return Result.error(SiritoriIllegalWordException("日本語(ひらがな・かたかな・漢字・ー)のみが使用可能です。"))
        }
        if (SiritoriLogger.contains(word.word)) {
            return Result.error(SiritoriIllegalWordException("その単語はすでに使われています。"))
        }
        val yomi = word.analyzeResult.readingForm.let { if (it.isEmpty()) word.analyzeResult.normalizedForm else it }
        val last = SiritoriLogger.getLastYomi()
        return if (last.isNullOrEmpty() || judge(last, yomi)) {
            Result.of { word }
        } else {
            Result.error(
                SiritoriIllegalWordException(
                    "単語\"${word.word}\"の読み\"$yomi\"の最初の文字\'${yomi.last()}\'は、直前の単語\"${SiritoriLogger.getLast()?.word}\"の読み\"$last\"の最後の文字\'${last.last()}\'と一致しません。"
                )
            )
        }
    }

    private fun judge(last: String, current: String): Boolean {
         return if (lowerCase.contains(last.last())) {
             val cut = last.takeLastWhile { lowerCase.contains(it) }
             cut == current.take(cut.length)
        } else {
            last.last() == current.first()
        }
    }

    override fun loadConfig(blackboard: Map<String, String>) {
        //Do Nothing
    }

    override fun saveConfig(blackboard: MutableMap<String, String>) {
        // Do Nothing
    }
}
