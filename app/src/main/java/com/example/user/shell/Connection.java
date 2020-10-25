package com.example.user.shell;

public class Connection extends Thread {

    public String CSQI(String num,int cookie,String cnt){
        SocketHandler new_thread = new SocketHandler(num,cookie,cnt);
        Thread thread = new Thread(new_thread);
        thread.start();
        if(Integer.parseInt(num)<700){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new_thread.getreply();
    }
}
