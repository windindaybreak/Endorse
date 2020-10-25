package com.example.user.shell;/*
	开放API类型
	type 200     账号登陆
	     500    退出登陆
*/

public class AcceptType {
	int type; // 接受信息种类
	int cookie; //临时身份信息
	String  command; // 指令
	AcceptType(int type,int cookie,String command){
		this.type = type;
		this.cookie = cookie;
		this.command = command;
	}
	int gettype() {
		return type;
	}
	int getcookie(){
		return cookie;
	}
	String getcommand(){
		return command;
	}
}
