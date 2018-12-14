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
//ToDo: 排他的処理(処理中にメッセージ送信によるバグの防止。)

fun main() {
    jda.addEventListener(object : ListenerAdapter() {

        // ToDo: apply plugins
        val checker = SiritoriChecker.Builder().build()

        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            if (!verifyGuildAndChannel(event.guild, event.channel)) return
            //ToDo: check whether owner is banned from Siritori or not
            event.channel.sendMessage(
                checker.check(event.message.contentRaw).fold({
                    TODO("チャットの単語の読みの最後の部分をメッセージで送信する。") as String
                }, { e: SiritoriIllegalWordException ->
                    e.message!!
                })
            ).queue()
        }

        private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean =
            config.guild.let { info -> info.guildId == guild.idLong && info.channelId == channel.idLong }
    })
}