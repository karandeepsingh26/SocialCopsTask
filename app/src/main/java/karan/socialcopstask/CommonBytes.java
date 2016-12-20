package karan.socialcopstask;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by karan on 19/12/16.
 */
public class CommonBytes {
    ArrayList<byte[]> buffer=new ArrayList<>();
    byte temp[];
    public void write(byte[] bytes,int bytesRead)
    {
        synchronized (this)
        {
            temp=new byte[bytesRead];
            System.arraycopy(bytes,0,temp,0,bytesRead);
            buffer.add(temp);
            notify();
        }
    }
    public byte[] read() throws InterruptedException {
            synchronized (this) {
                while (buffer.size() == 0)
                    return null;

                byte[] bytes = buffer.get(0);
                buffer.remove(0);
                notify();
                return bytes;
            }


    }

}
