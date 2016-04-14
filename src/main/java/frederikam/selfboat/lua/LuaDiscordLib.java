package frederikam.selfboat.lua;

import net.dv8tion.jda.JDA;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Frederik
 */
public class LuaDiscordLib extends TwoArgFunction {

    public static JDA jda;

    public LuaDiscordLib(JDA jda) {
        this.jda = jda;
    }
    
    /**
     * Perform one-time initialiasation on the library by creating a table
     * containing the library functions, adding that table to the supplied
     * environment, adding the table to package.loaded, and returning table as
     * the return value.
     *
     * @param modname the module name supplied if this is loaded via 'require'.
     * @param env the environment to load into, which must be a Globals
     * instance.
     */
    
    
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable discord = new LuaTable();
        env.set("discord", discord);

        LuaGuild thisChannel = new LuaGuild(jda.getGuildById(env.get("guildId").checkjstring()));
        discord.set("guild", thisChannel);
        
        return discord;
    }
}
