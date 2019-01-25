package trc;

//import edu.wpi.first.wpilibj.RobotBase;
//import edu.wpi.cscore.UsbCamera;
import org.usb4java.LibUsb;
import org.usb4java.Context;
import org.usb4java.LibUsbException;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
/**
 * #define PIXY_VID       0xB1AC
 * #define PIXY_PID       0xF000
 * 
 */

public final class App {

  public static Device findDevice(Context context, short vendorId, short productId)
  {
      // Read the USB device list
      DeviceList list = new DeviceList();
      int result = LibUsb.getDeviceList(context, list);
      if (result < 0) throw new LibUsbException("Unable to get device list", result);
  
      try
      {
          // Iterate over all devices and scan for the right one
          for (Device device: list)
          {
              DeviceDescriptor descriptor = new DeviceDescriptor();
              result = LibUsb.getDeviceDescriptor(device, descriptor);
              if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
              if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) return device;
          }
      }
      finally
      {
          // Ensure the allocated device list is freed
          LibUsb.freeDeviceList(list, true);
      }
  
      // Device not found
      return null;
  }

  private static final short PIXY_VID = (short) 0xB1AC;
  private static final short PIXY_PID = (short) 0xF000;
  public static void main(String[] args) {
    //System.out.println("Hello, World");
    int result=-1;

    Context context = new Context();
    result = LibUsb.init(context);
    if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to initialize libusb.", result);
    
    
    Device device = findDevice(context, PIXY_VID, PIXY_PID  );
    
    System.out.println(device!=null);

    LibUsb.exit(context);
  }
}
