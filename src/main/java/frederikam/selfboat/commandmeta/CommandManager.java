package frederikam.selfboat.commandmeta;

import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.util.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class CommandManager {

    public static HashMap<String, ICommand> commands = new HashMap<>();
    public static ICommand defaultCmd = new UnknownCommand();
    public static int commandsExecuted = 0;

    public static void registerCommand(String name, ICommand cmd) {
        System.out.println("Registered new command " + name);
        commands.put(name, cmd);
    }

    public static void registerAlias(String aliasName, String cmdName) {
        System.out.println("Registered new alias " + aliasName + " for " + cmdName);
        commands.put(aliasName, commands.get(cmdName));
    }

    public static void prefixCalled(Guild guild, TextChannel channel, User invoker, Message message) {
        //String[] args = message.getRawContent().replace("\n", " ").split(" ");
        String[] args = commandToArguments(message.getRawContent());
        ICommand invoked = commands.getOrDefault(args[0].substring(frederikam.selfboat.SelfBoat.PREFIX.length()), defaultCmd);
        commandsExecuted++;
        if (invoked instanceof ICommandOwnerRestricted) {
            //This command is restricted to only Frederikam
            //Check if invoker is actually Frederikam
            if (!invoker.getId().equals(SelfBoat.OWNER_ID)) {
                channel.sendMessage(TextUtils.prefaceWithMention(invoker, " you are not allowed to use that command!"));
                return;
            }
        }
        try {
            invoked.onInvoke(guild, channel, invoker, message, args);
        } catch (Exception e) {
            TextUtils.handleException(e, channel, invoker);
        }
        message.deleteMessage();
    }

    public static String[] commandToArguments(String cmd) {
        ArrayList<String> a = new ArrayList<>();
        int argi = 0;
        boolean isInQuote = false;

        for (Character ch : cmd.toCharArray()) {
            if (Character.isWhitespace(ch) && isInQuote == false) {
                String arg = null;
                try {
                    arg = a.get(argi);
                } catch (IndexOutOfBoundsException e) {
                }
                if (arg != null) {
                    argi++;//On to the next arg
                }//else ignore

            } else if (ch.equals('"')) {
                isInQuote = !isInQuote;
            } else {
                a = writeToArg(a, argi, ch);
            }
        }

        String[] newA = new String[a.size()];
        int i = 0;
        for (String str : a) {
            newA[i] = str;
            i++;
        }

        return newA;
    }

    private static ArrayList<String> writeToArg(ArrayList<String> a, int argi, char ch) {
        String arg = null;
        try {
            arg = a.get(argi);
        } catch (IndexOutOfBoundsException e) {
        }
        if (arg == null) {
            a.add(argi, String.valueOf(ch));
        } else {
            a.set(argi, arg + ch);
        }
        
        return a;
    }
}
