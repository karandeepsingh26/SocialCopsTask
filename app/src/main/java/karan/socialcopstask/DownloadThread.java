package karan.socialcopstask;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.net.http.HttpResponseCache;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.util.Xml;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by karan on 18/12/16.
 */
public class DownloadThread extends Thread {
    String url;
    Socket socket;
    ServerSocket serverSocket;
    CommonBytes commonBytes;
    public DownloadThread(String url,CommonBytes commonBytes)
    {
        this.url=url;
        this.commonBytes=commonBytes;
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(8090);

            while (true)
            {
                socket=serverSocket.accept();
                new DownloadThreadService(socket,commonBytes,url).start();

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
}

}
class DownloadThreadService extends Thread{
    Socket socket;
    CommonBytes commonBytes;
    String url;
    Base64 base64;

    DownloadThreadService(Socket socket,CommonBytes commonBytes,String url) {
        this.socket = socket;

        this.commonBytes=commonBytes;
        this.url=url;
    }
    @Override
    public void run() {
        try {

            BufferedOutputStream socketOutput=new BufferedOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            SocketChannel channel=socket.getChannel();
            String inputLine;
            while (!(inputLine = in.readLine()).equals(""))
                System.out.println(inputLine);
            Log.d("connection","established");
            String headers = "HTTP/1.1 206 Partial Content \r\n";
            headers+="Connection: keep-alive \r\n";
            headers += "Content-Type: audio/mpeg \r\n";
            headers += "Content-Length:2204961 \r\n";
            headers += "Content-Range: bytes 0-65536/* \r\n";
            headers += "\r\n";
//            String headers = "HTTP/1.1 200 \r\n";
//            headers += "Content-Type: audio/mp4 \r\n";
//            headers += "Content-Length:2212961 \r\n";
//            headers += "Accept-Ranges: bytes \r\n";


            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            //    String header=getHeaders(connection);
                InputStream inputStream = connection.getInputStream();

                File root = new File(Environment.getExternalStorageDirectory(), "Social Cops");
                if (!root.exists())
                    root.mkdirs();
                File filePath = new File(root, "social cops" + DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString() + ".mp4");
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                int bytesRead = -1;
                byte[] buffer = new byte[1024*50];
                ByteBuffer byteBuffer=ByteBuffer.allocate(headers.getBytes().length);
                byteBuffer.put(headers.getBytes());

                socket.getOutputStream().write(headers.getBytes());
                ArrayList<byte[]> arrayByte=new ArrayList<>();
                int count=0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                   // fileOutputStream.write(buffer, 0, bytesRead);
//                    commonBytes.write(buffer,bytesRead);

                    socket.getOutputStream().write(buffer,0,bytesRead);


                }



                inputStream.close();
                fileOutputStream.close();
                socketOutput.close();
                Log.d("file", "downloaded");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getHeaders(HttpURLConnection httpURLConnection)
    {
        ArrayList<String> strings=new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(httpURLConnection.getResponseCode())
                    .append(" ")
                    .append(httpURLConnection.getResponseMessage())
                    .append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, List<String>> map = httpURLConnection.getHeaderFields();
        int i=0;
        for (Map.Entry<String, List<String>> entry : map.entrySet())
        {
            if (entry.getKey() == null)
                continue;
            builder.append( entry.getKey())
                    .append(": ");

            List<String> headerValues = entry.getValue();
            Iterator<String> it = headerValues.iterator();
            if (it.hasNext()) {
               builder.append(it.next());


                while (it.hasNext()) {
                    builder.append(", ")
                            .append(it.next());
                }
            }
            strings.add(builder.toString());
            System.out.println("**********"+strings.get(i));
            i++;
            builder=new StringBuilder();
//            builder.append("\n");
        }
       // System.out.print(builder);
        return builder.toString();
    }
}



