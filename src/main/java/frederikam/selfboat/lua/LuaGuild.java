package frederikam.selfboat.lua;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaGuild extends LuaTable {

    public Guild guild;
    public JDA jda;

    public LuaGuild(Guild guild) {
        super();
        this.guild = guild;
        jda = guild.getJDA();
        setmetatable(generateMeta());
    }

    private LuaTable generateMeta() {
        LuaTable meta = new LuaTable();

        
        
        meta.set("__index", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue t, LuaValue k) {
                if (k.checkjstring().equals("users")) {
                    LuaTable users =  new LuaTable();
                    int i = 1;
                    for(User usr : guild.getUsers()){
                        users.set(i, new LuaUser(usr));
                        i++;
                    }
                    return users;
                }
                return LuaValue.NIL;
            }
        });

        return meta;
    }

}
