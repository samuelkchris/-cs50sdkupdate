package android.src.main.java.com.ctk.sdk;

public class ApduSend {
    public byte[] Command = null;
    public short  Lc;
    public byte[] DataIn = null;
    public short  Le;

    public ApduSend(byte[] Command, short  Lc, byte[] DataIn, short  Le){
        this.Command = new byte[Command.length];
        this.DataIn = new byte[DataIn.length];
        this.Command = Command;
        this.Lc = Lc;
        this.DataIn = DataIn;
        this.Le = Le;
    }

    public byte[] getBytes(){

        byte[] buf = new byte[520];
        System.arraycopy(Command, 0, buf, 0, Command.length);
        buf[4] = (byte) (Lc / 256);
        buf[5] = (byte) (Lc % 256);
        System.arraycopy(DataIn, 0, buf, 6, DataIn.length);
        buf[518] = (byte) (Le / 256);
        buf[519] = (byte) (Le % 256);
        return buf;
    }
}
