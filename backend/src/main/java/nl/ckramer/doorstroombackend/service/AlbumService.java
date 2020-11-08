package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.AlbumRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public ApiResponse validateAlbum(Album album) {
        if (StringUtils.isAnyEmpty(album.getName()) || album.getArtist() == null) {
            return new ApiResponse(false, "The album was not valid, please try again.");
        }
        return new ApiResponse(true);
    }

    public ApiResponse findById(Long id, User user) {
        Optional<Album> albumOptional = albumRepository.findById(id);

        if (albumOptional.isPresent()) {
            Album album = albumOptional.get();
            if (album.getUser() == user) {
                return new ApiResponse(true, album);
            }
        }
        return new ApiResponse(false, "You don't have access to view this content");
    }

}
