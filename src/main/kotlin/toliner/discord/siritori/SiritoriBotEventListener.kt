package toliner.discord.siritori

import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.map
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.ShutdownEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import toliner.discord.siritori.plugin.SiritoriIllegalWordException
import java.io.File
import java.lang.IllegalStateException

class SiritoriBotEventListener : ListenerAdapter() {
    val checker = SiritoriChecker.Builder().also { builder ->
        config.plugins.forEach {
            try {
                builder.applyPlugin(plugins[it] ?: throw RuntimeException("\"${it}\"という名前のpluginは存在しません。"))
                plugins[it]?.loadConfig(blackboard)
            } catch (e: Exception) {
                jda.shutdownNow()
                e.printStackTrace()
            }
        }
        logger.info(buildString {
            append("Plugin ")
            config.plugins.forEachIndexed { i, str ->
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
            logger.info("Message Received: ${event.message.contentRaw} by ${event.author}")
            event.channel.sendMessage(
                try {
                    checker.check(event.message.contentRaw).fold({
                        SiritoriLogger.addLog(SiritoriLog(event.author.idLong, it.word, it.analyzeResult.readingForm))
                        buildString {
                            appendln("単語:\"${event.message.contentRaw}\"(${it.analyzeResult.readingForm})を受け付けました。")
                            appendln("次の単語の読みの先頭の文字は\"${it.analyzeResult.readingForm.last()}\"です。")
                        }
                    }, { e: SiritoriIllegalWordException ->
                        logger.info("Wrong message received. word:${event.message.contentRaw} owner:${event.message.author} message:${e.message}")
                        e.message!!
                    })
                } catch (e: Exception) {
                    val stacktrace = e.stackTrace.joinToString(separator = "\n") { it.toString() }
                    logger.warn(e.toString())
                    logger.warn(stacktrace)
                    ownerDM.sendMessage(
                        buildString {
                            appendln("**Crush Report**")
                            appendln(e.toString())
                            appendln(stacktrace)
                            appendln("**Message**")
                            append("Content:")
                            appendln(event.message.contentRaw)
                            append("Author:")
                            appendln(event.author)
                        }
                    ).queue()
                    buildString {
                        appendln("予期せぬエラーが発生しました。")
                        appendln("開発者に情報を送信します。")
                    }
                }
            ).queue()
        }
    }

    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.idLong != config.owner) return
        if (event.message.contentRaw == "shutdown") {
            jda.shutdown()
        }
    }

    override fun onShutdown(event: ShutdownEvent?) {
        logger.info("Bot shutdown")
        checker.saveConfig(blackboard)
        File("blackboard.json").writeText(JSON.stringify((StringSerializer to StringSerializer).map, blackboard))
        SiritoriLogger.save()
    }

    private fun verifyGuildAndChannel(guild: Guild, channel: TextChannel): Boolean =
        config.guild.let { info -> info.guildId == guild.idLong && info.channelId == channel.idLong }

}