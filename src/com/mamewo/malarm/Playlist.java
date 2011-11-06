package com.mamewo.malarm;

public abstract class Playlist {
	protected String _basepath;
	
	protected Playlist() {
		//empty
	}
	
	public Playlist(String basepath) {
		_basepath = basepath;
		if (! _basepath.endsWith("/")) {
			_basepath = basepath + "/";
		}
	}

	/**
	 * 
	 * @return path to music files
	 */
	public String getBasepath() {
		return _basepath;
	}

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
