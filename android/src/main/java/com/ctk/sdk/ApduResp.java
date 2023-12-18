package android.src.main.java.com.ctk.sdk;


public class ApduResp {
    public short LenOut = 0;
    public byte[] DataOut = new byte[512];
    public byte SWA = 0;
    public byte SWB = 0;

    public ApduResp() {

    }

    public ApduResp(short LenOut, byte[] DataOut, byte SWA, byte SWB) {
        this.LenOut = LenOut;
        this.DataOut = DataOut;
        this.SWA = SWA;
        this.SWB = SWB;
    }

    public ApduResp(byte[] resp) {
        this.LenOut = (short) ((int) (resp[1] & 0xff) * 256 + (int) (resp[0] & 0xff));
        System.arraycopy(resp, 2, DataOut, 0, 512);
        this.SWA = resp[514];
        this.SWB = resp[515];
    }

    public short getLenOut() {
        return LenOut;
    }

    public byte[] getDataOut() {
        return DataOut;
    }

    public byte getSWA() {
        return SWA;
    }

    public byte getSWB() {
        return SWB;
    }
}
