package toliner.discord.siritori

import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Morpheme
import com.worksap.nlp.sudachi.Tokenizer
import java.io.File
import java.io.PrintStream

object MorphologicalAnalyser {
    private val dict = DictionaryFactory().create(ClassLoader.getSystemResource("sudachi_fulldict.json").readText())!!
    private val tokenizer = dict.create()!!.apply {
        setDumpOutput(PrintStream(File("logs/sudachi.log").createIfNotExsist()))
    }

    fun analyze(word: String): List<Morpheme> {
        return tokenizer.tokenize(Tokenizer.SplitMode.C, word)
    }

    fun getNormalized(tokens: List<Morpheme>): String {
        return tokens.joinToString(separator = "") {
            it.normalizedForm()
        }
    }

    fun getNormalized(word: String) = getNormalized(analyze(word))

    fun getReadingForm(tokens: List<Morpheme>): String {
        return tokens.joinToString(separator = "") {
            it.readingForm()
        }
    }

    fun getReadingForm(word: String) = getReadingForm(tokenizer.tokenize(word))
}

fun String.isSingleWord(): Boolean {
    return MorphologicalAnalyser.analyze(this).size == 1
}

fun String.isSingleNoun(): Boolean {
    val tokens = MorphologicalAnalyser.analyze(this)
    if(tokens.size != 1) return false
    val token = tokens[0]
    return token.partOfSpeech()[0] == "名詞"
}

fun File.createIfNotExsist(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}
