package Instructions;

import enums.OP;

public class JInstruction extends Word {
    // Format of J Instruction:
    //    31-26        25-0
    //    opcode       address
    // On instantiation make address an optional field since if it uses a label we might not know what address to set it to yet

    public JInstruction(OP o, String address) {
        for (int i=0; i< o.opcode.length; i++) {
            this.bits[i] = o.opcode[i];
        }

        addImm(address, 6);
    }
}
