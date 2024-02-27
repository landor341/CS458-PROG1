package Instructions;

import enums.OP;
import enums.REG;

public class RInstruction extends Word {
    // Format of R Instruction: (Opcode is always zero iirc)
    //    31-26        25-21     20-16      15-11     10-6      5-0
    //    opcode      $rs         $rt        $rd      shamt     funct

    public RInstruction (OP o, String rs, String rd, String rtOrShamt) {

        addReg(rs, 6);
        addReg(rtOrShamt, 11);
        addReg(rd, 16);


        // For R instructions the opcode is 0, the below represents the funct
        for (int i=0; i< o.opcode.length; i++) {
            this.bits[i+26] = o.opcode[i];
        }
    }
}
