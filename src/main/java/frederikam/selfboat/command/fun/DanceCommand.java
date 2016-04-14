package frederikam.selfboat.command.fun;

import frederikam.selfboat.ChannelListener;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class DanceCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        Runnable func = new Runnable() {
            @Override
            public void run() {
                synchronized (channel) {
                    Message msg = channel.sendMessage('\u200b' + "\\o\\");
                    ChannelListener.messagesToDeleteIfIdDeleted.put(message.getId(), msg);
                    long start = System.currentTimeMillis();
                    try {
                        synchronized (this) {
                            while (start + 60000 > System.currentTimeMillis()) {
                                wait(1000);
                                msg = msg.updateMessage("/o/");
                                wait(1000);
                                msg = msg.updateMessage("\\o\\");
                            }
                        }
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };

        Thread thread = new Thread(func);
        thread.start();
    }
}
