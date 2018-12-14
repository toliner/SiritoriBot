package toliner.discord.siritori

import kotlinx.serialization.json.JSON
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import toliner.discord.siritori.plugin.SiritoriIllegalWordException
import java.io.File

val config = JSON.parse(ConfigData.serializer(), File("config.json").bufferedReader().use { it.readText() })
val jda = JDABuilder(config.token)
    .setGame(Game.playing(config.gameMessage))
    .build()

fun main() {
    jda.addEventListener(object : ListenerAdapter() {

        // ToDo: apply plugins
        val checker = SiritoriChecker.Builder().build()

        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            if (!verifyGuildAndChannel(event.guild, event.channel)) return
            synchronized(checker) {
                event.channel.sendMessage(
                    checker.check(event.message.contentRaw).fold({
                        buildString {
                            appendln("単語:\"${event.message.contentRaw}\"($it)を受け付けました。")
                            appendln("次の単語の読みの先頭の文字は\"${it.last()}\"です。")
                        }
                    }, { e: SiritoriIllegalWordException ->
                        e.message!!
                    })
                ).queue()
            }
        }

        private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean =
            config.guild.let { info -> info.guildId == guild.idLong && info.channelId == channel.idLong }
    })
}