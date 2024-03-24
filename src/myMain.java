import Instructions.Word;
import mips.MIPSFileParser;
import mips.MIPSProgram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HexFormat;

// For now is a general top level class containing our main()
public class myMain {

    public static void main(String[] args) throws IOException {
        // There always be exactly one string in args for PROG1
        // Pass that string to an instance of our Program class and it should automatically find any instructions contained within it
        // and add it to it's instruction list. After that just print out the result of popping the next operation from our program.

        // TODO: What if a labels distance is greater than a single instruction can handle
        // TODO: File parser needs to have the ability to grab quotes to know how long to make the data section
        // TODO: la will break all of the relative addressing if it's passed a label with a relative value of less than 0x10000 (Should be fine since la will mostly be used for loading data which is always over 0x10000 away)
        try {
            MIPSProgram test = new MIPSProgram(args[0]);
            int count = 0;
            File curFile = new File(args[0]+".text");
            if (!curFile.exists()) curFile.createNewFile();
            FileWriter fileW = new FileWriter(curFile);
            for (Object w : test.getCode()) {
                fileW.append(((Word) w).toHex());
                fileW.append('\n');
            }

            count = 0;
            fileW.close();
            curFile = new File(args[0]+".data");
            fileW = new FileWriter(curFile);
            System.out.println("\n\nData: ");
            for (Object w: test.getData()) {
                fileW.append(((Word) w).toHex());
                fileW.append('\n');
            }
            fileW.close();
        } catch (Exception e) {throw e;}
    }

}
