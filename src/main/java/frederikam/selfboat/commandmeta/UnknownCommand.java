/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frederikam.selfboat.commandmeta;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.TextUtils;

/**
 *
 * @author frederik
 */
public class UnknownCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        //channel.sendMessage("<@"+invoker.getId()+"> unknown command \""+message.getContent()+"\"!");
        //channel.sendMessage(TextUtils.prefaceWithMention(invoker, " unknown command \""+message.getContent()+"\"!"));
    }
    
}
