package frederikam.selfboat.command.fun;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.util.TextUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class EndTrollCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel c, User invoker, Message message, String[] args) {
        guild.getJDA().getAudioManager().closeAudioConnection();
        TextUtils.replyWithMention(c, invoker, " Closed connection!");
    }
}
