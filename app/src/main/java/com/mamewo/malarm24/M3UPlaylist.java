package com.mamewo.malarm24;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

//TODO: change to proper interface
public final class M3UPlaylist
        implements Playlist
{
    private static final String TAG = "malarm";
    private int currentIndex_ = 0;
    private final String basepath_;
    private final String playlistFilename_;
    //use array list to call clone....
    private ArrayList<String> playlist_;
    private List<String> missinglist_;

    /**
     * @param basepath         path to directory which contains play list
     * @param playlistFilename filename of playlist (not absolute path)
     */
    public M3UPlaylist(String basepath, String playlistFilename)
            throws FileNotFoundException, IOException
    {
        basepath_ = basepath;
        playlistFilename_ = playlistFilename;
        String playlist_abs_path =
                (new File(basepath, playlistFilename)).getAbsolutePath();
        playlist_ = new ArrayList<String>();
        missinglist_ = new ArrayList<String>();
        currentIndex_ = 0;
        load(playlist_abs_path);
    }

    public void goNext() {
        //TODO: refactoring
    }

    public File getPlaylistFile(){
        return new File(basepath_, playlistFilename_);
    }
    
    //add repeat setting, shuffle mode
    // file | mp3 file on web | podcast
    @Override
    public MusicURL getURL() {
        if (playlist_.size() <= currentIndex_) {
            currentIndex_ = 0;
        }
        MusicURL result;
        String path = playlist_.get(currentIndex_);
        if (path.startsWith("podcast:http")) {
            //adhoc...
            String xmlURL = path.substring(8);
            //TODO: support multiple episode
            //result = getFirstEpisodeURL(result);
            result = new MusicURL(MusicURL.URLType.PODCAST_XML, xmlURL);
        }
        else {
            if (!(new File(path)).isAbsolute()) {
                path = (new File(basepath_, path)).getAbsolutePath();
            }
            result = new MusicURL(MusicURL.URLType.MUSIC, path);
        }
        return result;
    }

    @Override
    public String getName() {
        return playlistFilename_;
    }

    public List<String> toList() {
        return (List<String>) playlist_.clone();
    }

    protected void load(String filename)
            throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String musicFilename;
        while ((musicFilename = br.readLine()) != null) {
            if (musicFilename.length() > 0 && musicFilename.charAt(0) != '#') {
                //TODO: check podcast
                if (musicFilename.startsWith("podcast:http")) {
                    playlist_.add(musicFilename);
                }
                else {
                    File f = new File(basepath_, musicFilename);
                    if(f.exists()){
                        playlist_.add(musicFilename);
                    }
                    else {
                        //report missing file
                        missinglist_.add(musicFilename);
                    }
                }
            }
        }
        br.close();
    }

    @Override
    public void setPosition(int pos) {
        currentIndex_ = pos % size();
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

    public int getCurrentPosition() {
        return currentIndex_;
    }

    public void save()
            throws IOException {
        File playlist = new File(basepath_, playlistFilename_);
        if (playlist.exists()) {
            if (!playlist.renameTo(new File(basepath_, "_" + playlistFilename_))) {
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
