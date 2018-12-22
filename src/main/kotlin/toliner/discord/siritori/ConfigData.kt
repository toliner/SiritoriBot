package toliner.discord.siritori

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

const val FIVE_MINUTE = 5L * 60L * 1000L

@Serializable
data class ConfigData(
    val guild: GuildInfo,
    val token: String,
    @Optional
    val gameMessage: String = "しりとり",
    val plugins: List<String>,
    @Optional
    val savePeriod: Long = FIVE_MINUTE,
    val owner: Long
)

@Serializable
data class GuildInfo(
    val guildId: Long,
    val channelId: Long
)