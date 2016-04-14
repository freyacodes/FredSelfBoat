package frederikam.selfboat.command.maintenance;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import frederikam.selfboat.util.TextUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 *
 * @author frederik
 */
public class RestartCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        channel.sendMessage(TextUtils.prefaceWithMention(invoker, " Restarting.."));
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(RestartCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            
            /* is it a jar file? */
            if (!currentJar.getName().endsWith(".jar")) {
                throw new RuntimeException("Cannot restart in an IDE environment!");
            }
            
            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());
            
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            channel.sendMessage(TextUtils.prefaceWithMention(invoker, " New process started, ending this proces."));
            System.exit(0);
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace(System.out);
            throw new RuntimeException(ex);
        }
    }

}
