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
public class ExitCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if (invoker.getId().equals(SelfBoat.OWNER_ID)) {
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " goodbye!!"));
            System.exit(0);
        } else {
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " you are not allowed to use that command!"));
        }
    }

}
