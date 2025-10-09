package qorrnsmj.clematis.command

import com.github.kaktushose.jda.commands.annotations.interactions.Button
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction
import com.github.kaktushose.jda.commands.annotations.interactions.Optional
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import qorrnsmj.clematis.Clematis.guild
import qorrnsmj.clematis.listener.VoiceTimeManager
import java.util.concurrent.TimeUnit

@Interaction
class VoiceTimeCommand {
    @SlashCommand(value = "voicetime", desc = "Show your or someone’s total VC time")
    fun showVoiceTime(event: CommandEvent, @Optional userMention: String?) {
        val userId = Regex("<@!?([0-9]+)>").find(userMention ?: "")?.groupValues?.get(1)?.toLongOrNull()
            ?: event.user.idLong
        val time = VoiceTimeManager.getTotalTime(userId)

        event.reply(
            EmbedBuilder()
                .setTitle("🎧 Voice Time")
                .setDescription("<@$userId> has been in voice chat for **${formatDuration(time)}**.")
                .setColor(0x7289DA)
        )
    }

    @SlashCommand(value = "voicerank", desc = "Show VC time ranking")
    fun showRanking(event: CommandEvent) {
        val title = "🏆 VC Ranking (Total)"
        val ranking = VoiceTimeManager.getTotalRanking().take(10)

        event.with().components("onTotal", "onMonthly", "onWeekly")
            .reply(buildRankingEmbed(title, ranking))
    }

    @Button(value = "Total", style = ButtonStyle.PRIMARY)
    fun onTotal(event: ComponentEvent) {
        handleButton(event, "🏆 VC Ranking (Total)", VoiceTimeManager.getTotalRanking().take(10))
    }

    @Button(value = "Monthly", style = ButtonStyle.PRIMARY)
    fun onMonthly(event: ComponentEvent) {
        handleButton(event, "🏆 VC Ranking (Monthly)", VoiceTimeManager.getMonthlyRanking().take(10))
    }

    @Button(value = "Weekly", style = ButtonStyle.PRIMARY)
    fun onWeekly(event: ComponentEvent) {
        handleButton(event, "🏆 VC Ranking (Weekly)", VoiceTimeManager.getWeeklyRanking().take(10))
    }

    private fun handleButton(event: ComponentEvent, title: String, ranking: List<Pair<Long, Long>>) {
        event.jdaEvent(ButtonInteractionEvent::class.java).run {
            deferEdit().queue()
            hook.editOriginalEmbeds(buildRankingEmbed(title, ranking).build()).queue()
        }
    }

    private fun buildRankingEmbed(title: String, ranking: List<Pair<Long, Long>>): EmbedBuilder {
        val desc = ranking.mapIndexed { i, (userId, time) ->
            val exists = guild.getMemberById(userId) != null
            val userName = if (exists) "<@$userId>" else "Unknown"
            val emoji = when (i) {
                0 -> ":first_place:"
                1 -> ":second_place:"
                2 -> ":third_place:"
                3 -> ":four:"
                4 -> ":five:"
                5 -> ":six:"
                6 -> ":seven:"
                7 -> ":eight:"
                8 -> ":nine:"
                9 -> ":keycap_ten:"
                else -> "${i + 1}. "
            }

            "$emoji $userName ${formatDuration(time)}"
        }.joinToString("\n")

        return EmbedBuilder()
            .setTitle(title)
            .setDescription(desc.ifEmpty { "No data yet." })
            .setColor(0xF1C40F)
    }

    private fun formatDuration(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        return "%02dh %02dm".format(hours, minutes)
    }
}
