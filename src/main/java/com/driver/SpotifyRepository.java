package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newUser = new User(name,mobile);
        users.add(newUser);
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {

        Artist artist = new Artist();
        for(Artist a:artists){
            if(a.getName().equals(artistName)){
                artist = a;
            }
        }
        if(artists.contains(artist)){
            Album newAlbum = new Album(title);
            albums.add(newAlbum);
            return newAlbum;
        }
        Artist newArtist = new Artist(artistName);
        artists.add(newArtist);
        Album new_Album = new Album(title);
        albums.add(new_Album);
        return new_Album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        for(Album album: albums){
            if(album.getTitle().equals(albumName)){

                Song newSong = new Song(title,length);
                songs.add(newSong);

                List<Song> list = new ArrayList<>();
                if(albumSongMap.containsKey(album)){
                    list = albumSongMap.get(album);
                }
                list.add(newSong);
                albumSongMap.put(album,list);
                return newSong;
            }
        }
        throw new Exception("Album does not exist");
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                Playlist newPlaylist = new Playlist(title);
                List<Song> list = new ArrayList<>();
                for(Song song:songs){
                    if(song.getLength() == length){
                        list.add(song);
                    }
                }
                playlistSongMap.put(newPlaylist,list);
                playlists.add(newPlaylist);
                return newPlaylist;
            }
        }
        throw new Exception("User does not exist");
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                Playlist newPlaylist = new Playlist(title);
                List<Song> list = new ArrayList<>();
                for(Song song:songs){
                    if(songTitles.contains(song.getTitle())){
                        list.add(song);
                    }
                }
                playlistSongMap.put(newPlaylist,list);
                playlists.add(newPlaylist);
                return newPlaylist;
            }
        }
        throw new Exception("User does not exist");
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;

        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user = u;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }

        for(Playlist playlist:playlists){
            if(playlist.getTitle().equals(playlistTitle)){

                List<Playlist> list = new ArrayList<>();
                if(userPlaylistMap.containsKey(user)){
                    list = userPlaylistMap.get(user);
                }
                list.add(playlist);
                userPlaylistMap.put(user,list);

                creatorPlaylistMap.put(user,playlist);

                List<User>  userlist = new ArrayList<>();
                if(playlistListenerMap.containsKey(playlist)){
                    userlist = playlistListenerMap.get(playlist);
                }
                userlist.add(user);
                playlistListenerMap.put(playlist,userlist);
            }
        }
        throw new Exception("Playlist does not exist");
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = new User();
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user = u;
            }
        }
        if(user.getMobile() == null){
            throw new Exception("User does not exist");
        }
        Song song = new Song();
        for(Song s:songs){
            if(s.getTitle().equals(songTitle)){
                song = s;
            }
        }
        if(song.getTitle() == null){
            throw new Exception("Song does not exist");
        }
        if(songLikeMap.containsKey(song)){
            boolean liked = false;
            for(User myUser :songLikeMap.get(song)){
                if(myUser.equals(user)){
                    liked = true;
                    break;
                }
            }
            if(!liked){
                song.setLikes(song.getLikes()+1);

                Album albumName = null;
                for(Map.Entry<Album,List<Song>> am_map:albumSongMap.entrySet()){
                    if(am_map.getValue().contains(song)){
                        albumName = am_map.getKey();
                        break;
                    }
                }
                if(albumName != null){
                    Artist artistName = null;
                    for(Map.Entry<Artist,List<Album>> aa_map:artistAlbumMap.entrySet()){
                        if(aa_map.getValue().contains(albumName)){
                            artistName = aa_map.getKey();
                            break;
                        }
                    }
                    if(artistName != null)artistName.setLikes(artistName.getLikes()+1);
                }
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        String most_pop_artist = "";
        int likes = 0;

        for(Artist artist:artists){
            if(artist.getLikes() > likes){
                most_pop_artist = artist.getName();
                likes = artist.getLikes();
            }
        }
        return most_pop_artist;
    }

    public String mostPopularSong() {
        String most_pop_song = "";
        int likes = 0;
        for(Song song:songs){
            if(song.getLikes() > likes){
                most_pop_song = song.getTitle();
                likes = song.getLikes();
            }
        }
        return most_pop_song;
    }
}
