package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Genre;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.GenreRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    public ApiResponse validateGenre(Genre genre) {
        if (StringUtils.isAnyEmpty(genre.getName())) {
            return new ApiResponse(false, "The genre was not valid, please try again.");
        }
        return new ApiResponse(true);
    }

    public ApiResponse findById(Long id, User user) {
        Optional<Genre> genreOptional = genreRepository.findByIdAndUser(id, user);

        if (genreOptional.isPresent()) {
            return new ApiResponse(true, genreOptional.get());
        }
        return new ApiResponse(false, "You don't have access to view this content");
    }

}
