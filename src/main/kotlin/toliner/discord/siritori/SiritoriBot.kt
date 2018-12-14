package toliner.discord.siritori

import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import toliner.discord.siritori.plugin.SiritoriIllegalWordException

val jda = JDABuilder(TODO("Load token from config file") as String)
    .setGame(Game.playing(TODO("Load game name from config file") as String))
    .build()
val config: Any = TODO("This is place holder of config data")
//ToDo: 排他的処理(処理中にメッセージ送信によるバグの防止。)

fun main() {
    jda.addEventListener(object : ListenerAdapter() {

        // ToDo: apply plugins
        val checker = SiritoriChecker.Builder().build()

        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            if (!verifyGuildAndChannel(event.guild, event.channel)) return
            //ToDo: check whether owner is banned from Siritori or not
            checker.check(event.message.contentRaw).fold({
                event.channel.sendMessage(TODO("チャットの単語の読みの最後の部分をメッセージで送信する。") as String).queue()
                }, { e: SiritoriIllegalWordException ->
                event.channel.sendMessage(e.message!!)
                })
        }

        private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean = TODO("reference config & check")
    })
}