package com.mamewo.malarm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

public class M3UPlaylist extends Playlist{
	private int _nextindex = 0;
	private Vector<String> _playlist;
	
	public M3UPlaylist(String basepath, String playlistpath) {
		super(basepath);
		try {
			_playlist = new Vector<String>();
			loadPlaylist(playlistpath);
		} catch (FileNotFoundException e) {
			//TODO: set default playlist?
			e.printStackTrace();
		} catch (IOException e) {
			//TODO: set default playlist?
			e.printStackTrace();
		}
	}
	
	@Override
	public String next() {
		if (_playlist.size() <= _nextindex) {
			_nextindex = 0;
		}
		String result = _playlist.get(_nextindex);
		_nextindex++;
		return getBasepath() + result;
	}

	@Override
	public void reset() {
		_nextindex = 0;
	}

	protected void loadPlaylist(String filename) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader (filename));
		String music_filename;
		while ((music_filename = br.readLine()) != null) {
			_playlist.add(music_filename);
		}
		br.close();
	}
}
