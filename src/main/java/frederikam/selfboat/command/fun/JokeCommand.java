package frederikam.selfboat.command.fun;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.util.HttpUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

public class JokeCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        try {
            String response = HttpUtils.httpGet("http://api.icndb.com/jokes/random");
            JSONObject object = new JSONObject(response);

            if (!"success".equals(object.getString("type"))) {
                throw new IOException("Couldn't gather joke ;|");
            }
            
            String joke = object.getJSONObject("value").getString("joke");
            String remainder = message.getContent().substring(args[0].length()).trim();
            
            if(message.getMentionedUsers().size() > 0){
                joke = joke.replaceAll("Chuck Norris", "<@"+message.getMentionedUsers().get(0).getId()+">");
            } else if (remainder.length() > 0){
                joke = joke.replaceAll("Chuck Norris", remainder);
            }
            
            joke = joke.replaceAll("&quot;", "\"");
            
            channel.sendMessage(joke);
        } catch (IOException ex) {
            Logger.getLogger(JokeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
