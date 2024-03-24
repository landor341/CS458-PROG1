package mips;

import Instructions.*;
import enums.DIRECTIVES;
import enums.OP;
import enums.PSUEDOOP;
import java.io.IOException;
import java.util.ArrayList;

// A program consists of a series of instructions. This class will be passed a stream of chars to search through
public class MIPSProgram {

    // Arraylist where each index represents a word in memory
    private final ArrayList<Word> code = new ArrayList<Word>(),
            data = new ArrayList<Word>();

    labelPosTracker pos = new labelPosTracker();



    public MIPSProgram(String filePath) throws IOException {
        this.append(filePath);
    }


    private void append(String filePath) throws IOException {
        MIPSFileParser words = new MIPSFileParser(filePath);
        this.appendLabels(words);

        words = new MIPSFileParser(filePath);
        this.appendText(words);

    }

    private void appendLabels(MIPSFileParser words) throws IOException {
        pos.start();
        boolean needToAddLabel = false;
        String nextLabel="";


        // Scan to assign labels
        while (!words.isFinished()) {
            String word = words.getNextWord();
            if (word.equals(".data")) {
                pos.setState(labelPosTracker.programStage.data);
            } else if (word.equals(".text")) {
                pos.setState(labelPosTracker.programStage.text);
            } else if (pos.getState() == labelPosTracker.programStage.none) {
                continue;
            } else if (word.charAt(word.length()-1) == ':') { // it's a label
                needToAddLabel = true;
                nextLabel = word.substring(0, word.length()-1);
            } else {
                if (word.equals(DIRECTIVES.asciiz.name)) {
                    if (needToAddLabel) {
                        needToAddLabel = false;
                        this.pos.addLabel(nextLabel);
                    }
                    this.pos.advanceNBytes(words.getNextString().length() + 1); // + 1 for null terminator
                    words.endLine();
                    continue;
                }
                else if (word.equals(DIRECTIVES.space.name)) {
                    if (needToAddLabel) {
                        needToAddLabel = false;
                        this.pos.addLabel(nextLabel);
                    }
                    this.pos.advanceNBytes(Integer.parseInt(words.getNextWord()));
                    words.endLine();
                    continue;
                }
                else if (word.equals(DIRECTIVES.word.name)) {
                    if (needToAddLabel) {
                        needToAddLabel = false;
                        this.pos.goToNextWordBeginning();
                        this.pos.addLabel(nextLabel);
                    }
                    this.pos.goToNextWord();
                    words.endLine();
                    continue;
                }

                this.pos.goToNextWordBeginning();
                if (needToAddLabel) {
                    needToAddLabel = false;
                    this.pos.addLabel(nextLabel);
                }

                boolean found = false;
                // Check if it's a pseudoOp
                for (PSUEDOOP p : PSUEDOOP.values())
                    if (word.equals(p.name())) {
                        found = true;
                        this.pos.goToNextWordBeginning();
                        this.pos.advanceNBytes(4*p.numOps(words, pos));
                        words.endLine();
                        break;
                    }
                // Otherwise assume it's an op
                if (!found) {
                    this.pos.goToNextWord();
                    words.endLine();
                }
            }
        }
        this.pos.start();
        words.close();
    }

    private void appendText(MIPSFileParser words) throws IOException {
        pos.start();

        // Scan to assign labels
        while (!words.isFinished()) {
            String word = words.getNextWord();
            if (words.isFinished()) break;

            if (word.equals(".data")) {
                this.pos.setState(labelPosTracker.programStage.data);
            } else if (word.equals(".text")) {
                this.pos.setState(labelPosTracker.programStage.text);
            } else if (word.charAt(word.length()-1) == ':' || this.pos.getState() == labelPosTracker.programStage.none) {
                continue;
            } else { // it's an op
                if (word.equals(DIRECTIVES.asciiz.name)) {
                    String str = words.getNextString();
                    int numBits = str.length() + 1;

                    int curBit = 0;
                    fourBytes cur;
                    boolean alreadyAdded;
                    if (this.pos.getCurrentPos()%4!=0) {
                        curBit = this.pos.getCurrentPos()%4;
                        alreadyAdded = true;
                        cur = (fourBytes) (this.pos.getState() == labelPosTracker.programStage.data ? data.getLast() : code.getLast());
                    } else {
                        alreadyAdded = false;
                        cur = new fourBytes();
                    }

                    for (char c : str.toCharArray()) {
                        cur.setByte(curBit, Word.decimalToBinary(c, 8));
                        if (curBit == 3) {
                            if (alreadyAdded) alreadyAdded = false;
                            else this.addToCode(cur);
                            cur = new fourBytes();
                            curBit = 0;
                        } else curBit++;
                    }
                    this.addToCode(cur); // There should always be at least one unset byte in cur which is automatically the null terminator

                    this.pos.advanceNBytes(numBits);
                    continue;
                } else if (word.equals(DIRECTIVES.space.name)) {
                    int numBytes = Integer.parseInt(words.getNextWord());
                    for (int i=0; i<numBytes; i+=4) this.addToCode(new Word());
                    this.pos.advanceNBytes(numBytes);
                    continue;
                } else if (word.equals(DIRECTIVES.word.name)) {
                    this.pos.goToNextWordBeginning();
                    this.addToCode(new Word(words.getNextWord()));
                    this.pos.goToNextWord();
                    continue;
                }

                this.pos.goToNextWordBeginning(); // This is needed because of label calculations

                // Check if it's a pseudoOp
                boolean found = false;
                for (PSUEDOOP p: PSUEDOOP.values())
                    if (word.equals(p.name())) {
                        found = true;
                        for (Word w : p.parseInstruction(words, pos)) {
                            addToCode(w);
                            this.pos.goToNextWord();
                        }
                        break;
                    }

                if (!found) {
                    this.addToCode(OP.matchOp(word).parseInstruction(words, pos));
                    this.pos.goToNextWord();
                }
            }
        }
        this.pos.start();
        this.pos.setState(labelPosTracker.programStage.text);
        words.close();
    }

    public Word getCurrentWord() {
        Word res = code.get(pos.getCurrentIndex());
        pos.goToNextWord();
        return res;
    }

    public boolean hasNextWord() {
        return this.pos.getCurrentIndex() < code.size();
    }

    public Object[] getCode() {
        return code.toArray();
    }

    public Object[] getData() {
        return data.toArray();
    }

//    private boolean isAnImm(String word) {
//        return Character.isDigit(word.charAt(0));
//    }

    private void addToCode(Word w) {
        if (this.pos.getState() == labelPosTracker.programStage.data) data.add(w);
        else code.add(w);
    }
}
