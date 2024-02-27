import Instructions.*;
import enums.IFormat;
import enums.ITYPE;
import enums.OP;
import enums.REG;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A program consists of a series of instructions. This class will be passed a stream of chars to search through
public class MIPSProgram {

    // An arraylist is probably the most efficient data structure here, we just need to be able to sequentially access and occasionally jump to known indexes.
    // Though technically this could cause an issue if you were to jump into the middle of a word in memory (unsure if this is possible
    private ArrayList<Word> code = new ArrayList<Word>();

    private final String commentPattern = "#[^\\n]*?\\n"; // Ex: # 1 comment :)
            private final String betweenWordsPattern = "(?:\\s|" + commentPattern + ")*"; // whitespace or comment pattern
            private final String nextWordPattern = "(\\(|\\)|,|[^\\s,#()]*)"; // A comma counts as a full word by itself. A word can't include whitespace or #'s. Any punctuation will count as part of a word (brackets, minus signs, etc)
            private final String remainingInputPattern = "([\\s\\S]*)"; //gets everything remaining
    private final Pattern getNextWord = Pattern.compile(betweenWordsPattern + nextWordPattern); // clears out leading comments/whitepsace. Group 1 is the next word, Group 2 is the remaining input

    int curLine = 0; // Corresponds to the key used in the arrayList. This is like the "address", each instruction will get loaded into the curLine address then increment it


    boolean append(String codeToAdd) {
        /*
            1. clear out comments and newlines before getting the next word. Start by identifying if the next word is a label or an instruciton then find parameters to match it. Repeat until through all text

            TODO: Handle semicolons, how to deal with larger files since instructions can be spread over multiple lines (Probably want to turn codeToAdd into some kind of stream)
         */
        Matcher nextWord = getNextWord.matcher(codeToAdd);

        while (nextWord.find()) {
            // Check if the next word is a label (It would end with a colon)

            // Otherwise assume it's a valid operation.
            for (OP o : OP.values()) {
                if (o.name.equals(nextWord.group(1))) { // If the code returns true here then you could throw an "Unknown operation error" here
                    if (o.type == ITYPE.R) nextWord = this.addOP(o, nextWord);
                    else if (o.type == ITYPE.I) nextWord = this.addOP(o, nextWord);
                    else if (o.type == ITYPE.J) nextWord = this.addOP(o, nextWord);
                    else if (o.type == ITYPE.SysCall) code.add(new SysCall());

                    break;
                }
            }
        }
        return false;
    }

    //TODO: for addXOP functions, make function getParameters(int n, Matcher nextWord) that automatically gets the next n parameters

    private Matcher addOP(OP o, Matcher nextWord) {
        if (o.format == IFormat.rdRsRt) {
            // search for rd then rt, rs, and shamt
            nextWord.find();

            String rd = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();

            String rs = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();

            String rtOrShamt = nextWord.group(1);

            code.add(new RInstruction(o, rs, rd, rtOrShamt));
        }
        else if (o.format == IFormat.imm) {
            // search for addr
            nextWord.find();
            String addr = nextWord.group(1);
            code.add(new JInstruction(o, addr));
        }
        else if (o.format == IFormat.rtBaseOffset) {
            // rt, offset(base)    offset might be nothing
            nextWord.find();

            String rt = nextWord.group(1);
            String offset = "0";

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();

            if (!nextWord.group(1).equals("(")) {
                offset = nextWord.group(1);
                nextWord.find();
                if (!nextWord.group(1).equals("(")) throw new IllegalStateException();
            }

            nextWord.find();
            String base = nextWord.group(1);
            code.add(new IInstruction(o, base, rt, offset));

            nextWord.find();
            if(!nextWord.group(1).equals(")")) throw new IllegalStateException();
        }
        else if (o.format == IFormat.rsRtOff) {
            nextWord.find();

            String rs = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();

            String rt = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();
            String offset = nextWord.group(1);
            code.add(new IInstruction(o, rs, rt, offset));
        }
        else if (o.format == IFormat.rtImm) {
            nextWord.find();

            String rt = nextWord.group(1);
            String second = "$zero";

            String imm;

            nextWord.find();
            if (nextWord.group(1).equals(",")) {
                nextWord.find();
                imm = nextWord.group(1);
                code.add(new IInstruction(o, second, rt, imm));
            }
        }
        else if (o.format == IFormat.rtRsImm) {
            nextWord.find();

            String rt = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();

            String rs = nextWord.group(1);

            nextWord.find();
            if (!nextWord.group(1).equals(",")) throw new IllegalStateException();
            nextWord.find();
            String offset = nextWord.group(1);
            code.add(new IInstruction(o, rs, rt, offset));

        }
        return nextWord;
    }

    public Word getCurrentWord() {
        return code.get(curLine++);
    }

    public boolean hasNextWord() {
        return curLine < code.size();
    }

}
