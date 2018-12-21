package toliner.discord.siritori

import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.map
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.ShutdownEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import toliner.discord.siritori.plugin.SiritoriCorePlugin
import toliner.discord.siritori.plugin.SiritoriIllegalWordException
import toliner.discord.siritori.plugin.WordSizeCounter
import toliner.discord.siritori.plugin.toPair
import java.io.File

val config = JSON.parse(ConfigData.serializer(), File("config.json").readText())
val jda = JDABuilder(config.token)
    .setGame(Game.playing(config.gameMessage))
    .build()
val blackboard = File("blackboard.json").let { file ->
    if (file.exists()) {
        JSON.parse((StringSerializer to StringSerializer).map, file.readText()) as MutableMap
    } else {
        file.createNewFile()
        mutableMapOf()
    }
}
val plugins = mapOf(
    SiritoriCorePlugin().toPair(),
    WordSizeCounter().toPair()
)

fun main() {
    jda.addEventListener(object : ListenerAdapter() {
        val checker = SiritoriChecker.Builder().also { builder ->
            config.plugins.forEach {
                try {
                    builder.applyPlugin(plugins[it] ?: throw RuntimeException("\"${it}\"という名前のpluginは存在しません。"))
                } catch (e: Exception) {
                    jda.shutdownNow()
                    e.printStackTrace()
                }
            }
            println(buildString {
                append("Plugin ")
                config.plugins.forEachIndexed{ i, str ->
                    append(str)
                    if (i != config.plugins.size - 1) {
                        append(", ")
                    }
                }
                append(" is loaded.")
            })

        }.build()

        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            if (!verifyGuildAndChannel(event.guild, event.channel) || event.author.idLong == jda.selfUser.idLong) return
            synchronized(checker) {
                event.channel.sendMessage(
                    checker.check(event.message.contentRaw).fold({
                        SiritoriLogger.addLog(SiritoriLog(event.author.idLong, it.word, it.yomi))
                        buildString {
                            appendln("単語:\"${event.message.contentRaw}\"(${it.yomi})を受け付けました。")
                            appendln("次の単語の読みの先頭の文字は\"${it.yomi.last()}\"です。")
                        }
                    }, { e: SiritoriIllegalWordException ->
                        e.message!!
                    })
                ).queue()
            }
        }

        override fun onShutdown(event: ShutdownEvent?) {
            checker.saveConfig(blackboard)
            File("blackboard.json").writeText(JSON.stringify((StringSerializer to StringSerializer).map, blackboard))
            SiritoriLogger.save()
        }

        private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean =
            config.guild.let { info -> info.guildId == guild.idLong && info.channelId == channel.idLong }
    })
}