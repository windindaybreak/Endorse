package com.example.user.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private String num, command, reply;
    private int cookie;

    public SocketHandler(String num, int cookie, String command) {
        this.num = num;
        this.cookie = cookie;
        this.command = command;
        this.reply = "000";
    }

    public String getreply() {
        return reply;
    }

    @Override
    public void run() {
        Socket socket;
        BufferedReader br;
        PrintWriter pw;
        try {
            socket = new Socket();
            socket.connect( new InetSocketAddress( "59.110.237.234", 18824 ), 5000 ); // DESKTOP-JM3E1LE 59.110.237.234
            br = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            pw = new PrintWriter( socket.getOutputStream(), true );
            String ask = num + cookie + command;
            pw.println( ask );
            while (true) {
                try {
                    if (br.ready()) {
                        reply = br.readLine();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println( "信息接收失败" );
                }
            }
            ask = "50000000"; //断开连接
            pw.println( ask );
            pw.close();
            try {
                br.close();
                socket.close();
            } catch (IOException e) {
            }
        } catch (IOException e) {
        }
    }
}