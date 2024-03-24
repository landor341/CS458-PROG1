package mips;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MIPSFileParser {

    private  final char[] endOfWordChars = "\t\n ;".toCharArray();
    private  final char[] independentChars = "(),+".toCharArray();
    private  final char startOfComment = '#';


    private final BufferedReader file;
    private char lastChar = ' ';
    boolean finished = false;

    public MIPSFileParser(String filePath) throws IOException {
        file = new BufferedReader(new FileReader(filePath));
    }

    /**
     * Returns the next word in the open document. A word is defined as a set of characters not including whitespace.
     * Commas and parenthesis count as their own words and anything on a line after and including a # is skipped over.
     *
     * @return next word in document as described in description
     */
    public String getNextWord() throws IOException {
        // skip to the front of the next word
        while (!this.charIsPartOfWord(lastChar)) {
            if (lastChar == '\uFFFF')
                throw new IOException();
            else if (lastChar == startOfComment)
                while (lastChar != '\n')
                    lastChar = (char) file.read();
            else if (arrContains(lastChar, endOfWordChars))
                lastChar = (char) file.read();
            else { // is an independent char
                char temp = lastChar;
                lastChar = (char) file.read();
                return "" + temp;
            }
        }

        // scan in the next word
        StringBuilder res = new StringBuilder();
        boolean endOfWord = false;
        while (!endOfWord && !finished) {
            if (!this.charIsPartOfWord(lastChar)) break;
            res.append(lastChar);
            if (!file.ready()) {
                finished = true;
                break;
            } else {
                lastChar = (char) file.read();
            }
        }

        return res.toString();
    }


    /**
     * Skips to after the next newline character
     */
    public void endLine() throws IOException {
        if (this.isFinished() || this.lastChar == '\n') return;
        //noinspection StatementWithEmptyBody
        while (file.ready() && !(file.read() == '\n'));
        lastChar = '\n';
        if (!file.ready()) finished = true;
    }

    public String getNextString() throws IOException {
        if (this.isFinished()) throw new IOException();

        //noinspection StatementWithEmptyBody
        while (file.ready() && !(file.read() == '"'));
        StringBuilder res = new StringBuilder();
        for (char n = (char) file.read(); file.ready() && n != '"'; n = (char) file.read()) res.append(n);

        return res.toString();
    }

    public void close() throws IOException {
        file.close();
    }

    public char peak() {
        return lastChar;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean charIsPartOfWord(char c) {
        return !(this.arrContains(c, endOfWordChars)  || this.arrContains(c, independentChars)  || c == startOfComment);
    }

    private boolean arrContains(char c, char[] arr) {
        for (char a: arr ) {
            if (c == a) return true;
        }
        return false;
    }

    public boolean isFinished() throws IOException {
        return finished;
    }
}
