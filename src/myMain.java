import Instructions.IInstruction;
import Instructions.Word;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// For now is a general top level class containing our main()
public class myMain {

    public static void main(String[] args) {
        // There always be exactly one string in args for PROG1
        // Pass that string to an instance of our Program class and it should automatically find any instructions contained within it
        // and add it to it's instruction list. After that just print out the result of popping the next operation from our program.

        MIPSProgram myProgram = new MIPSProgram();

        myProgram.append(args[0]);

        Word result = myProgram.getCurrentWord();
    }
}
