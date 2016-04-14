package frederikam.selfboat.command.fun;

import frederikam.selfboat.ChannelListener;
import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import frederikam.selfboat.util.TextUtils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;

public class AudioTrollCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel c, User invoker, Message message, String[] args) {
        String arg = "";
        for (int i = 1; i < args.length; i++) {
            arg = arg+" "+args[i];
        }
        arg = arg.substring(1);
        
        JDA JDA = c.getJDA();
        List<VoiceChannel> channels = guild.getVoiceChannels();
        VoiceChannel foundChannel = null;
        for (VoiceChannel ch : channels) {
            if (ch.getName().equalsIgnoreCase(arg)) {
                foundChannel = ch;
                break;
            }
        }
        if (foundChannel == null) {
            TextUtils.replyWithMention(c, invoker, " Couldn't find channel \"" + arg + "\"");
        } else {
            ChannelListener.toRunOnConnectingToVoice.put(foundChannel, onConnected);
            JDA.getAudioManager().openAudioConnection(foundChannel);
        }
    }

    public static Runnable onConnected = new Runnable() {
        @Override
        public synchronized void run() {
            JDA JDA = SelfBoat.jda;
            AudioManager am = JDA.getAudioManager();
            VoiceChannel ch = am.getConnectedChannel();
            try {
                URL dir_url = ClassLoader.getSystemResource("Troll.mp3");
                System.out.println(dir_url);
                File audioFile = new File(dir_url.toURI());
                
                FilePlayer player = new FilePlayer(audioFile);
                am.setSendingHandler(player);
                player.play();
                //synchronized (Thread.currentThread()) {
                    while (player.isPlaying()) {
                        wait(200);
                    }
                //}
                System.out.println("Audio ended");
            } catch (IOException | UnsupportedAudioFileException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            } finally {
                if(am.getConnectedChannel() == ch || am.getConnectedChannel() == null){
                    am.closeAudioConnection();
                }
            }
        }
    };
}
