package frederikam.selfboat.lua;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaUser extends LuaTable {

    public User user;
    public JDA jda;

    public LuaUser(User usr) {
        super();
        user = usr;
        jda = usr.getJDA();
        setmetatable(generateMeta());
    }

    private LuaTable generateMeta() {
        LuaTable meta = new LuaTable();

        meta.set(INDEX, new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue t, LuaValue k) {
                if (k.checkjstring().equals("id")) {
                    return LuaString.valueOf(user.getId());
                }
                return LuaValue.NIL;
            }
        });

        meta.set(TOSTRING, new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg){//, LuaValue arg2) {
                return LuaString.valueOf(user.getUsername());
            }
        });

        return meta;
    }

}
