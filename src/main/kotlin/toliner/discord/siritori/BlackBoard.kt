package toliner.discord.siritori

import kotlinx.serialization.Serializable

@Serializable
class BlackBoard {
    private val int: MutableMap<String, Int> = mutableMapOf()
    private val long: MutableMap<String, Long> = mutableMapOf()
    private val double: MutableMap<String, Double> = mutableMapOf()
    private val string: MutableMap<String, String> = mutableMapOf()

    fun setInt(key: String, value: Int) {
        int[key] = value
    }

    fun setLong(key: String, value: Long) {
        long[key] = value
    }

    fun setDouble(key: String, value: Double) {
        double[key] = value
    }

    fun setString(key: String, value: String) {
        string[key] = value
    }

    fun getInt(key: String) = int[key]
    fun getLong(key: String) = long[key]
    fun getDouble(key: String) = double[key]
    fun getString(key: String) = string[key]
}