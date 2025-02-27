package io.github.lemcoder.mikroaall;

public class Pointer {
    private long address;

    public Pointer(long address) {
        this.address = address;
    }

    public long getAddress() {
        return address;
    }

    public void increment() {
        address++;
    }

    public void decrement() {
        address--;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
