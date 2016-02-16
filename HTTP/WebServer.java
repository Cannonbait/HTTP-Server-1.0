package HTTP;//
// Multithreaded Java HTTP.WebServer
// (C) 2001 Anders Gidenstam
// (based on a lab in Computer Networking: ..)
//

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        // Set port number
        int port = 0;

        // Establish the listening socket
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Port number is: "+serverSocket.getLocalPort());


        // Wait for and process HTTP service requests
        while (true) {
            // Wait for TCP connection
            Socket requestSocket = serverSocket.accept();
            requestSocket.setSoLinger(true, 5);

            // Create an object to handle the request
            HttpRequest request  = new HttpRequest(requestSocket);

            //request.run()

            // Create a new thread for the request
            Thread thread = new Thread(request);

            // Start the thread
            thread.start();
        }
    }
}

final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    // Constants
    final static String SERVER = "WebServer/1.0";
    final static String HTTPVERSION = "HTTP/1.0";
    final static String HOMEPAGE = "/index.html";
    final static String ROOTPATH = "root";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implements the run() method of the Runnable interface
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Process a HTTP request
    private void processRequest() throws Exception {
        // Get the input and output streams of the socket.
        InputStream ins       = socket.getInputStream();
        DataOutputStream outs = new DataOutputStream(socket.getOutputStream());

        // Set up input stream filters
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));

        // Get the request line of the HTTP request
        String requestLine = br.readLine();

        String str = handleRequest(requestLine);

        System.out.println(str.toString());
        outs.writeBytes(str.toString());
        outs.close();
        br.close();
        socket.close();
    }

    private String handleRequest(String requestLine) {

        //String builder
        StringBuilder str = new StringBuilder();

        str.append(HTTPVERSION + " ");
        if (!validRequest(requestLine)){
            str.append("400 Bad Request" + CRLF);
            str.append(strDate() + CRLF);
            str.append(CRLF);
            return str.toString();
        }

        String[] reqStrings = requestLine.split(" ");
        File f = new File(completeFilePath(reqStrings[1]));
        if(!f.exists() || f.isDirectory()){
            str.append("404 File not found" + CRLF);
            str.append(strDate() + CRLF);
            str.append(CRLF);
            return str.toString();
        }

        System.out.println(reqStrings[0]);
        if(reqStrings[0].equals("POST")){
            str.append("501 Not Implemented" + CRLF);
            str.append(strDate() + CRLF);
            str.append(CRLF);
            return str.toString();
        }

        str.append("200 OK" + CRLF);
        str.append(strDate() + CRLF);
        str.append("Location: " + reqStrings[1] + CRLF);
        str.append("Server: " + SERVER + CRLF);
        str.append("Allow: GET, HEAD" + CRLF);

        str.append(CRLF);
        return str.toString();

    }


    private static String strDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        return("Date: " + dateFormat.format(new Date()));
    }

    private static String completeFilePath(String filePath){
        if (filePath.equals("/")){
            return ROOTPATH + HOMEPAGE;
        } else {
            return ROOTPATH + filePath;
        }
    }

    private static boolean validRequest(String requestLine){
        String[] reqStrings = requestLine.split(" ");
        if (reqStrings.length != 3){
            return false;
        }
        return generalHeader(reqStrings[0]) && requestHeader(reqStrings[1]) && entityHeader(reqStrings[2]);
    }

    private static boolean generalHeader(String header){
        return header.equals("POST") || header.equals("GET") || header.equals("HEAD");
    }

    private static boolean requestHeader(String request){
        if (request.length() >= 1 && request.charAt(0) == '/'){
            return true;
        }
        return false;
    }

    private static boolean entityHeader(String header){
        return header.equals(HTTPVERSION);
    }



    private static void sendBytes(FileInputStream  fins,
                                  OutputStream     outs) throws Exception {
        // Coopy buffer
        byte[] buffer = new byte[1024];
        int    bytes = 0;

        while ((bytes = fins.read(buffer)) != -1) {
            outs.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName)
    {
        if (fileName.toLowerCase().endsWith(".htm") ||
                fileName.toLowerCase().endsWith(".html")) {
            return "text/html";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.toLowerCase().endsWith(".jpg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }
}

