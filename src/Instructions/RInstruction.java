package Instructions;

import enums.OP;

public class RInstruction extends Word {
    // Format of R Instruction: (Opcode is always zero iirc)
    //    31-26        25-21     20-16      15-11     10-6      5-0
    //    opcode      $rs         $rt        $rd      shamt     funct

    public RInstruction (OP o, String rt, String rd, String rsOrShamt) {
        // Throw error is rs, rt, and rdOrShamt don't map to valid REG enums
        System.out.println("RInstruction " + o.name + " " + rt + " " + rd + " " + rsOrShamt); // TODO: Remove. Debug line
    }
}
