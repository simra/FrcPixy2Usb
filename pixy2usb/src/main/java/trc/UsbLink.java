package trc;
import org.usb4java.LibUsb;
import org.usb4java.Context;
import org.usb4java.LibUsbException;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.Duration;
import java.time.Instant;


class USBLink  extends Link
{
    Context m_context;
    DeviceHandle m_handle;
    Instant m_timer;

    public USBLink() {
        m_handle = null;
        m_context = null;
        m_blockSize = 64;
        m_flags = LINK_FLAG_ERROR_CORRECTED;    
    }

    public int open() {
        close();

        m_context = new Context();
        LibUsb.init(m_context);
    
        return openDevice();
    
    }

    public void close() {
        
        if (m_handle!=null) {
            LibUsb.close(m_handle);
            m_handle=null;
        }
        if (m_context!=null)
        {
            LibUsb.exit(m_context);
            m_context=null;
        }

    }


    public int send(byte[] data, short timeoutMs) {
        
        if (timeoutMs==0) // 0 equals infinity
            timeoutMs = 10;

        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        IntBuffer transferred = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(m_handle, (byte)0x02, buffer, transferred, timeoutMs); 
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Control transfer failed", transferred.get());
        int sent = transferred.get();
        System.out.println(sent + " bytes sent");
        return sent;
    }

    public int receive(byte[] data, short timeoutMs) {
        
        if (timeoutMs==0) // 0 equals infinity
            timeoutMs = 100;
    
        // Note: if this call is taking more time than than expected, check to see if we're connected as USB 2.0.  Bad USB cables can
        // cause us to revert to a 1.0 connection.
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        IntBuffer transferred = IntBuffer.allocate(1);
        int result=LibUsb.bulkTransfer(m_handle, (byte)0x82, buffer, transferred, timeoutMs);
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Control transfer failed", transferred.get());
        int received = transferred.get();
        buffer.get(data,0, received);
        return received;
    }
     
    public void setTimer() 
    {
          m_timer = Instant.now();
    }

    public int getTimer()
    {
        long time = Duration.between(m_timer,Instant.now()).toMillis();
        return (int)time;
    }

    private int openDevice() {
        DeviceList list = null;
        int result = 0;
        int returnValue = -1;
        
        DeviceDescriptor desc = null;

        list = new DeviceList();
        result = LibUsb.getDeviceList(m_context, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        for (Device device: list)
        {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
            if (desc.idVendor()==PixyDefs.PIXY_VID && desc.idProduct()==PixyDefs.PIXY_PID)
            {
                m_handle=new DeviceHandle();
                result = LibUsb.open(device, m_handle);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to open device", result);
                // was if MACOS
                //LibUsb.resetDevice(m_handle);
                //
                if (LibUsb.setConfiguration(m_handle, 1)<0)
                {
                        LibUsb.close(m_handle);
                        m_handle = null;
                        continue;
                }
                if (LibUsb.claimInterface(m_handle, 1)<0)
                    {
                        LibUsb.close(m_handle);
                        m_handle = null;
                        continue;
                    }
                // was if LINUX
                LibUsb.resetDevice(m_handle);
                //
                returnValue=0;
                break;
            
            }
            
        }

        LibUsb.freeDeviceList(list, true);
        return returnValue;
    }

    






}

    




