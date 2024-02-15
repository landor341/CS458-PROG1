package Instructions;

import enums.OP;

public class JInstruction extends Word {
    // Format of J Instruction:
    //    31-26        25-0
    //    opcode       address
    // On instantiation make address an optional field since if it uses a label we might not know what address to set it to yet

    public JInstruction(OP o, String address) {
        // throw error if address isn't valid
        System.out.println("JInstruction " + o.name + " " + address); // TODO: Remove. Debug line
    }
}
