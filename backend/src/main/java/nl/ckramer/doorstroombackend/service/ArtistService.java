package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    public ApiResponse validateArtist(Artist artist) {
        if (StringUtils.isAnyEmpty(artist.getName(), artist.getSurname(), artist.getPlace())) {
            return new ApiResponse(false, "The artist was not valid, please try again.");
        }
        return new ApiResponse(true);
    }

}
