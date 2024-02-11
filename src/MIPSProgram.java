import Instructions.SysCall;
import Instructions.Word;
import enums.ITYPE;
import enums.OP;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A program consists of a series of instructions. This class will be passed a stream of chars to search through
public class MIPSProgram {

    // An arraylist is probably the most efficient data structure here, we just need to be able to sequentially access and occasionally jump to known indexes.
    // Though technically this could cause an issue if you were to jump into the middle of a word in memory (unsure if this is possible
    ArrayList<Word> code = new ArrayList<Word>();

    int curLine = 0; // Corresponds to the key used in the arrayList. This is like the "address", each instruction will get loaded into the curLine address then increment it 


    boolean append(String codeToAdd) {
        /*
            If a word ends with a colon then it's a label
            If a word starts with a $ then it's a register
            If there is a # then nothing is read until a new line since those are comments

            When looking for words, the # and ; character can be the character immediately after (it would be treated like whitespace)
            commas separate registers/imms/labels

            1. Find comments and blank lines
            2. find a label (if there is a label, map it to curLine and continue)
            3. Find a word (and map it to one of the OP enum values)
                a. depending on its ITYPE type look for either a
                    1. register
                    2. immediate value
                    3. label
                    always look for commas and comments or newlines in between parameters

            TODO: Handle semicolons, how to deal with larger files since instructions can be spread over multiple lines (Probably want to turn codeToAdd into some kind of stream)
         */
        String commentPattern = "#[^\\n]*?\\n", // Ex: # 1 comment :)
                betweenWords = "(?:\\s|" + commentPattern + ")*", // whitespace or comment pattern
                nextRegister = "($\\w*?)", // Ex: $t0
                nextOP = "([a-zA-Z]*)",
                nextImmValue = "(0x[\\da-fA-F]+?)|([\\d]+?)", // Ex: 0x2a  or   55   TODO: labels
                remainingInput = "([\\s\\S]*)"; //gets everything after whatevers found to continue searching from that point
        Pattern goToNextWord = Pattern.compile(betweenWords + remainingInput),
                getNextOP = Pattern.compile(nextOP + remainingInput),
                getNextRegister = Pattern.compile(nextRegister + remainingInput);

        String curCode = codeToAdd;

        while (!curCode.isEmpty()) {
            curCode = goToNextWord.matcher(curCode).group(1);

            // Check if the next word is a label (It would end with an apostrophe)

            // Otherwise assume it's a valid operation. This will break everything if the word has a non a-z character in it
            Matcher nextOp = getNextOP.matcher(curCode); // unary based indexing :(
            curCode = nextOp.group(2);

            for (OP o : OP.values()) {
                if (o.name.equals(nextOp.group(1))) {
                    if (o.type == ITYPE.R) {
                        // search for rs, rt, rd, and sham
                    } else if (o.type == ITYPE.I) {
                        // search for rs, rt, and IMM
                    } else if (o.type == ITYPE.J) {
                        // search for addr
                    } else if (o.type == ITYPE.SysCall) {
                        code.add(new SysCall());
                    }
                    break;
                }
            }

        }


        return false;
    }

    Word getCurrentWord() {
        return code.get(curLine);
    }

}
