package frederikam.selfboat;

import frederikam.selfboat.command.fun.AudioTrollCommand;
import frederikam.selfboat.command.fun.DanceCommand;
import frederikam.selfboat.command.util.AvatarCommand;
import frederikam.selfboat.command.util.BrainfuckCommand;
import frederikam.selfboat.commandmeta.CommandManager;
import frederikam.selfboat.command.maintenance.DBGetCommand;
import frederikam.selfboat.command.fun.EndTrollCommand;
import frederikam.selfboat.command.maintenance.ExitCommand;
import frederikam.selfboat.command.util.FindCommand;
import frederikam.selfboat.command.fun.JokeCommand;
import frederikam.selfboat.command.fun.LeetCommand;
import frederikam.selfboat.command.maintenance.EvalCommand;
import frederikam.selfboat.command.util.LuaCommand;
import frederikam.selfboat.command.maintenance.RestartCommand;
import frederikam.selfboat.command.util.SayCommand;
import frederikam.selfboat.command.maintenance.TestCommand;
import frederikam.selfboat.command.maintenance.UptimeCommand;
import frederikam.jca.JCA;
import frederikam.jca.JCABuilder;
import frederikam.selfboat.command.fun.RandomEmojiCommand;
import frederikam.selfboat.command.fun.TextCommand;
import frederikam.selfboat.command.util.ClearCommand;
import java.io.InputStream;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

public class SelfBoat {

    //public static final boolean IS_BETA = "Windows 10".equals(System.getProperty("os.name"));
    public static volatile JDA jda;
    public static JCA jca;
    public static final String PREFIX = "<<";
    public static final String OWNER_ID = "81011298891993088";
    public static final long START_TIME = System.currentTimeMillis();
    //public static final String ACCOUNT_EMAIL_KEY = IS_BETA ? "emailBeta" : "emailProduction";
    //public static final String ACCOUNT_PASSWORD_KEY = IS_BETA ? "passwordBeta" : "passwordProduction";
    //public static String accountEmail = IS_BETA ? "frederikmikkelsen2@outlook.com" : "frederikmikkelsen@outlook.com";
    //private static String accountPassword;
    public static String mashapeKey;
    public static String myUserId = "";
    public static volatile User myUser;

    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException {
        //Load credentials file
        InputStream is = new SelfBoat().getClass().getClassLoader().getResourceAsStream("credentials.json");
        Scanner scanner = new Scanner(is);
        JSONObject credsjson = new JSONObject(scanner.useDelimiter("\\A").next());
        
        String accountEmail = credsjson.getString("email");
        String accountPassword = credsjson.getString("password");
        //accountToken = credsjson.getString(ACCOUNT_TOKEN_KEY);
        mashapeKey = credsjson.getString("mashapeKey");
        String cbUser = credsjson.getString("cbUser");
        String cbKey = credsjson.getString("cbKey");
        
        if(credsjson.has("scopePasswords")){
            JSONObject scopePasswords = credsjson.getJSONObject("scopePasswords");
            for (String k : scopePasswords.keySet()){
                scopePasswords.put(k, scopePasswords.getString(k));
            }
        }
        
        scanner.close();
        
        frederikam.selfboat.util.HttpUtils.init();
        jda = new JDABuilder().addListener(new ChannelListener()).setEmail(accountEmail).setPassword(accountPassword).buildAsync();
        System.out.println("JDA version:\t"+JDAInfo.VERSION);
        
        //Initialise JCA
        jca = new JCABuilder().setKey(cbKey).setUser(cbUser).buildBlocking();
    }

    static void init() {
        //if (IS_BETA) {
        //    helpMsg = helpMsg + "\n\n**This is the beta version of Fredboat. Are you sure you are not looking for the non-beta version \"FredBoat\"?**";
        //}

        for (Guild guild : jda.getGuilds()) {
            System.out.println(guild.getName());

            for (TextChannel channel : guild.getTextChannels()) {
                System.out.println("\t" + channel.getName());
            }
        }

        myUserId = jda.getSelfInfo().getId();
        myUser = jda.getUserById(myUserId);

        //Commands
        //CommandManager.registerCommand("help", new HelpCommand());
        CommandManager.registerCommand("dbget", new DBGetCommand());
        CommandManager.registerCommand("say", new SayCommand());
        CommandManager.registerCommand("uptime", new UptimeCommand());
        CommandManager.registerAlias("stats", "uptime");
        CommandManager.registerCommand("exit", new ExitCommand());
        CommandManager.registerCommand("avatar", new AvatarCommand());
        CommandManager.registerCommand("test", new TestCommand());
        CommandManager.registerCommand("lua", new LuaCommand());
        CommandManager.registerCommand("brainfuck", new BrainfuckCommand());
        CommandManager.registerCommand("joke", new JokeCommand());
        CommandManager.registerCommand("leet", new LeetCommand());
        CommandManager.registerAlias("1337", "leet");
        CommandManager.registerAlias("l33t", "leet");
        CommandManager.registerCommand("troll", new AudioTrollCommand());
        CommandManager.registerCommand("endtroll", new EndTrollCommand());
        CommandManager.registerCommand("restart", new RestartCommand());
        CommandManager.registerCommand("find", new FindCommand());
        CommandManager.registerCommand("dance", new DanceCommand());
        CommandManager.registerCommand("eval", new EvalCommand());
        
        CommandManager.registerCommand("s", new TextCommand("¯\\_(ツ)_/¯"));
        CommandManager.registerCommand("random", new RandomEmojiCommand());
        CommandManager.registerCommand("clear", new ClearCommand());
        CommandManager.registerAlias("flush", "clear");
        CommandManager.registerCommand("lenny", new TextCommand("( ͡° ͜ʖ ͡°)"));
    }
}
