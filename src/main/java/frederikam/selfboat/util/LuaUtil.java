package frederikam.selfboat.util;

import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.TOSTRING;
import static org.luaj.vm2.LuaValue.valueOf;

public class LuaUtil {

    public static LuaValue tostring(LuaValue arg) {
        LuaValue h = arg.metatag(TOSTRING);
        if (!h.isnil()) {
            return h.call(arg);
        }
        LuaValue v = arg.tostring();
        if (!v.isnil()) {
            return v;
        }
        return valueOf(arg.tojstring());
    }

}
