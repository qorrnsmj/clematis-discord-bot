package qorrnsmj.clematis

import com.github.kaktushose.jda.commands.JDACommands
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.EnumSet

object Clematis {
    lateinit var logger: Logger
    lateinit var guild: Guild

    @JvmStatic
    fun main(args: Array<String>) {
        logger = LoggerFactory.getLogger(Clematis::class.java)
        Properties.load()

        val jda = JDABuilder
            .createDefault(Properties.TOKEN)
            .setStatus(Properties.BOT_STATUS)
            .setActivity(Properties.BOT_ACTIVITY)
            .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
            .build()

        jda.awaitReady()
        guild = jda.getGuildById(Properties.GUILD_ID)!!
        JDACommands.start(jda, this::class.java)
    }
}
