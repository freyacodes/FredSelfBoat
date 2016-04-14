package frederikam.selfboat.commandmeta;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public interface ICommand {

    public abstract void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args);

}
