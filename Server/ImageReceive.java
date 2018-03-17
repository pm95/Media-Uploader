import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.DataInputStream;

public class ImageReceive
{

  public final static int SOCKET_PORT = 10000;
  public final static String IMAGE_TO_RECEIVE = "receiveme.jpg";
  public final static int FILE_SIZE = 100000000; // upper limit for file size

  int bytesRead;
  int current = 0;
  FileOutputStream fos = null;
  BufferedOutputStream bos = null;
  ServerSocket serverSocket = null;
  Socket sock = null;

  ImageReceive()
  {
    try
    {
      serverSocket = new ServerSocket(SOCKET_PORT);
      System.out.println("Connecting to sender\n");
      sock = serverSocket.accept();
      System.out.println("Waiting on sender now\n");

      DataInputStream dis = new DataInputStream(sock.getInputStream());
      System.out.println("Connection successful!\n");

      byte [] mybytearray  = new byte [FILE_SIZE];
      fos = new FileOutputStream(IMAGE_TO_RECEIVE);
      bos = new BufferedOutputStream(fos);
      bytesRead = dis.read(mybytearray,0,mybytearray.length);
      current = bytesRead;

      System.out.println("reading the bytes\n");
      while(bytesRead > -1) {
        bytesRead = dis.read(mybytearray, current, (mybytearray.length-current));
        if(bytesRead >= 0) current += bytesRead;
        System.out.println("waiting....");
      }

      bos.write(mybytearray, 0 , current);
      System.out.println("written....");
      bos.flush();
      System.out.println("flushed....");
      System.out.println("File " + "receiveme" + " downloaded (" + current + " bytes read)");

      fos.close();
      bos.close();
      sock.close();

    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main (String [] args ) throws IOException
  {
    System.out.println("\nReceiving IMAGE from Android client:\n");
    ImageReceive dos = new ImageReceive();
    System.out.println("\n\n");
  }

}
