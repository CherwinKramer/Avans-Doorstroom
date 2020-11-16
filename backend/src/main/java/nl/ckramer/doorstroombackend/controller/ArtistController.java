package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.request.ArtistRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.AlbumRepository;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import nl.ckramer.doorstroombackend.repository.SongRepository;
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
public class ArtistController extends CrudController<ArtistRequest>{

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

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

    @Override
    @Transactional
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);
        List<Artist> artistList = artistRepository.findAllByDeletedFalseAndUser(user);
        return ResponseEntity.ok(new ApiResponse(true, artistList));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<?> getAlbumsByArtist(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        ApiResponse apiResponse = artistService.findById(id, user);

        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        } else {
            Artist artist = (Artist) apiResponse.getObject();
            List<Album> albumList = albumRepository.findAllByArtistAndDeletedFalse(artist);
            for (Album album : albumList) {
                album.setSongs(songRepository.findAllByAlbumAndDeletedFalse(album));
            }
            return ResponseEntity.ok(new ApiResponse(true, albumList));
        }
    }

    @Transactional
    @GetMapping("/{id}/songs")
    public ResponseEntity<?> getSongsByArtist(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        ApiResponse apiResponse = artistService.findById(id, user);

        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        } else {
            Artist artist = (Artist) apiResponse.getObject();
            List<Song> songList = songRepository.findAllByArtistAndDeletedFalse(artist);
            return ResponseEntity.ok(new ApiResponse(true, songList));
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @RequestBody ArtistRequest request) {
        Artist artist = new Artist();
        artist.setArtistRequest(request);

        ApiResponse apiResponse = artistService.validateArtist(artist);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        artist = artistRepository.save(artist);
        return ResponseEntity.ok(new ApiResponse(true, "Artist has been succesfully created!", artist));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ArtistRequest artistRequest, @PathVariable Long id) {
        Optional<Artist> oldOptional = artistRepository.findById(id);
        Artist artist = oldOptional.orElse(null);
        if (artist != null) artist.setArtistRequest(artistRequest);

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
