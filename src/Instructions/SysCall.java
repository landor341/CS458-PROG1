package Instructions;

import enums.OP;

// SysCall has a unique word format so it gets it's own class
public class SysCall extends Word {
    public SysCall() {
        for (int i=0; i< OP.syscall.opcode.length; i++) {
            this.bits[i+26] = OP.syscall.opcode[i];
        }
    }
}
