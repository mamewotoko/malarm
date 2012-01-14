package com.mamewo.malarm;

//TODO: fix interface
public interface Playlist {
	
	boolean isEmpty();
	int size();

	/**
	 * 
	 * @return iterator which refers wakeup playlist, which has filename relative to base path
	 */
	String next();
	/**
	 * reset position
	 */
	void reset();
	
}
