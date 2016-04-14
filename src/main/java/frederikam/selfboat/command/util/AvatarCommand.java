package frederikam.selfboat.command.util;

import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.TextUtils;

public class AvatarCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if(message.getMentionedUsers().isEmpty()){
            TextUtils.replyWithMention(channel, invoker, " proper usage is: ```;;avatar @<username>```");
        } else {
            TextUtils.replyWithMention(channel, invoker, " found it!\n"+message.getMentionedUsers().get(0).getAvatarUrl());
        }
    }

}
