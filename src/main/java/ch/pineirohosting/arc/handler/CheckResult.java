package ch.pineirohosting.arc.handler;

public class CheckResult {

    private final int counter;
    private final String lastPacket;

    public CheckResult(int counter, String lastPacket) {
        this.counter = counter;
        this.lastPacket = lastPacket;
    }

    public int getCounter() {
        return counter;
    }

    public String getLastPacket() {
        return lastPacket;
    }
}
