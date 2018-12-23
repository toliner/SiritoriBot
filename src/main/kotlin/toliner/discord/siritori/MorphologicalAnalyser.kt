package toliner.discord.siritori

import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Tokenizer
import org.slf4j.LoggerFactory
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
    private val tokenizer = DictionaryFactory()
        .create(ClassLoader.getSystemResource("sudachi_fulldict.json").readText())!!
        .use {
            it.create()!!.apply {
                setDumpOutput(PrintStream(File("logs/sudachi.log").createIfNotExists()))
            }
        }
    private val logger = LoggerFactory.getLogger("Sudachi")

    fun analyze(word: String): AnalyzeResult {
        val tokens = tokenizer.tokenize(Tokenizer.SplitMode.C, word)
        logger.debug("Tokenize Word: $word")
        logger.debug("Surface, Normalized, Reading, PartOfSpeech")
        tokens.forEach {
            logger.debug("${it.surface()}, ${it.normalizedForm()}, ${it.readingForm()}, [${it.partOfSpeech().joinToString()}]")
        }
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
