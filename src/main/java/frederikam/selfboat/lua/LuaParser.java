/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frederikam.selfboat.lua;

import frederikam.selfboat.SelfBoat;
import frederikam.selfboat.util.LuaUtil;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;

/**
 *
 * @author Frederik
 */
public class LuaParser {
    
    public static Globals sandboxGlobals(HashMap<String, String> args) {
        Globals globals = new Globals();
        
        for(String k : args.keySet()){
            String v = args.get(k);
            
            globals.set(k, LuaString.valueOf(v));
        }
        
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

        globals.set("wait", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue time) {
                synchronized (Thread.currentThread()) {
                    try {
                        long timeStarted = System.currentTimeMillis();
                        Thread.currentThread().wait((int)(time.checkdouble() * 1000));
                        return LuaValue.valueOf(((double)System.currentTimeMillis()-(double)timeStarted)/1000);//Return elapsed time
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

                //System.out.println(args);

                for (int i = 1; i < args.narg() + 1; i++) {
                    newLine = newLine + " " + LuaUtil.tostring(args.arg(i));
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

    public static Outcome parseLua(String source, long timeout) throws InterruptedException, ExecutionException{
        return parseLua(source, timeout, new HashMap<>());
    }
    
    public static Outcome parseLua(String source, long timeout, HashMap<String, String> args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Outcome> future = executor.submit(new LuaTask(source, args));

        try {
            //System.out.println("Started..");
            return future.get(timeout, TimeUnit.MILLISECONDS);

            //System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            //System.out.println("Terminated!");
        }

        executor.shutdownNow();
        return new Outcome("", true);
    }

    public static class Outcome {

        public volatile String output;
        public volatile boolean timedOut;
        public volatile String luaError;

        public Outcome(String output, boolean timedOut) {
            this.output = output;
            this.timedOut = timedOut;
        }

        public Outcome(String output, boolean timedOut, String luaError) {
            this.output = output;
            this.timedOut = timedOut;
            this.luaError = luaError;
        }

    }

    static class LuaTask implements Callable<Outcome> {

        public volatile String source;
        public volatile HashMap<String, String> args;

        public LuaTask(String source, HashMap<String, String> args) {
            this.source = source;
            this.args = args;
        }

        @Override
        public Outcome call() throws Exception {
            try {
                Globals globals = sandboxGlobals(args);
                LuaValue chunk = globals.load(source);
                LuaValue returnValue = chunk.call();
                LuaValue outMsg = globals.get("msgout");
                String finalOutMsg;

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
                    return new Outcome("", false);
                }

                return new Outcome(finalOutMsg, false);
            } catch (LuaError e) {
                return new Outcome("", false, e.getMessage());
            }
        }
    }
}
