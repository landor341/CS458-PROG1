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

        for (int i=0; i<args.length; i++) {
            myProgram.append(args[i]);
            System.out.println(myProgram.getCurrentWord().toHex());
        }

//        runTestSuite(myProgram);


    }

    private static void runTestSuite(MIPSProgram myProgram) {

        myProgram.append("add $t2, $s6, $s4");
        myProgram.append("add $at, $a2, $s2");
        myProgram.append("add $t7, $t6, $sp");
        myProgram.append("add $k0, $at, $s4");
        myProgram.append("add $t3, $t2, $t0");
        myProgram.append("addiu $s0, $s3, 0x6e");
        myProgram.append("addiu $t8, $zero, 0x74");
        myProgram.append("addiu $t3, $t2, 0x32");
        myProgram.append("addiu $k0, $v1, 0xffc8");
        myProgram.append("addiu $fp, $k0, 11");
        myProgram.append("and $t8, $a3, $a2");
        myProgram.append("and $s6, $s1, $s5");
        myProgram.append("and $t5, $v0, $gp");
        myProgram.append("and $s6, $gp, $ra");
        myProgram.append("and $s2, $sp, $t3");
        myProgram.append("andi $t4, $s2, 103");
        myProgram.append("andi $fp, $s7, -149");
        myProgram.append("andi $s0, $t7, 0xffa9");
        myProgram.append("andi $s2, $s5, 221");
        myProgram.append("andi $v1, $a0, 0xff8a");
        myProgram.append("beq $t9, $t7, 0xff50"); // TODO For BNE and BEQ he had broken data
        myProgram.append("beq $t6, $at, -116");
        myProgram.append("beq $a3, $a3, 0xd8");
        myProgram.append("beq $s5, $s0, 50# Comment");
        myProgram.append("beq $s0, $s6, 0xff4c");
        myProgram.append("bne $s1, $t3, -61");
        myProgram.append("bne $t6, $sp, 0xff64");
        myProgram.append("bne $t7, $t1, 0x22");
        myProgram.append("bne $t3, $a1, 0xff02");
        myProgram.append("bne $t1, $s4, 0xff4d");
        myProgram.append("j 0xc5");
        myProgram.append("j 0x48");
        myProgram.append(" j 0x15");
        myProgram.append(" j 0x53# Comment");
        myProgram.append(" j 0x2d");
        myProgram.append("lui $v1, 0xd3");
        myProgram.append("lui $s3, 0x49");
        myProgram.append("lui $t6, 0x85");
        myProgram.append("lui $t2, 0xc7");
        myProgram.append("lui $s7, 0xd2");
        myProgram.append("lw $a0, 133($t9)");
        myProgram.append("lw $t3, 43($k0)# Comment");
        myProgram.append("lw $v0, ($s6)");
        myProgram.append("lw $t3, ($a1)");
        myProgram.append("lw $k0, -118($ra)");
        myProgram.append("or $t0, $t2, $t0");
        myProgram.append("or $zero, $s4, $a0");
        myProgram.append("or $t1, $t9, $t7");
        myProgram.append("or $v1, $t8, $t8");
        myProgram.append("or $k0, $fp, $t0");
        myProgram.append("ori $a3, $t8, 0x5b");
        myProgram.append("ori $s7, $s6, -233");
        myProgram.append("ori $t2, $gp, 60");
        myProgram.append("ori $fp, $t6, 0xff23");
        myProgram.append("ori $zero, $fp, 0xffb5");
        myProgram.append("slt $t0, $t5, $gp");
        myProgram.append("slt $a1, $a0, $fp");
        myProgram.append("slt $v1, $gp, $t3");
        myProgram.append("slt $zero, $zero, $s1");
        myProgram.append("slt $k0, $sp, $t1");
        myProgram.append("sub $s5, $at, $sp");
        myProgram.append("sub $sp, $t2, $t8# Comment");
        myProgram.append("sub $s4, $sp, $a0");
        myProgram.append("sub $a1, $s5, $t7");
        myProgram.append("sub $s7, $s3, $s7");
        myProgram.append("sw $t6, ($s2)");
        myProgram.append("sw $t5, ($fp)");
        myProgram.append("sw $s2, ($t6)");
        myProgram.append("sw $at, 39($zero)# Comment");
        myProgram.append("sw $t5, -179($t3)");
        myProgram.append("syscall");

        String[] expectedOutput = {
                "02d45020", "00d20820", "01dd7820", "0034d020", "01485820",
                "2670006e", "24180074", "254b0032", "247affc8", "275e000b",
                "00e6c024", "0235b024", "005c6824", "039fb024", "03ab9024",
                "324c0067", "32feff6b", "31f0ffa9", "32b200dd", "3083ff8a",
                "124600bb", "1026ff8f", "106f00fd", "11c90026", "11f00090", // Expected to fail
                "16100028", "164b00a0", "142cffb9", "15770060", "1492ff67", // Expected to fail
                "080000c5", "08000048", "08000015", "08000053", "0800002d",
                "3c0300d3", "3c130049", "3c0e0085", "3c0a00c7", "3c1700d2",
                "8f240085", "8f4b002b", "8ec20000", "8cab0000", "8ffaff8a",
                "01484025", "02840025", "032f4825", "03181825", "03c8d025",
                "3707005b", "36d7ff17", "378a003c", "35deff23", "37c0ffb5",
                "01bc402a", "009e282a", "038b182a", "0011002a", "03a9d02a",
                "003da822", "0158e822", "03a4a022", "02af2822", "0277b822",
                "ae4e0000", "afcd0000", "add20000", "ac010027", "ad6dff4d",
                "0000000c"
        };

        Word result;
        int count = 0;
        while (myProgram.hasNextWord()) {
            result = myProgram.getCurrentWord();
            if (!result.toHex().equals(expectedOutput[count]))
                System.out.println("Expected: [" + expectedOutput[count] + "] " + "Actual: [" + result.toHex() + "]");
            count++;
        }
        System.out.println("TOTAL: " + count);
    }

}
