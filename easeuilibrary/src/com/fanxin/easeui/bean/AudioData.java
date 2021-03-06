package com.fanxin.easeui.bean;

import java.io.Serializable;

/**
 * Created by houminjiang on 18-7-3.
 */

public class AudioData implements Serializable {
    private static final long serialVersionUID = 3999951906575286192L;
    private int version;
    private long sequence;
    private byte[] data;

    public AudioData(byte[] data) {
        this(0, data);
    }

    public AudioData(long sequence, byte[] data) {
        this(0, sequence, data);
    }

    public AudioData(int version, long sequence, byte[] data) {
        this.version = version;
        this.sequence = sequence;
        this.data = data;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getSequence() {
        return sequence;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
