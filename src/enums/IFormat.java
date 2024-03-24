package enums;

import Instructions.*;
import mips.MIPSFileParser;
import mips.MIPSProgram;
import mips.TriFunction;
import mips.labelPosTracker;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum IFormat {
    rtBaseOffset((w, o, l) -> {
        try {
            // rt, offset(base)    offset might be nothing
            String rt = w.getNextWord();
            String offset = "0";

            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            if (!w.getNextWord().equals("(")) {
                offset = w.getNextWord();
                if (!w.getNextWord().equals("(")) throw new IllegalStateException();
            }
            String base = w.getNextWord();
            if (!w.getNextWord().equals(")")) throw new IllegalStateException();
            return new IInstruction(o, base, rt, offset, l);
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    rtRsImm((w, o, l) -> {
        try {
            String rt = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            String rs = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            return (new IInstruction(o, rs, rt, w.getNextWord(), l));
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    rsRtOff((w, o, l) -> {
        try {
            String rs = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            String rt = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            return (new IInstruction(o, rs, rt, w.getNextWord(), l));
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    rtImm((w, o, l) -> {
        try {
            String rt = w.getNextWord();
            String second = "$zero";
            if (w.getNextWord().equals(",")) throw new IllegalStateException();
            return (new IInstruction(o, second, rt, w.getNextWord(), l));
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    rdRsRt((w, o, l) -> {
        try {
            String rd = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            String rs = w.getNextWord();
            if (!w.getNextWord().equals(",")) throw new IllegalStateException();
            String rtOrShamt = w.getNextWord();

            return (new RInstruction(o, rs, rd, rtOrShamt));
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    imm((w, o,l) -> {
        try {
            return (new JInstruction(o, w.getNextWord(), l));
        } catch (IOException e) {
            System.out.println(e);
            return new Word(true);
        }
    }),
    syscall((w, o, l) -> new SysCall());

    private final TriFunction<MIPSFileParser, OP, labelPosTracker, Word> parse;

    IFormat(TriFunction<MIPSFileParser, OP, labelPosTracker, Word> parseInstruction) {
        parse = parseInstruction;
    }

    public Word parseInstruction(MIPSFileParser words, OP operator, labelPosTracker p) {
        return parse.apply(words, operator, p);
    }
}
