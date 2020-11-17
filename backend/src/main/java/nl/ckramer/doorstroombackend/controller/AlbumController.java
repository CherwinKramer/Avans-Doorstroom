package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.controller.interf.CrudController;
import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.dto.AlbumDto;
import nl.ckramer.doorstroombackend.model.dto.SongDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/album")
public class AlbumController extends CrudController<AlbumDto> {

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
        Optional<Album> albumOptional = albumRepository.findByIdAndUser(id, user);

        if (albumOptional.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(true, new AlbumDto(albumOptional.get())));
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have access to view this album"), HttpStatus.FORBIDDEN);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        List<AlbumDto> albumDtoList = new ArrayList<>();
        for (Album album : albumRepository.findAllByUserAndDeletedFalse(findUserByUserPrincipal(userPrincipal))) {
            albumDtoList.add(new AlbumDto(album));
        }
        return ResponseEntity.ok(new ApiResponse(true, albumDtoList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody AlbumDto albumDto) {
        Album album = new Album();
        if (albumDto.getArtistDto() != null && albumDto.getArtistDto().getId() != null) {
            Optional<Artist> artistOptional = artistRepository.findById(albumDto.getArtistDto().getId());
            album.setArtist(artistOptional.isEmpty() ? null : artistOptional.get());
        }
        album.setName(albumDto.getName());

        ApiResponse apiResponse = albumService.validateAlbum(album);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        album = albumRepository.save(album);
        return ResponseEntity.ok(new ApiResponse(true, "Album has been succesfully created!", new AlbumDto(album)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody AlbumDto albumDto, @PathVariable Long id) {
        Optional<Album> albumOptional = albumRepository.findById(id);
        if (!albumOptional.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        Album album = albumOptional.get();
        if (albumDto.getArtistDto() != null && albumDto.getArtistDto().getId() != null) {
            Optional<Artist> artistOptional = artistRepository.findById(albumDto.getArtistDto().getId());
            album.setArtist(artistOptional.isEmpty() ? null : artistOptional.get());
        }
        album.setName(albumDto.getName());

        ApiResponse apiResponse = albumService.validateAlbum(album);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The data is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true, "Album has been succesfully updated!", new AlbumDto(albumRepository.save(album))));
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
                return new ResponseEntity<>(new ApiResponse(true, "Album has been deleted succesfully!", new AlbumDto(album)), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "Could not delete the album!"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<?> getSongsByAlbum(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);
        ApiResponse apiResponse = albumService.findById(id, user);

        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        } else {
            Album album = (Album) apiResponse.getObject();
            List<SongDto> songDtoList= new ArrayList<>();
            for (Song song : songRepository.findAllByAlbumAndDeletedFalse(album)) {
                songDtoList.add(new SongDto(song));
            }
            return ResponseEntity.ok(new ApiResponse(true, songDtoList));
        }
    }
}
