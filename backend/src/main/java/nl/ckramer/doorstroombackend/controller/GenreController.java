package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.controller.interf.CrudController;
import nl.ckramer.doorstroombackend.entity.Genre;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.dto.GenreDto;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.GenreRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genre")
public class GenreController extends CrudController<GenreDto> {

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    GenreService genreService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);

        ApiResponse apiResponse = genreService.findById(id, user);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }

        GenreDto genreDto = new GenreDto((Genre) apiResponse.getObject());
        return ResponseEntity.ok(new ApiResponse(true, genreDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);

        List<GenreDto> genreDtoList = new ArrayList<>();
        for (Genre genre : genreRepository.findAllByUserAndDeletedFalse(user)) {
            genreDtoList.add(new GenreDto(genre));
        }

        return ResponseEntity.ok(new ApiResponse(true, genreDtoList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setGenreDto(genreDto);

        ApiResponse apiResponse = genreService.validateGenre(genre);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new ApiResponse(true, "Genre has been succesfully created!", new GenreDto(genreRepository.save(genre))));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @RequestBody GenreDto genreDto, @PathVariable Long id) {
        Optional<Genre> optionalGenre = genreRepository.findByIdAndUser(id, findUserByUserPrincipal(userPrincipal));
        if (optionalGenre.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "You don't have access to this date."), HttpStatus.FORBIDDEN);
        }
        Genre genre = optionalGenre.get();
        genre.setGenreDto(genreDto);

        ApiResponse apiResponse = genreService.validateGenre(genre);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new ApiResponse(true, "Genre has been succesfully updated!", new GenreDto(genreRepository.save(genre))));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        ApiResponse apiResponse = genreService.findById(id, findUserByUserPrincipal(userPrincipal));
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }

        Genre genre = (Genre) apiResponse.getObject();
        if (genre.getDeleted()) {
            return new ResponseEntity<>(new ApiResponse(false, "The genre has already been deleted!"), HttpStatus.BAD_REQUEST);
        }
        genre.setDeleted(true);
        
        return new ResponseEntity<>(new ApiResponse(true, "Genre has been deleted succesfully!", new GenreDto(genreRepository.save(genre))), HttpStatus.OK);
    }

}
