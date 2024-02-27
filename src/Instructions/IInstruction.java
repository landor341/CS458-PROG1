package Instructions;

import enums.OP;
import enums.REG;

public class IInstruction extends Word {
    // Format of I Instruction:
    //    31-26        25-21     20-16      15-0
    //    opcode      $rs         $rt        imm

    public IInstruction(OP o, String rs, String rt, String imm) {
        for (int i=0; i< o.opcode.length; i++) {
            this.bits[i] = o.opcode[i];
        }

        addReg(rs, 6);
        addReg(rt, 11);

        addImm(imm, 16);
    }
}
