package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.SongRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public ApiResponse validateSong(Song song) {
        if (StringUtils.isAnyEmpty(song.getName()) || song.getArtist() == null || song.getGenre() == null) {
            return new ApiResponse(false, "The song was not valid, please try again.");
        }
        return new ApiResponse(true);
    }

    public ApiResponse findById(Long id, User user) {
        Optional<Song> songOptional = songRepository.findByIdAndUser(id, user);
        if (songOptional.isPresent()) {
            return new ApiResponse(true, songOptional.get());
        }
        return new ApiResponse(false, "You don't have access to view this content");
    }

}
