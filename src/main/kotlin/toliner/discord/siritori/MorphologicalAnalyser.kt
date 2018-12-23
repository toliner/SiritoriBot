package toliner.discord.siritori

import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Tokenizer
import java.io.File
import java.io.PrintStream

data class AnalyzeResult(
    val original: String,
    val normalizedForm: String,
    val readingForm: String,
    val separated: List<String>,
    val parts: List<List<String>>
)

object MorphologicalAnalyser {
    private val dict = DictionaryFactory().create(ClassLoader.getSystemResource("sudachi_fulldict.json").readText())!!
    private val tokenizer = dict.create()!!.apply {
        setDumpOutput(PrintStream(File("logs/sudachi.log").createIfNotExists()))
    }

    fun analyze(word: String): AnalyzeResult {
        val tokens = tokenizer.tokenize(Tokenizer.SplitMode.C, word)
        return AnalyzeResult(
            original =  word,
            normalizedForm =  tokens.joinToString("") { it.normalizedForm() },
            readingForm = tokens.joinToString(""){ it.readingForm() },
            separated = tokens.map { it.surface() },
            parts = tokens.map { it.partOfSpeech() }
        )
    }
}

fun AnalyzeResult.isSingleWord() = separated.size == 1
fun AnalyzeResult.isNoun() = isSingleWord() && parts[0][0] == "名詞"

fun File.createIfNotExists(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}
