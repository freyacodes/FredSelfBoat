/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frederikam.selfboat.command.fun;

import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.ChannelListener;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.util.HttpUtils;
import frederikam.selfboat.util.TextUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

/**
 *
 * @author frederik
 */
public class LeetCommand extends Command implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        String res = "";
        channel.sendTyping();
        for (int i = 1; i < args.length; i++) {
            res = res+" "+args[i];
        }
        res = res.substring(1);
        try {
            HashMap<String, String> headers = new HashMap<>();
            //headers.put("Accept", "text/plain");
            res = HttpUtils.httpGetMashape("https://montanaflynn-l33t-sp34k.p.mashape.com/encode?text=" + URLEncoder.encode(res, "UTF-8").replace("+", "%20"), headers);
        } catch (IOException ex) {
            Message myMsg = TextUtils.replyWithMention(channel, invoker, " Could not connect to API! "+ex.getMessage());
            return;
        }
        Message myMsg = channel.sendMessage(res);
        
        ChannelListener.messagesToDeleteIfIdDeleted.put(message.getId(), myMsg);
    }
    
}
