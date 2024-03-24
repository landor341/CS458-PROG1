package enums;

import Instructions.IInstruction;
import Instructions.JInstruction;
import Instructions.RInstruction;
import Instructions.Word;
import mips.MIPSFileParser;
import mips.labelPosTracker;

import java.io.IOException;
import java.util.HexFormat;

public enum PSUEDOOP {
    li("li"), // 1
    la("la"), //2
    move("move"), //1
    blt("blt"); //2

    public String name;

    PSUEDOOP(String name) {
        this.name = name;
    }

    public Word[] parseInstruction(MIPSFileParser words, labelPosTracker pos) throws IOException {
        try {
            if (this == move) {
                String rt = words.getNextWord();
                if (!words.getNextWord().equals(",")) throw new IOException();
                String rd = words.getNextWord();
                return new Word[] {new RInstruction(OP.addu, "$0", rt, rd)};
            }
            if (this == li) {
                String rt = words.getNextWord();
                if (!words.getNextWord().equals(",")) throw new IOException();
                String imm = words.getNextWord();

                return new Word[] {new IInstruction(OP.addiu, "$0", rt, imm, pos)};
            }
            if (this == la) {
                return calculateLA(words, pos);
            }
            if (this == blt) {
                String rt = words.getNextWord();
                if (!words.getNextWord().equals(",")) throw new IOException();
                String rsOrImm = words.getNextWord();
                if (Character.isDigit(rsOrImm.charAt(0))) {
                    if (!words.getNextWord().equals(",")) throw new IOException();
                    String offsetOrLabel = words.getNextWord();

                    return new Word[] {
                            new IInstruction(OP.slti, rt, "$1", rsOrImm, pos),
                            new IInstruction(OP.PSEUDObne, "$1", "$0", offsetOrLabel, pos)
                    };
                } else { // rt, rs, offset/label
                    if (!words.getNextWord().equals(",")) throw new IOException();
                    String offsetOrLabel = words.getNextWord();
                    return new Word[] {
                        new RInstruction( OP.slt, rt, "$1", rsOrImm),
                        new IInstruction(OP.PSEUDObne, "$1", "$0", offsetOrLabel, pos)
                    };
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return new Word[] {new Word(true)}; // Error
    }

    public int numOps(MIPSFileParser words, labelPosTracker pos) throws IOException {
        try {
            if (this == move) {
                return 1;
            }
            if (this == li) {
                return 1;
            }
            if (this == la) {
                String rt = words.getNextWord();
                if (!words.getNextWord().equals(",")) throw new IOException();
                int imm = 0;
                boolean foundParen = false;
                boolean foundLabel = false;
                if (words.peak() != '(') {
                    String n = words.getNextWord();
                    if (!n.equals("(")) {
                        if (!Character.isDigit(n.charAt(0))) foundLabel = true;
                        else imm += this.parseImm(n, pos, false);
                        if (words.peak() == '+') {
                            words.getNextWord(); // will be +
                            if (!Character.isDigit(n.charAt(0))) foundLabel = true;
                            else imm += this.parseImm(words.getNextWord(), pos, false);
                        }
                    } else foundParen = true;
                }
                if (foundParen || words.peak() == '(') {
                    if (!foundParen) words.getNextWord(); // Will be (
                    String rs = words.getNextWord();
                    words.getNextWord(); // will be )
                    if (foundLabel || imm > 0xFFFF) return 3; // TODO: I just assume that if it's a lable that it's going ot be greater than 0xFFFF since the label may not yet be known when this runs
                    if (foundParen) return 1;
                    return 2;
                } else if (foundLabel || imm > 0xFFFF) return 2;
                return 1;
            }
            if (this == blt) {
                return 2;
            }
        } catch (Exception e) {
            throw e;
        }
        return 0;
    }


    public Word[] calculateLA(MIPSFileParser words, labelPosTracker pos) throws IOException {
        // 1 la $t0, label
        // 2 la $t0, imm
        // 3 la $t0, label+imm
        // 4 la $t0, 50($t1)
        // 5 la $t0, label+imm($t1)
        // 6 la $t0 label($t1)
        String rt = words.getNextWord();
        if (!words.getNextWord().equals(",")) throw new IOException();
        int imm = 0;
        boolean foundParen = false;
        if (words.peak() != '(') {
            String n = words.getNextWord();
            if (!n.equals("(")) {
                imm += this.parseImm(n, pos, false);
                if (words.peak() == '+') {
                    words.getNextWord(); // will be +
                    imm += this.parseImm(words.getNextWord(), pos, false);
                }
            } else foundParen = true;
        }
        if (foundParen || words.peak() == '(') {
            // cover cases 4,5,6
            if (!foundParen) words.getNextWord(); // Will be (
            String rs = words.getNextWord();
            words.getNextWord(); // will be )
            if (imm > 0xFFFF) {
                String toHex = HexFormat.of().toHexDigits(imm); // Converts to 8 bit string
                String upperHex = "0x"+toHex.substring(0,4);
                String lowerHex = "0x"+toHex.substring(4);
                return new Word[] {
                        new IInstruction(OP.lui, "$zero", "$1", upperHex, pos),
                        new IInstruction(OP.ori, "$1", "$1", lowerHex, pos),
                        new RInstruction(OP.add, rs, rt, "$1")
                };
            }
            if (foundParen) return new Word[] { new IInstruction(OP.addi, rs, rt, "0", pos) };
            return new Word[] {
                new IInstruction(OP.ori, "$0", "$1", imm+"", pos),
                new RInstruction(OP.add, rs, rt, "$1")
            };


        } else {
            // cover cases 1,2,3 with the imm var value
            if (imm > 0xFFFF) {
                String toHex = HexFormat.of().toHexDigits(imm); // Converts to 8 bit string
                String upperHex = "0x"+toHex.substring(0,4);
                String lowerHex = "0x"+toHex.substring(4);
                return new Word[] {
                    new IInstruction(OP.lui, "$zero", "$1", upperHex, pos),
                    new IInstruction(OP.ori, "$1", rt, lowerHex, pos)
                };
            }
        }

        return new Word[] {
            new IInstruction(OP.addiu, "$0", rt, ""+imm, pos)
        };
    }

    public int parseImm(String imm, labelPosTracker pos, boolean isWordCount) {
        if (imm.length() > 1 && imm.substring(0,2).equals("0x")) {
            return HexFormat.fromHexDigits(imm.substring(2));
        } else if (Character.isDigit(imm.charAt(0))) {
             return Integer.parseInt(imm);
        } else {
            if (isWordCount) {
                return (pos.getLabelValue(imm)-4) / 4;
            } else {
                return pos.getLabelValue(imm);
            }
        }
    }
}
