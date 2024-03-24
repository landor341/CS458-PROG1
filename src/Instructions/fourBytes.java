package Instructions;

public class fourBytes extends Word {
    public void setByte(int byteNum, boolean[] newByte) {
        for (int i=0; i<8; i++) {
            this.bits[i+8*(3-byteNum)] = newByte[i];
        }
    }
}
