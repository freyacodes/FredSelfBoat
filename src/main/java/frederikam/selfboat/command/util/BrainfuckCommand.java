package frederikam.selfboat.command.util;

import frederikam.selfboat.commandmeta.Command;
import frederikam.selfboat.commandmeta.ICommand;
import frederikam.selfboat.util.BrainfuckException;
import frederikam.selfboat.util.TextUtils;
import java.nio.ByteBuffer;
import java.util.*;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public class BrainfuckCommand extends Command {

    ByteBuffer bytes = null;
    char[] code;
    public static final int MAX_CYCLE_COUNT = 10000;

    public String process(String input) {
        int data = 0;
        char[] inChars = input.toCharArray();
        int inChar = 0;
        StringBuilder output = new StringBuilder();
        int cycleCount = 0;
        for (int instruction = 0; instruction < code.length; ++instruction) {
            cycleCount++;
            if (cycleCount > MAX_CYCLE_COUNT) {
                throw new BrainfuckException("Program exceeded the maximum cycle count of " + MAX_CYCLE_COUNT);
            }
            char command = code[instruction];
            switch (command) {
                case '>':
                    ++data;
                    break;
                case '<':
                    --data;
                    if(data < 0){
                        throw new BrainfuckException("Data pointer out of bounds: "+data);
                    }
                    break;
                case '+':
                    bytes.put(data, (byte) (bytes.get(data) + 1));
                    break;
                case '-':
                    bytes.put(data, (byte) (bytes.get(data) - 1));
                    break;
                case '.':
                    output.append((char) bytes.get(data));
                    break;
                case ',':
                    try {
                        bytes.put(data, (byte) inChars[inChar++]);
                        break;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new BrainfuckException("Input out of bounds at position: " + (inChar - 1), ex);
                    }
                case '[':
                    if (bytes.get(data) == 0) {
                        int depth = 1;
                        do {
                            command = code[++instruction];
                            if (command == '[') {
                                ++depth;
                            } else if (command == ']') {
                                --depth;
                            }
                        } while (depth > 0);
                    }
                    break;
                case ']':
                    if (bytes.get(data) != 0) {
                        int depth = -1;
                        do {
                            command = code[--instruction];
                            if (command == '[') {
                                ++depth;
                            } else if (command == ']') {
                                --depth;
                            }
                        } while (depth < 0);
                    }
                    break;
            } // switch (command)
        }
        return output.toString();
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        code = message.getContent().replaceFirst(args[0], "").toCharArray();
        bytes = ByteBuffer.allocateDirect(1024 * 1024 * 8);
        String inputArg = "";

        try {
            inputArg = args[2];
        } catch (Exception e) {

        }

        inputArg = inputArg.replaceAll("ZERO", String.valueOf((char) 0));

        String out = process(inputArg);
        //TextUtils.replyWithMention(channel, invoker, " " + out);
        String out2 = "";
        for (char c : out.toCharArray()) {
            int sh = (short) c;
            out2 = out2 + "," + sh;
        }
        try {
            TextUtils.replyWithMention(channel, invoker, " " + out + "\n-------\n" + out2.substring(1));
        } catch (IndexOutOfBoundsException ex) {
TextUtils.replyWithMention(channel, invoker, " There was no output");
        }
    }

}
