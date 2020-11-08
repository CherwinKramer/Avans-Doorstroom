package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.AlbumRepository;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artist")
public class ArtistController extends CrudController<Artist>{

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    ArtistService artistService;

    @Override
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        Optional<Artist> artistOptional = artistRepository.findById(id);

        if (artistOptional.isPresent()) {
            Artist artist = artistOptional.get();
            if (artist.getUser() == user) {
                return ResponseEntity.ok(new ApiResponse(true, artist));
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have access to view this artist"), HttpStatus.FORBIDDEN);
    }

    @Transactional
    @GetMapping("/{id}/albums")
    public ResponseEntity<?> getAlbumsByArtist(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        Optional<Artist> artistOptional = artistRepository.findById(id);

        if (artistOptional.isPresent()) {
            Artist artist = artistOptional.get();
            if (artist.getUser() == user) {
                return ResponseEntity.ok(new ApiResponse(true, artist));
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have access to view this artist"), HttpStatus.FORBIDDEN);
    }

    @Override
    @Transactional
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);
        List<Artist> artistList = artistRepository.findAllByDeletedFalseAndUser(user);
        return ResponseEntity.ok(new ApiResponse(true, artistList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody Artist artist) {

        ApiResponse apiResponse = artistService.validateArtist(artist);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        artist = artistRepository.save(artist);
        return ResponseEntity.ok(new ApiResponse(true, "Artist has been succesfully created!", artist));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        if (id != null) {
            Optional<Artist> artistOptional = artistRepository.findById(id);
            if (artistOptional.isPresent()) {
                Artist artist = artistOptional.get();
                if (artist.getDeleted()) {
                    return new ResponseEntity<>(new ApiResponse(false, "The artist has already been deleted!"), HttpStatus.BAD_REQUEST);
                }
                artist.setDeleted(true);
                artistRepository.save(artist);
                return new ResponseEntity<>(new ApiResponse(true, "Artist has been deleted succesfully!", artist), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "Could not delete the artist!"), HttpStatus.BAD_REQUEST);
    }
}
