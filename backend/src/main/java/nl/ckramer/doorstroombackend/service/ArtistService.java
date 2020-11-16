package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public ApiResponse validateArtist(Artist artist) {
        if (artist == null || StringUtils.isAnyEmpty(artist.getName(), artist.getSurname(), artist.getPlace())) {
            return new ApiResponse(false, "The artist was not valid, please try again.");
        }
        return new ApiResponse(true);
    }

    public ApiResponse findById(Long id, User user) {
        Optional<Artist> artistOptional = artistRepository.findById(id);

        if (artistOptional.isPresent()) {
            Artist artist = artistOptional.get();
            if (artist.getUser().equals(user)) {
                return new ApiResponse(true, artist);
            }
        }
        return new ApiResponse(false, "You don't have access to view this content");
    }

}
