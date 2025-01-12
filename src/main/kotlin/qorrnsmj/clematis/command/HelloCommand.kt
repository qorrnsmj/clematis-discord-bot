package qorrnsmj.clematis.command

import com.github.kaktushose.jda.commands.annotations.interactions.Choices
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Interaction
class HelloCommand {
    @SlashCommand(value = "hello", desc = "Hello?")
    fun hello(event: CommandEvent) {
        event.reply("Hello ${event.member?.nickname ?: event.member!!.effectiveName} !")
    }

    @SlashCommand(value = "day-hello", desc = "Good Morning!")
    fun dayHello(event: CommandEvent, @Choices("set", "unset") action: String) {
        when (action) {
            "set" -> setDayHello(event)
            "unset" -> unsetDayHello(event)
        }
    }

    private fun setDayHello(event: CommandEvent) {
        if (scheduler != null) {
            event.reply("Already set day-hello!")
                .delete().queueAfter(5, TimeUnit.SECONDS)
            return
        }

        scheduler = Executors.newScheduledThreadPool(1)
        scheduler!!.scheduleAtFixedRate({
            event.messageChannel.sendMessageEmbeds(getMessageEmbed()).queue()
        }, getInitialDelay(), 24 * 86400, TimeUnit.SECONDS)

        event.reply("Set day-hello!")
            .delete().queueAfter(5, TimeUnit.SECONDS)
    }

    private fun unsetDayHello(event: CommandEvent) {
        scheduler?.shutdown()
        scheduler = null

        event.reply("Unset day-hello!")
            .delete().queueAfter(5, TimeUnit.SECONDS)
    }

    private fun getMessageEmbed(): MessageEmbed {
        // title
        val oCount = Random.nextInt(1, 11)
        val emoji = if (oCount == 1) "<:god:1327940708589371442>" else ":sunny:"
        val title = "$emoji G" + "o".repeat(oCount) + "d Morning!"

        // description
        val today = LocalDate.now()
        val daysPassed = today.dayOfYear
        val progress = (daysPassed / 365f)
        val barLength = 20
        val filledLength = (barLength * progress).toInt()
        val progressBar = "#".repeat(filledLength) + " - ".repeat(barLength - filledLength)

        val description = """
            **[Date]** ${today.format(DateTimeFormatter.ofPattern("MMMM d - EEEE"))}
            **[Progress]** ${"%.2f".format(progress * 100f)}% ($daysPassed / 365)
            **[$progressBar]**
        """.trimIndent()

        return EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setFooter("provided by clematis", null)
            .setColor(0xbb9adc)
            .build()
    }

    private fun getInitialDelay(): Long {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentSecond = currentTime.get(Calendar.SECOND)

        var delayHours = TARGET_HOUR - currentHour
        if (delayHours < 0) {
            // If the target time has already passed, add 24 hours
            delayHours += 24
        }

        // Calculate the remaining delay in minutes and seconds
        val delayMinutes = 60 - currentMinute
        val delaySeconds = 60 - currentSecond

        // Convert the delay time to seconds and sum them up
        val delayInSeconds = TimeUnit.HOURS.toSeconds(delayHours.toLong()) +
                             TimeUnit.MINUTES.toSeconds(delayMinutes.toLong()) +
                             delaySeconds.toLong()

        return delayInSeconds
    }

    companion object {
        private const val TARGET_HOUR = 6
        private var scheduler: ScheduledExecutorService? = null
    }
}
