package toliner.discord.siritori

import kotlinx.serialization.json.JSON
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.ShutdownEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import toliner.discord.siritori.plugin.ISiritoriCheckerPlugin
import toliner.discord.siritori.plugin.SiritoriIllegalWordException
import java.io.File
import java.util.*

val config = JSON.parse(ConfigData.serializer(), File("config.json").bufferedReader().use { it.readText() })
val jda = JDABuilder(config.token)
    .setGame(Game.playing(config.gameMessage))
    .build()
val blackboard = File("blackboard.json").let { file ->
    if (file.exists()) {
        JSON.parse(BlackBoard.serializer(), file.readText())
    } else {
        file.createNewFile()
        BlackBoard()
    }
}

fun main() {
    jda.addEventListener(object : ListenerAdapter() {
        val checker = SiritoriChecker.Builder().also { builder ->
            val f = ClassLoader::class.java.getDeclaredField("classes")
            f.isAccessible = true
            val classes = f.get(ClassLoader.getSystemClassLoader()) as Vector<Class<*>>
            classes.asSequence().filter {
                !it.isAnnotation && !it.isAnonymousClass && !it.isArray && !it.isEnum && !it.isInterface && !it.isPrimitive
            }.filter {
                ISiritoriCheckerPlugin::class.java.isAssignableFrom(it)
            }.mapNotNull {
                try {
                    it.getConstructor().newInstance() as ISiritoriCheckerPlugin
                } catch (e: Exception) {
                    null
                }
            }.forEach {
                builder.applyPlugin(it)
            }
        }.build()

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

        override fun onShutdown(event: ShutdownEvent?) {
            File("blackboard.json").writeText(JSON.stringify(BlackBoard.serializer(), blackboard))
        }

        private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean =
            config.guild.let { info -> info.guildId == guild.idLong && info.channelId == channel.idLong }
    })
}