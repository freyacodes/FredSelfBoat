package frederikam.selfboat.command.util;

import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.util.TextUtils;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.utils.PermissionUtil;

public class ClearCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();
        
        if(PermissionUtil.checkPermission(invoker, Permission.MESSAGE_MANAGE, channel) == false && invoker.getId().equals(SelfBoat.OWNER_ID) == false){
            TextUtils.replyWithMention(channel, invoker, " You must have Manage Messages to do that!s");
            return;
        }
        
        MessageHistory history = new MessageHistory(jda, channel);
        List<Message> msgs = history.retrieve(100);

        for (Message msg : msgs) {
            if(msg.getAuthor().equals(SelfBoat.myUser)){
                msg.deleteMessage();
            }
        }
    }
}
