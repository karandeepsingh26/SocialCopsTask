package karan.socialcopstask;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Created by karan on 18/12/16.
 */
public class DownloadThread extends Thread {
    String url;
    Socket socket;
    CommonBytes commonBytes;
    public DownloadThread(String url,CommonBytes commonBytes)
    {
        this.url=url;
        this.commonBytes=commonBytes;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
//                socket=new Socket("localhost",8080);
                InputStream inputStream = connection.getInputStream();
//                OutputStream socketOutput= socket.getOutputStream();
                File root = new File(Environment.getExternalStorageDirectory(), "Social Cops");
                if (!root.exists())
                    root.mkdirs();
                File filePath = new File(root, "social cops" + DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString() + ".mp4");
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                int bytesRead = -1;
                byte[] buffer = new byte[8000];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    commonBytes.read(buffer);
//                    socketOutput.write(buffer,0,bytesRead);
                }
                inputStream.close();
                fileOutputStream.close();
//                socketOutput.close();
                Log.d("file", "downloaded");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

