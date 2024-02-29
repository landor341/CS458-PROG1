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
    // Though technically this wouldn't be compatible if you were able to jump directly into the middle of a word in memory (I don't believe it's possible to set the PC like that)
    private ArrayList<Word> code = new ArrayList<Word>();

    // Regex DEFINITELY are not a very efficient implementation but I hate writing string parsers
    private final String commentPattern = "#[^\\n]*?\\n"; // Ex: # 1 comment :)
            private final String betweenWordsPattern = "(?:\\s|" + commentPattern + ")*"; // whitespace or comment pattern
            private final String nextWordPattern = "(\\(|\\)|,|[^\\s,#()]*)"; // commas and parenthesis count full words. A word can't include whitespace, commas, parenthesis, or #'s. Any other punctuation will count as part of a word (brackets, minus signs, etc)
    private final Pattern getNextWord = Pattern.compile(betweenWordsPattern + nextWordPattern); // clears out leading comments/whitepsace. Group 1 is the next word

    int curLine = 0; // Corresponds to the key used in the arrayList. This is like the "address", each instruction will get loaded into the curLine address then increment it


    boolean append(String codeToAdd) {
        Matcher nextWord = getNextWord.matcher(codeToAdd);

        while (nextWord.find()) {
            // Check if the next word is a label (It would end with a colon)
            // TODO: Label functionality. Create a hashmap to store the words that need to be returned to.

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
