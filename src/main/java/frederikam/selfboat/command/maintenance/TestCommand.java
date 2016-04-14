package frederikam.selfboat.command.maintenance;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

/**
 *
 * @author frederik
 */
public class TestCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        throw new NullPointerException("Test!");
    }

}
