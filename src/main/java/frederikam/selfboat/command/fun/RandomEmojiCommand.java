package frederikam.selfboat.command.fun;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.util.Emoji;
import java.util.ArrayList;
import java.util.Random;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class RandomEmojiCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        ArrayList<Emoji> emojis = new ArrayList<>();
        for(Emoji e : Emoji.values()){
            emojis.add(e);
        }
        channel.sendMessage(emojis.get(new Random().nextInt(emojis.size())).getTag());
    }
    
}
