package com.chn.event;

public interface IEventHandler<TEventArgs extends EventArgs> {
	 public void eventReceived(Object sender, TEventArgs e);
}
