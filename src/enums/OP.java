package enums;

// ENUMS for each supported operation. The opcode for R types if their funct code
public enum OP {
    add("add", new boolean[]{true, false, false, false, false, false}, ITYPE.R), // 100000
    addiu("addi", new boolean[]{false, false, true, false, false, false}, ITYPE.I), // 001000
    and("and", new boolean[]{true, false, false, true, false, false}, ITYPE.R), // 100100
    andi("andi", new boolean[]{false, false, true, true, false ,false}, ITYPE.I), // 001100
    beq("beq", new boolean[]{false, false, false, true, false, false}, ITYPE.I), // 000100
    bne("beq", new boolean[]{false, false, false, true, false, true}, ITYPE.I), // 000101
    j("j", new boolean[]{false, false, false, false, true, false}, ITYPE.J), // 000010
    lui("lui", new boolean[]{false, false, true, true, true, true}, ITYPE.I), // 001111
    lw("lw", new boolean[]{true, false, false, false, true, true}, ITYPE.R), // 100011
    or("or", new boolean[]{true, false, false, true, false, true}, ITYPE.R), // 100101
    ori("ori", new boolean[]{false, false, true, true, false, true}, ITYPE.I), // 001101
    slt("slt", new boolean[]{true, false, true, false, true, false}, ITYPE.R), // 101010
    sub("sub", new boolean[]{true, false, false, false, true, false}, ITYPE.R), // 100010
    sw("sw", new boolean[]{true, false, true, false, true, true}, ITYPE.I), // 101011
    syscall("syscall", new boolean[]{false, false, true, true, false, false}, ITYPE.SysCall);

    public String name;
    public boolean[] opcode;
    public ITYPE type;

    OP(String name, boolean[] opcode, ITYPE type) {
        this.name = name;
        this.opcode = opcode;
        this.type = type;
    }

    }
