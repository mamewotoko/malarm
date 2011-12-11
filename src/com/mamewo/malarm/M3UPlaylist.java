package com.mamewo.malarm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import android.util.Log;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

public class M3UPlaylist implements Playlist{
	private int _nextindex = 0;
	String _basepath;
	private Vector<String> _playlist;

	/**
	 * 
	 * @param playlist_basepath path to directory which contains play list
	 * @param playlist_filename filename of playlist (not absolute path)
	 */
	public M3UPlaylist(String playlist_basepath, String playlist_filename) {
		_basepath = playlist_basepath;
		String playlist_abs_path = (new File(playlist_basepath, playlist_filename)).getAbsolutePath();
		try {
			_playlist = new Vector<String>();
			loadPlaylist(playlist_abs_path);
		} catch (FileNotFoundException e) {
			Log.i("M3UPlaylist", "cannot find playlist " + playlist_filename);
		} catch (IOException e) {
			Log.i("M3UPlaylist", "cannot read playlist " + playlist_filename);
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isEmpty() {
		return _playlist.isEmpty();
	}
	
	@Override
	public String next() {
		if (_playlist.size() <= _nextindex) {
			_nextindex = 0;
		}
		String result = _playlist.get(_nextindex);
		//TODO: test
		if (! (new File(result)).isAbsolute()) {
			result = (new File(_basepath, result)).getAbsolutePath();
		}
		_nextindex++;
		return result;
	}

	@Override
	public void reset() {
		_nextindex = 0;
	}

	protected void loadPlaylist(String filename) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader (filename));
		String music_filename;
		while ((music_filename = br.readLine()) != null) {
			if (! music_filename.startsWith("#")) {
				_playlist.add(music_filename);
			}
		}
		br.close();
	}
}
