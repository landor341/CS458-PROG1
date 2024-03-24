package enums;

import Instructions.Word;
import mips.MIPSFileParser;
import mips.labelPosTracker;

// ENUMS for each supported operation. The opcode for R types if their funct code
public enum OP {
    //        opStr,        opAsBoolArray,                                   opType,    opRegFormat
    add("add", new boolean[]{true, false, false, false, false, false}, ITYPE.R, IFormat.rdRsRt), // 100000
    addu("addu", new boolean[]{true, false, false, false, false, true}, ITYPE.R, IFormat.rdRsRt), // 100000
    addiu("addiu", new boolean[]{false, false, true, false, false, true}, ITYPE.I, IFormat.rtRsImm), // 001000
    and("and", new boolean[]{true, false, false, true, false, false}, ITYPE.R, IFormat.rdRsRt), // 100100
    andi("andi", new boolean[]{false, false, true, true, false ,false}, ITYPE.I, IFormat.rtRsImm), // 001100
    beq("beq", new boolean[]{false, false, false, true, false, false}, ITYPE.I, IFormat.rsRtOff), // 000100
    bne("bne", new boolean[]{false, false, false, true, false, true}, ITYPE.I, IFormat.rsRtOff), // 000101
    PSEUDObne("PSEUDObne", new boolean[]{false, false, false, true, false, true}, ITYPE.I, IFormat.rsRtOff), // 000101
    j("j", new boolean[]{false, false, false, false, true, false}, ITYPE.J, IFormat.imm), // 000010
    addi("addi", new boolean[]{false, false, true, false, false, false}, ITYPE.I, IFormat.rtRsImm), // 001000
    lui("lui", new boolean[]{false, false, true, true, true, true}, ITYPE.I, IFormat.rtImm), // 001111
    lw("lw", new boolean[]{true, false, false, false, true, true}, ITYPE.I, IFormat.rtBaseOffset), // 100011
    or("or", new boolean[]{true, false, false, true, false, true}, ITYPE.R, IFormat.rdRsRt), // 100101
    ori("ori", new boolean[]{false, false, true, true, false, true}, ITYPE.I, IFormat.rtRsImm), // 001101
    slt("slt", new boolean[]{true, false, true, false, true, false}, ITYPE.R, IFormat.rdRsRt), // 101010
    sub("sub", new boolean[]{true, false, false, false, true, false}, ITYPE.R, IFormat.rdRsRt), // 100010
    slti("slti", new boolean[]{false, false, true, false, true, false}, ITYPE.I, IFormat.rsRtOff),
    sw("sw", new boolean[]{true, false, true, false, true, true}, ITYPE.I, IFormat.rtBaseOffset), // 101011
    syscall("syscall", new boolean[]{false, false, true, true, false, false}, ITYPE.SysCall, IFormat.syscall),
    nop("nop", new boolean[]{false, false, false, false, false, false, false}, null, null);

    public final String name;
    public final boolean[] opcode;
    public final ITYPE type;
    public final IFormat format;

    OP(String name, boolean[] opcode, ITYPE type, IFormat format) {
        this.name = name;
        this.opcode = opcode;
        this.type = type;
        this.format = format;
    }

    public static OP matchOp(String op) {
        for (OP o : OP.values())
            if (op.equals(o.name)) return o;
        return nop;
    }

    public Word parseInstruction(MIPSFileParser words, labelPosTracker p) {
        if (this == nop) return new Word(true);
        return this.format.parseInstruction(words, this, p);
    }

}

