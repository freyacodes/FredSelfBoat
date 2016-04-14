package frederikam.selfboat.command.maintenance;

import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import frederikam.selfboat.util.TextUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class EvalCommand extends Command implements ICommandOwnerRestricted {

    //Thanks Dinos!
    private ScriptEngine engine;

    public EvalCommand() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");

        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User author, Message message, String[] args) {
        JDA jda = guild.getJDA();
        String msg = message.getContent();

        channel.sendTyping();

        final String source = message.getRawContent().substring(args[0].length() + 1);

        engine.put("jda", jda);
        engine.put("api", jda);
        engine.put("channel", channel);
        engine.put("author", author);
        engine.put("bot", SelfBoat.myUser);
        engine.put("message", message);
        engine.put("guild", guild);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = service.schedule(() -> {

            Object out = null;
            try {
                out = engine.eval(
                        "(function() {"
                        + "with (imports) {\n" + source + "\n}"
                        + "})();");

            } catch (Exception ex) {
                channel.sendMessage("`"+ex.getMessage()+"`");
                return;
            }

            String outputS;
            if (out == null) {
                outputS = ":ok_hand::skin-tone-3:";
            } else if (out.toString().contains("\n")) {
                outputS = "\nEval: ```\n" + out.toString() + "```";
            } else {
                outputS = "\nEval: `" + out.toString() + "`";
            }

            channel.sendMessage("```java\n"+source+"```" + "\n" + outputS);

        }, 0, TimeUnit.MILLISECONDS);

        Thread script = new Thread("Eval") {
            @Override
            public void run() {
                try {
                    future.get(10, TimeUnit.SECONDS);

                } catch (TimeoutException ex) {
                    future.cancel(true);
                    channel.sendMessage("Task exceeded time limit.");
                } catch (Exception ex) {
                    channel.sendMessage("`"+ex.getMessage()+"`");
                }
            }
        };
        script.start();
    }
}
