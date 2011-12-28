package com.mamewo.malarm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

//TODO: change to proper interface
public final class M3UPlaylist implements Playlist {
	private int mNextIndex = 0;
	private final String mBasepath;
	private List<String> mPlaylist;
	static private final String[] DUMMY = new String[] {};
	
	/**
	 * 
	 * @param playlist_basepath path to directory which contains play list
	 * @param playlist_filename filename of playlist (not absolute path)
	 */
	public M3UPlaylist(String playlist_basepath, String playlist_filename) throws FileNotFoundException {
		mBasepath = playlist_basepath;
		final String playlist_abs_path = (new File(playlist_basepath, playlist_filename)).getAbsolutePath();
		try {
			mPlaylist = new ArrayList<String>();
			loadPlaylist(playlist_abs_path);
		} catch (IOException e) {
			Log.i("M3UPlaylist", "cannot read playlist " + playlist_filename);
		}
	}
	
	@Override
	public boolean isEmpty() {
		return mPlaylist.isEmpty();
	}
	
	@Override
	public int size() {
		return mPlaylist.size();
	}
	
	@Override
	public String next() {
		if (mPlaylist.size() <= mNextIndex) {
			mNextIndex = 0;
		}
		String result = mPlaylist.get(mNextIndex);
		//TODO: test
		if (! (new File(result)).isAbsolute()) {
			result = (new File(mBasepath, result)).getAbsolutePath();
		}
		mNextIndex++;
		return result;
	}

	@Override
	public void reset() {
		mNextIndex = 0;
	}
	
	public List<String> toList() {
		return mPlaylist;
	}

	protected void loadPlaylist(final String filename) throws FileNotFoundException, IOException {
		final BufferedReader br = new BufferedReader(new FileReader (filename));
		String music_filename;
		while ((music_filename = br.readLine()) != null) {
			if (music_filename.charAt(0) != '#') {
				mPlaylist.add(music_filename);
			}
		}
		br.close();
	}
}
