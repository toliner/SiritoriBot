package toliner.discord.siritori.plugin

import com.github.kittinunf.result.Result

private const val 半角 = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｵﾝｧｨｩｪｫｬｭｮｯｦ"
private const val 全角 = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワオンァィゥェォャュョュヲ"
private val 濁点半角カナ = "ｶﾞｷﾞｸﾞｹﾞｺﾞｻﾞｼﾞｽﾞｾﾞｿﾞﾀﾞﾁﾞﾂﾞﾃﾞﾄﾞﾊﾞﾋﾞﾌﾞﾍﾞﾎﾞ".chunked(2)
private val 濁点カナ = "ガギグゲゴザジズゼゾダヂヅデドバビブベボ".mapIndexed { index, c -> 濁点半角カナ[index] to c.toString() }.toMap()
private val 半濁点半角カナ = "ﾊﾟﾋﾟﾌﾟﾍﾟﾎﾟ".chunked(2)
private val 半濁点カナ = "パピプペポ".mapIndexed { index, c -> 半濁点半角カナ[index] to c.toString() }.toMap()

class HankakuKatakanaConverter: ISiritoriCheckerPlugin {
    override val name: String
        get() = "カタカナ半角2全角"

    override fun check(word: SiritoriWord): Result<SiritoriWord, SiritoriIllegalWordException> {
        return Result.of {
            SiritoriWord(
                convert(word.word),
                convert(word.yomi)
            )
        }
    }

    private fun convert(word: String): String {
        var str = word
        fun List<Int>.getConnectedString(): List<String> {
            return filter { it != 0 }.map {
                buildString {
                    append(str[it - 1])
                    append(str[it])
                }
            }
        }
        val dakuten = str.mapIndexedNotNull { index, c -> if (c == 'ﾞ') index else null }.getConnectedString().toSet()
        val handakuten = str.mapIndexedNotNull { index, c -> if (c == 'ﾟ') index else null }.getConnectedString().toSet()
        dakuten.forEach {
            str = str.replace(it, 濁点カナ[it]!!)
        }
        handakuten.forEach {
            str = str.replace(it, 半濁点カナ[it]!!)
        }
        return str.map {
            val index = 半角.indexOf(it)
            if (index == -1) {
                it
            } else {
                全角[index]
            }
        }.joinToString(separator = "")
    }

    override fun loadConfig(blackboard: Map<String, String>) {
        // Do Nothing
    }

    override fun saveConfig(blackboard: MutableMap<String, String>) {
        // Do Nothing
    }
}