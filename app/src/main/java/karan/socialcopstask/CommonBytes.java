package karan.socialcopstask;

import java.util.ArrayList;

/**
 * Created by karan on 19/12/16.
 */
public class CommonBytes {
    ArrayList<byte[]> buffer=new ArrayList<>();
    public void read(byte[] bytes)
    {
        synchronized (this)
        {
            buffer.add(bytes);
            notify();
        }
    }
    public byte[] write() throws InterruptedException {
            synchronized (this) {
                while (buffer.size() == 0)
                    wait();

                byte[] bytes = buffer.get(0);
                buffer.remove(0);
                notify();
                return bytes;
            }


    }

}
