package com.mamewo.malarm24;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

//TODO: change to proper interface
public final class M3UPlaylist implements Playlist {
	private static final String TAG = "malarm";
	private int mNextIndex = 0;
	private final String mBasepath;
	private final String mPlaylistFilename;
	private ArrayList<String> mPlaylist;
	
	/**
	 * 
	 * @param playlist_basepath path to directory which contains play list
	 * @param playlist_filename filename of playlist (not absolute path)
	 */
	public M3UPlaylist(String playlist_basepath, String playlist_filename) throws FileNotFoundException {
		mBasepath = playlist_basepath;
		mPlaylistFilename = playlist_filename;
		final String playlist_abs_path = (new File(playlist_basepath, playlist_filename)).getAbsolutePath();
		try {
			mPlaylist = new ArrayList<String>();
			load(playlist_abs_path);
		} catch (IOException e) {
			Log.i(TAG, "cannot read playlist " + playlist_filename);
		}
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
		return (List<String>)mPlaylist.clone();
	}

	protected void load(final String filename) throws FileNotFoundException, IOException {
		final BufferedReader br = new BufferedReader(new FileReader (filename));
		String music_filename;
		while ((music_filename = br.readLine()) != null) {
			if (music_filename.charAt(0) != '#') {
				mPlaylist.add(music_filename);
			}
		}
		br.close();
	}
	
	@Override
	public boolean isEmpty() {
		return mPlaylist.isEmpty();
	}
	
	@Override
	public int size() {
		return mPlaylist.size();
	}

	public void remove(int pos) {
		mPlaylist.remove(pos);
	}
	
	public void insert(int pos, String path) {
		mPlaylist.add(pos, path);
	}
	
	public void save() throws IOException {
		File playlist = new File(mBasepath, mPlaylistFilename);
		if (playlist.exists()) {
			if (! playlist.renameTo(new File(mBasepath, "_" + mPlaylistFilename))) {
				//TODO: localize
				Log.i(TAG, "rename failed");
				return;
			}
		}
		Log.i(TAG, "after rename: " + playlist.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(playlist));
		for (String filename : mPlaylist) {
			bw.append(filename + "\n");
		}
		bw.close();
	}
}
