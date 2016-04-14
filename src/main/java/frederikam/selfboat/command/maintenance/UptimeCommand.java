package frederikam.selfboat.command.maintenance;

import frederikam.selfboat.commandmeta.CommandManager;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.TextUtils;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.Permission;

public class UptimeCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        long totalSecs = (System.currentTimeMillis() - SelfBoat.START_TIME) / 1000;
        int days = (int) (totalSecs / (60 * 60 * 24));
        int hours = (int) ((totalSecs / (60 * 60)) % 24);
        int mins = (int) ((totalSecs / 60) % 60);
        int secs = (int) (totalSecs % 60);

        String str = " This boat has been floating for "
                + days + " days, "
                + hours + " hours, "
                + mins + " minutes and "
                + secs + " seconds.\n"
                + "This bot has executed " + (CommandManager.commandsExecuted - 1) + " commands this session.\n";
        str = str + "That's a rate of " + (float) (CommandManager.commandsExecuted - 1) / ((float) totalSecs / (float) (60 * 60)) + " commands per hour\n\n```";
        str = str + "Reserved memory:         " + Runtime.getRuntime().totalMemory() / 1000000 + "MB\n";
        str = str + "-> Of which is used:     " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + "MB\n";
        str = str + "-> Of which is free:     " + Runtime.getRuntime().freeMemory() / 1000000 + "MB\n";
        str = str + "Max reservable:          " + Runtime.getRuntime().maxMemory() / 1000000 + "MB\n";

        str = str + "\n----------\n\n";

        str = str + "Known servers:           " + guild.getJDA().getGuilds().size() + "\n";
        str = str + "Known text channels:     " + guild.getJDA().getTextChannels().size() + "\n";
        str = str + "Known users in servers:  " + guild.getJDA().getUsers().size() + "\n";
        str = str + "JDA responses total:     " + guild.getJDA().getResponseTotal() + "\n";
        str = str + "JDA version:             " + JDAInfo.VERSION;
        str = str + "```";

        channel.sendMessage(TextUtils.prefaceWithMention(invoker, str));
        //channel.sendMessage(str2);
        //hannel.sendMessage(str3);
    }

}
