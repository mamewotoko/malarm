package com.mamewo.malarm;

public interface Playlist {
	
	public boolean isEmpty();

	/**
	 * 
	 * @return iterator which refers wakeup playlist, which has filename relative to base path
	 */
	public abstract String next();
	/**
	 * reset position
	 */
	public abstract void reset();
}
