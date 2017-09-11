package ch.pineirohosting.arc.handler;

class CheckResult {

    private final int counter;
    private final String lastPacket;

    CheckResult(final int counter, final String lastPacket) {
        this.counter = counter;
        this.lastPacket = lastPacket;
    }

    int getCounter() {
        return counter;
    }

    String getLastPacket() {
        return lastPacket;
    }
}
