package karan.socialcopstask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by karan on 18/12/16.
 */
public class PlayThread extends Thread {
    int id=0;

    ServerSocket serverSocket;
    Socket socket;
    CommonBytes commonBytes;
    public PlayThread(CommonBytes commonBytes)
    {
        this.commonBytes=commonBytes;
        try {
            serverSocket=new ServerSocket(8090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run()
    {
        while (true)
        {
            try {
                socket=serverSocket.accept();
                Log.d("request","Accepted");
                id++;
                PlayThreadService service=new PlayThreadService(socket,commonBytes);
                service.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
class PlayThreadService extends Thread{
    ServerSocket serverSocket;
    Socket socket;
    int id;
    CommonBytes commonBytes;
    byte[] incomingBytes=new byte[8000];

    public PlayThreadService(Socket socket,CommonBytes commonBytes)
    {
        this.commonBytes=commonBytes;
        this.socket=socket;
    }
    @Override
    public void run() {
        try {
            Log.d("connection","established");
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();
            commonBytes.read(incomingBytes);

           while (incomingBytes.length!=0)
           {
               outputStream.write(incomingBytes);
               commonBytes.read(incomingBytes);

           }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
