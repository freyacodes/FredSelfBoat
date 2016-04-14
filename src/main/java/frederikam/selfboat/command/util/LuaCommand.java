package frederikam.selfboat.command.util;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.lua.LuaParser;
import frederikam.selfboat.lua.LuaParser.Outcome;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.TextUtils;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class LuaCommand extends Command {

    public static final int MAX_COMPUTATION_TIME = 5;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            String source = message.getContent().replaceFirst(args[0], "");
            HashMap<String, String> luaArgs = new HashMap<>();
            luaArgs.put("guildId", guild.getId());
            Outcome outcome = LuaParser.parseLua(source, MAX_COMPUTATION_TIME * 1000, luaArgs);
            String finalOutMsg = outcome.output;

            if (outcome.timedOut) {
                TextUtils.replyWithMention(channel, invoker, " Function timed out :anger: allowed computation time is " + MAX_COMPUTATION_TIME + " seconds.");
            } else if (!finalOutMsg.equals("")) {
                if (finalOutMsg.length() > 2000) {
                    TextUtils.replyWithMention(channel, invoker, " Output buffer is too large :anger: Discord only allows 2000 characters per message, got " + finalOutMsg.length());
                } else {
                    TextUtils.replyWithMention(channel, invoker, " " + finalOutMsg);
                }
            }

            if (outcome.luaError != null) {
                TextUtils.replyWithMention(channel, invoker, " A Lua error occured :anger:\n```" + outcome.luaError + "```");
            }

        } catch (InterruptedException ex) {

        } catch (ExecutionException ex) {

        }
    }
}
