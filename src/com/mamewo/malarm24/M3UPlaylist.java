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
public final class M3UPlaylist
	implements Playlist
{
	private static final String TAG = "malarm";
	private int nextIndex_ = 0;
	private final String basepath_;
	private final String playlistFilename_;
	private ArrayList<String> playlist_;
	
	/**
	 * 
	 * @param basepath path to directory which contains play list
	 * @param playlistFilename filename of playlist (not absolute path)
	 */
	public M3UPlaylist(String basepath, String playlistFilename)
			throws FileNotFoundException
	{
		basepath_ = basepath;
		playlistFilename_ = playlistFilename;
		final String playlist_abs_path =
				(new File(basepath, playlistFilename)).getAbsolutePath();
		try {
			playlist_ = new ArrayList<String>();
			load(playlist_abs_path);
		}
		catch (IOException e) {
			Log.i(TAG, "cannot read playlist " + playlistFilename);
		}
	}
	
	
	@Override
	public String next() {
		if (playlist_.size() <= nextIndex_) {
			nextIndex_ = 0;
		}
		String result = playlist_.get(nextIndex_);
		//TODO: test
		if (! (new File(result)).isAbsolute()) {
			result = (new File(basepath_, result)).getAbsolutePath();
		}
		nextIndex_++;
		return result;
	}

	@Override
	public void reset() {
		nextIndex_ = 0;
	}
	
	public String getName() {
		return playlistFilename_;
	}
	
	public List<String> toList() {
		return (List<String>)playlist_.clone();
	}

	protected void load(final String filename)
			throws FileNotFoundException, IOException
	{
		final BufferedReader br = new BufferedReader(new FileReader (filename));
		String music_filename;
		while ((music_filename = br.readLine()) != null) {
			if (music_filename.charAt(0) != '#') {
				playlist_.add(music_filename);
			}
		}
		br.close();
	}
	
	@Override
	public boolean isEmpty() {
		return playlist_.isEmpty();
	}
	
	@Override
	public int size() {
		return playlist_.size();
	}

	public void remove(int pos) {
		playlist_.remove(pos);
	}
	
	public void insert(int pos, String path) {
		playlist_.add(pos, path);
	}
	
	public void save()
			throws IOException
	{
		File playlist = new File(basepath_, playlistFilename_);
		if (playlist.exists()) {
			if (! playlist.renameTo(new File(basepath_, "_" + playlistFilename_))) {
				Log.i(TAG, "rename failed");
				return;
			}
		}
		Log.i(TAG, "after rename: " + playlist.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(playlist));
		for (String filename : playlist_) {
			bw.append(filename + "\n");
		}
		bw.close();
	}
}
