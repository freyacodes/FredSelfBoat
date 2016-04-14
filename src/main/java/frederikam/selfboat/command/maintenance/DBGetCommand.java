package frederikam.selfboat.command.maintenance;

import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.HttpUtils;
import frederikam.selfboat.util.TextUtils;

/**
 *
 * @author frederik
 */
public class DBGetCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if (invoker.getId().equals(SelfBoat.OWNER_ID)) {
            String result = "";
            //if ("Windows 10".equals(System.getProperty("os.name"))) {
            try {
                //We are likely in a testing environment
                result = HttpUtils.httpGet("http://revgamesrblx.com/api/read.php?password="
                        + HttpUtils.scopePasswords.get(args[1])
                        + "&scope=" + args[1]
                        + "&guid=FredBoat"
                        + "&key=" + args[2]);
            } catch (IOException ex) {
                channel.sendMessage(TextUtils.prefaceWithMention(invoker, " an error occured: " + ex.getMessage()));
            }
            //} else {
            //We are likely on Frednet
            //}
            Message msg = TextUtils.prefaceWithMention(invoker, "");
            MessageBuilder builder = new MessageBuilder();
            builder.appendString("```" + result + "```");
            channel.sendMessage(builder.build());
        } else {
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " you are not allowed to use that command!"));
        }
    }

}
