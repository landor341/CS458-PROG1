package Instructions;

import enums.OP;

public class IInstruction extends Word {
    // Format of I Instruction:
    //    31-26        25-21     20-16      15-0
    //    opcode      $rs         $rt        imm

    public IInstruction(OP o, String rs, String rt, String imm) {
        // throw error if rs and rt don't map to REG enums and if imm isn't an integer or hex (ex. "56" or "0xa34f")
        System.out.println("IInstruction " + o.name + " " + rs + " " + rt + " " + imm); // TODO: Remove. Debug line
    }
}
