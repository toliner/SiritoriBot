package toliner.discord.siritori

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    val guild: GuildInfo,
    val token: String,
    @Optional
    val gameMessage: String = "しりとり",
    val plugins: List<String>
)

@Serializable
data class GuildInfo(
    val guildId: Long,
    val channelId: Long
)