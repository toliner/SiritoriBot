package toliner.discord.siritori

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class SiritoriLog(
    @SerialId(1) val owner: Long,
    @SerialId(2) val word: String
)
