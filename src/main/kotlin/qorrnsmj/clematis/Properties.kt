package qorrnsmj.clematis

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess

object Properties {
    var BOT_STATUS = OnlineStatus.ONLINE
    var BOT_ACTIVITY = Activity.playing("with Serenity")

    var TOKEN = ""
    var GUILD_ID = ""

    fun load() {
        val rootPath = this::class.java.protectionDomain.codeSource.location.toURI().path
        val propertyFile = File(rootPath).parentFile.resolve("bot.properties")

        if (!propertyFile.exists()) {
            propertyFile.writeText("""
                TOKEN=
                GUILD_ID=
            """.trimIndent())

            Clematis.logger.info("Please fill in the bot.property file")
            exitProcess(1)
        }

        java.util.Properties()
            .apply { load(FileInputStream(propertyFile)) }
            .let {
                TOKEN = it.getProperty("TOKEN", TOKEN)
                GUILD_ID = it.getProperty("GUILD_ID", GUILD_ID)
            }
    }
}
