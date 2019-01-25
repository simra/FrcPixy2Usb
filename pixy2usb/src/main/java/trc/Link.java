package trc;

public abstract class Link {
// flags
protected final int LINK_FLAG_SHARED_MEM     =                       0x01;
protected final int	LINK_FLAG_ERROR_CORRECTED           =            0x02;

// result codes
//private final int LINK_RESULT_OK                    =              0;
//private final int LINK_RESULT_ERROR                  =             -100;
protected final int LINK_RESULT_ERROR_RECV_TIMEOUT      =            -101;
protected final int LINK_RESULT_ERROR_SEND_TIMEOUT       =           -102;

// link flag index
protected final byte LINK_FLAG_INDEX_FLAGS                     =      0x00;
protected final byte LINK_FLAG_INDEX_SHARED_MEMORY_LOCATION     =     0x01;
protected final byte LINK_FLAG_INDEX_SHARED_MEMORY_SIZE         =     0x02;

protected int m_flags = 0;
protected int m_blockSize = 0;

    public Link()
    {
        m_flags = 0;
        m_blockSize = 0;
    }


    // the timeoutMs is a timeout value in milliseconds.  The timeout timer should expire
    // when the data channel has been continuously idle for the specified amount of time
    // not the summation of the idle times.
    abstract int send(byte[] data, short timeoutMs);
    abstract int receive(byte[] data, short timeoutMs);
    abstract void setTimer();
    abstract int getTimer(); // returns elapsed time in milliseconds since setTimer() was called
    public int getFlags(byte index) // c++ default was =LINK_FLAG_INDEX_FLAGS
    {
        if (index==LINK_FLAG_INDEX_FLAGS)
            return m_flags;
        else
            return 0;
    }
    public int blockSize()
    {
        return m_blockSize;
    }
    public byte[] getBuffer()
    {
        throw new  UnsupportedOperationException();
    }

}
