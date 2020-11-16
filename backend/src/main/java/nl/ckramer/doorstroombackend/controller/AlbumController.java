package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.request.AlbumRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.AlbumRepository;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import nl.ckramer.doorstroombackend.repository.SongRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/album")
public class AlbumController extends CrudController<AlbumRequest>{

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    AlbumService albumService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        Optional<Album> albumOptional = albumRepository.findById(id);

        if (albumOptional.isPresent()) {
            Album album = albumOptional.get();
            if (album.getUser() == user) {
                return ResponseEntity.ok(new ApiResponse(true, album));
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have access to view this album"), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<?> getSongsByAlbum(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        ApiResponse apiResponse = albumService.findById(id, user);

        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        } else {
            Album album = (Album) apiResponse.getObject();
            List<Song> songList = songRepository.findAllByAlbumAndDeletedFalse(album);
            return ResponseEntity.ok(new ApiResponse(true, songList));
        }
    }

    @Override
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);
        List<Album> albumList = albumRepository.findAllByUserAndDeletedFalse(user);
        return ResponseEntity.ok(new ApiResponse(true, albumList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody AlbumRequest albumRequest) {
        Album album = new Album();
        if (albumRequest.getArtistRequest() != null && albumRequest.getArtistRequest().getId() != null) {
            Optional<Artist> artistOptional = artistRepository.findById(albumRequest.getArtistRequest().getId());
            album.setArtist(artistOptional.isEmpty() ? null : artistOptional.get());
        }
        album.setAlbumRequest(albumRequest);

        ApiResponse apiResponse = albumService.validateAlbum(album);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        album = albumRepository.save(album);
        return ResponseEntity.ok(new ApiResponse(true, "Album has been succesfully created!", album));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody AlbumRequest albumRequest, @PathVariable Long id) {
        Optional<Album> albumOptional = albumRepository.findById(id);
        if (!albumOptional.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        Album album = albumOptional.get();
        if (albumRequest.getArtistRequest() != null && albumRequest.getArtistRequest().getId() != null) {
            Optional<Artist> artistOptional = artistRepository.findById(albumRequest.getArtistRequest().getId());
            album.setArtist(artistOptional.isEmpty() ? null : artistOptional.get());
        }
        album.setAlbumRequest(albumRequest);

        ApiResponse apiResponse = albumService.validateAlbum(album);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        album = albumRepository.save(album);
        return ResponseEntity.ok(new ApiResponse(true, "Album has been succesfully updated!", album));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        if (id != null) {
            Optional<Album> albumOptional = albumRepository.findById(id);
            if (albumOptional.isPresent()) {
                Album album = albumOptional.get();
                if (album.getDeleted()) {
                    return new ResponseEntity<>(new ApiResponse(false, "The artist has already been deleted!"), HttpStatus.BAD_REQUEST);
                }
                album.setDeleted(true);
                albumRepository.save(album);
                return new ResponseEntity<>(new ApiResponse(true, "Album has been deleted succesfully!", album), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "Could not delete the album!"), HttpStatus.BAD_REQUEST);
    }
}
