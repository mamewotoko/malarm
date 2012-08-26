package com.mamewo.malarm24;

public interface Playlist
{
	public boolean isEmpty();
	public int size();

	public String getName();

	/**
	 * 
	 * @return iterator which refers wakeup playlist, 
	 * which has filename relative to base path
	 */
	public String getURL();
	public int getCurrentPosition();
	public void setPosition(int pos);
}
