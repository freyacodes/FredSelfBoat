/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frederikam.selfboat;

import frederikam.selfboat.commandmeta.CommandManager;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.ReconnectedEvent;
import net.dv8tion.jda.events.audio.AudioConnectEvent;
import net.dv8tion.jda.events.message.MessageDeleteEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringEscapeUtils;

public class ChannelListener extends ListenerAdapter {

    public static HashMap<String, Message> messagesToDeleteIfIdDeleted = new HashMap<>();
    public static HashMap<VoiceChannel, Runnable> toRunOnConnectingToVoice = new HashMap<>();
    public static User lastUserToReceiveHelp;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().equals(SelfBoat.myUser)){
            return;
        }
        
        if (event.getMessage().getContent().length() < SelfBoat.PREFIX.length()) {
            return;
        }

        if (event.getMessage().getContent().substring(0, frederikam.selfboat.SelfBoat.PREFIX.length()).equals(frederikam.selfboat.SelfBoat.PREFIX)) {
            System.out.println(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
            CommandManager.prefixCalled(event.getGuild(), event.getTextChannel(), event.getAuthor(), event.getMessage());
        }
    }

    public void cleverbotTalk(User user, TextChannel channel, String question) {
        CommandManager.commandsExecuted++;

        //Clerverbot integration
        String response = SelfBoat.jca.getResponse(question);
        response = user.getUsername() + ": " + StringEscapeUtils.unescapeHtml4(response);
        channel.sendMessage(response);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        /*if (messagesToDeleteIfIdDeleted.containsKey(event.getMessageId())) {
            messagesToDeleteIfIdDeleted.get(event.getMessageId()).deleteMessage();
        }*/
    }

    /*@Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        //Ignore self
        if (event.getAuthor().getUsername().equals(event.getJDA().getSelfInfo().getUsername())) {
            return;
        }

        //Ignore invites (handled elsewhere)
        if (event.getMessage().getContent().contains("discord.gg")) {
            return;
        }

        if (event.getAuthor() == lastUserToReceiveHelp) {
            //Ignore, just got help!
            return;
        }

        event.getChannel().sendMessage(SelfBoat.helpMsg);
        lastUserToReceiveHelp = event.getAuthor();
    }*/

    /*@Override
    public void onInviteReceived(InviteReceivedEvent event) {
        if (event.getMessage().isPrivate()) {
            event.getAuthor().getPrivateChannel().sendMessage("Sorry! Since the release of the official API, registered bots must now be invited by someone with Manage **Server permissions**. If you have permissions, you can invite me at:\n"
                    + "https://discordapp.com/oauth2/authorize?&client_id=" + SelfBoat.CLIENT_ID + "&scope=bot");
            /*
            //System.out.println(event.getInvite().getUrl());
            //InviteUtil.join(event.getInvite(), FredBoat.jda);
            Guild guild = null;
            try {
                guild = FredBoat.jda.getGuildById(event.getInvite().getGuildId());
            } catch (NullPointerException ex) {
                event.getAuthor().getPrivateChannel().sendMessage("That invite is not valid!");
                return;
            }

            boolean isNotInGuild = true;

            if (isNotInGuild) {
                event.getAuthor().getPrivateChannel().sendMessage("Invite accepted!");
                InviteUtil.join(event.getInvite(), FredBoat.jda, null);
            } else {
                event.getAuthor().getPrivateChannel().sendMessage("Already in that channel!");
            }
             *//*
        }
    }*/

    public HashMap<String, ArrayList<Integer>> recentTableFlips = new HashMap<>();

    public void pruneRecentTableflips(Guild guild, int seconds) {
        ArrayList<Integer> recent = recentTableFlips.containsKey(guild.getId()) ? recentTableFlips.get(guild.getId()) : new ArrayList<>();
        for (int time : recent) {

        }
    }

    public void getRecentTableflips(Guild guild, int seconds) {

    }

    public void tableflip(MessageReceivedEvent event) {
        //System.out.println(event.getGuild().getName() + " \t " + event.getAuthor().getUsername() + " \t " + event.getMessage().getRawContent());
        //event.getChannel().sendMessage("┬─┬﻿ ノ( ゜-゜ノ)");
    }

    @Override
    public void onReady(ReadyEvent event) {
        SelfBoat.init();
        //jda.getAccountManager().setGame("Say ;;help");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        //jda.getAccountManager().setGame("Say ;;help");
    }

    public static Runnable onUnrequestedConnection = new Runnable() {
        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    @Override
    public void onAudioConnect(AudioConnectEvent event) {
        Runnable run = toRunOnConnectingToVoice.getOrDefault(event.getConnectedChannel(), onUnrequestedConnection);
        run.run();
    }

}
