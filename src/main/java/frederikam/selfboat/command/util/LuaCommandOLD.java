package frederikam.selfboat.command.util;

import frederikam.selfboat.commandmeta.CommandManager;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.commandmeta.ICommandOwnerRestricted;
import frederikam.selfboat.lua.LuaDiscordLib;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import frederikam.selfboat.util.TextUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseIoLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

public class LuaCommandOLD implements ICommand {

    public static final int MAX_COMPUTATION_TIME = 5;

    public static Globals sandboxGlobals() {
        Globals globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        //globals.load(new JseIoLib());
        globals.load(new JseOsLib());
        //globals.load(new LuajavaLib());
        globals.load(new LuaDiscordLib(SelfBoat.jda));

        globals.set("require", LuaValue.NIL);
        globals.set("load", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("rawset", LuaValue.NIL);
        globals.set("rawlen", LuaValue.NIL);
        globals.set("msgout", LuaString.valueOf(""));

        globals.set("wait", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue time) {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(time.checkint() * 1000);
                    } catch (InterruptedException ex) {
                        globals.get("error").call("wait() InterruptedException: \"" + ex.getMessage() + "\"");
                    }
                }
                return time;
            }
        });

        globals.set("print", new VarArgFunction() {

            @Override
            public Varargs invoke(Varargs args) {
                String newLine = "";

                System.out.println(args);

                for (int i = 1; i < args.narg() + 1; i++) {
                    newLine = newLine + " " + args.arg(i);
                }
                if (args.narg() > 0) {
                    newLine = newLine.substring(1);//Removes the first space
                }

                newLine = newLine + "\n";

                globals.set("msgout", LuaString.valueOf(globals.get("msgout") + newLine));

                return LuaValue.NIL;
            }

            /*@Override
            public Varargs invoke(Varargs args) {
                String newLine = "";

                System.out.println(args);

                for (int i = 1; i < args.narg() + 1; i++) {
                    newLine = newLine + " " + args.arg(i);
                }
                if (args.narg() > 0) {
                    newLine = newLine.substring(1);//Removes the first space
                }

                newLine = newLine + "\n";

                globals.set("msgout", LuaString.valueOf(globals.get("msgout") + newLine));

                return LuaValue.NIL;
            }*/
        });

        LuaValue osLib = globals.get("os");
        LuaTable newOsLib = new LuaTable();
        LuaValue k = LuaValue.NIL;
        while (true) {
            Varargs n = osLib.next(k);
            if ((k = n.arg1()).isnil()) {
                break;
            }
            LuaValue v = n.arg(2);
            if ("time".equals(k.toString()) || "date".equals(k.toString())) {
                //Whitelisted values
                newOsLib.set(k, v);
            }
        }

        globals.set("os", newOsLib);

        LoadState.install(globals);
        LuaC.install(globals);
        return globals;
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        String source = message.getContent().replaceFirst(args[0], "");

        Thread luaThread = new Thread() {

            @Override
            public void run() {
                Globals globals = sandboxGlobals();
                try {
                    LuaValue chunk = globals.load(source);
                    LuaValue returnValue = chunk.call();
                    LuaValue outMsg = globals.get("msgout");
                    String finalOutMsg = "";

                    if (LuaValue.EMPTYSTRING.equals(outMsg)) {
                        outMsg = LuaString.valueOf("");
                    }

                    if (!outMsg.equals(LuaString.valueOf("")) && !outMsg.toString().equals("nil") && !returnValue.isnil()) {
                        finalOutMsg = outMsg + "" + returnValue;
                    } else if (!outMsg.equals(LuaString.valueOf("")) && !outMsg.toString().equals("nil")) {
                        finalOutMsg = outMsg.toString();
                    } else if (!returnValue.isnil()) {
                        finalOutMsg = returnValue.toString();
                    } else {
                        return;
                    }

                    TextUtils.replyWithMention(channel, invoker, " " + finalOutMsg);

                } catch (LuaError e) {
                    TextUtils.replyWithMention(channel, invoker, " A Lua error occured :anger:\n```" + e.getMessage() + "```");
                }
            }
        };

        luaThread.start();

        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait(MAX_COMPUTATION_TIME * 1000);
            } catch (InterruptedException ex) {
                TextUtils.replyWithMention(channel, invoker, " InterruptedException " + ex.getMessage());
            }
        }

        synchronized (luaThread) {
            if (luaThread.isAlive()) {
                luaThread.stop();

                TextUtils.replyWithMention(channel, invoker, " Function timed out :anger: allowed computation time is " + MAX_COMPUTATION_TIME + " seconds.");
            }
        }
    }

}
