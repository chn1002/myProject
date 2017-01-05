package com.chn.resource;

import com.chn.event.EventArgs; 

public class VisitedEventArgs extends EventArgs {
	
	public static final int NETWORK_MESSGAE = 0x001;
	public static final int POWER_OFF = 0x002;
	
    private int command = 0;
    private String message;
     
    public VisitedEventArgs(int msg, String message) {
        this.command = msg;
        this.message = message;
    }
    
    public int getCommand(){
    	return command;
    }
     
    public String getMessage() {
        return this.message;
    }
} 