/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frederikam.selfboat.command.util;

import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.ChannelListener;
import frederikam.selfboat.commandmeta.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

/**
 *
 * @author frederik
 */
public class SayCommand extends Command {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        String res = "";
        for (int i = 1; i < args.length; i++) {
            res = res+" "+args[i];
        }
        res = res.substring(1);
        Message myMsg = channel.sendMessage('\u200b' + res);
        
        ChannelListener.messagesToDeleteIfIdDeleted.put(message.getId(), myMsg);
    }
    
}
