package toliner.discord.siritori

import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.map
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.slf4j.LoggerFactory
import toliner.discord.siritori.plugin.HankakuKatakanaConverter
import toliner.discord.siritori.plugin.SiritoriCorePlugin
import toliner.discord.siritori.plugin.WordSizeCounter
import toliner.discord.siritori.plugin.toPair
import java.io.File

val logger = LoggerFactory.getLogger("SiritoriBot")
val config = try {
    JSON.parse(ConfigData.serializer(), File("config.json").readText())
} catch (e: Exception) {
    logger.warn(e.message)
    throw e
}
val jda = JDABuilder(config.token)
    .setGame(Game.playing(config.gameMessage))
    .build()
val blackboard = File("blackboard.json").let { file ->
    if (file.exists()) {
        JSON.parse((StringSerializer to StringSerializer).map, file.readText()) as MutableMap
    } else {
        logger.info("blackboard.json does not exist.")
        file.createNewFile()
        logger.info("Succeed to create blackboard.json")
        mutableMapOf()
    }
}
val plugins = mapOf(
    SiritoriCorePlugin().toPair(),
    WordSizeCounter().toPair(),
    HankakuKatakanaConverter().toPair()
)
val owner by lazy { jda.getUserById(config.owner)!! }
val ownerDM by lazy { owner.openPrivateChannel().complete() }

fun main() {
    jda.addEventListener(SiritoriCorePlugin())
}