package tech.gdragon.commands.settings

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.transactions.transaction
import tech.gdragon.BotUtils
import tech.gdragon.commands.CommandHandler
import tech.gdragon.commands.InvalidCommand
import tech.gdragon.db.dao.Guild

class AutoSave : CommandHandler() {
  override fun action(args: Array<String>, event: GuildMessageReceivedEvent) {

    require(args.isEmpty()) {
      throw InvalidCommand(::usage, "Incorrect number of arguments: ${args.size}")
    }

    val autoSave: Boolean? = transaction {
      Guild
        .findById(event.guild.idLong)
        ?.settings
        ?.apply { autoSave = !autoSave } // Toggle AutoSave
        ?.autoSave
    }

    val defaultChannel = BotUtils.defaultTextChannel(event.guild) ?: event.channel

    val message =
      when (autoSave) {
        true -> ":vibration_mode::floppy_disk: _Automatically saving at the end of each session._"
        false -> ":mobile_phone_off::floppy_disk: _Not saving at the end of each session._"
        else -> ":no_entry_sign: _Could not toggle autosave option._"
      }

    BotUtils.sendMessage(defaultChannel, message)
  }

  override fun usage(prefix: String): String = "${prefix}autosave"

  override fun description(): String = "Toggles the option to automatically save and send all files at the end of each session - not just saved or clipped files."
}
