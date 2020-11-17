package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.controller.interf.CrudController;
import nl.ckramer.doorstroombackend.entity.*;
import nl.ckramer.doorstroombackend.model.dto.SongDto;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.AlbumRepository;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import nl.ckramer.doorstroombackend.repository.GenreRepository;
import nl.ckramer.doorstroombackend.repository.SongRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/song")
public class SongController extends CrudController<SongDto> {

    @Autowired
    SongRepository songRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongService songService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);

        ApiResponse apiResponse = songService.findById(id, user);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }

        SongDto songDto = new SongDto((Song) apiResponse.getObject());
        return ResponseEntity.ok(new ApiResponse(true, songDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);

        List<SongDto> songDtoList = new ArrayList<>();
        for (Song song : songRepository.findAllByUserAndDeletedFalse(user)) {
            songDtoList.add(new SongDto(song));
        }

        return ResponseEntity.ok(new ApiResponse(true, songDtoList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody SongDto songDto) {
        Song song = initializeSongByDto(songDto);

        ApiResponse apiResponse = songService.validateSong(song);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true, "Song has been succesfully created!", new SongDto(songRepository.save(song))));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @RequestBody SongDto songDto, @PathVariable Long id) {
        Song song = initializeSongByDto(songDto);

        ApiResponse apiResponse = songService.validateSong(song);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true, "Song has been succesfully updated!", new SongDto(songRepository.save(song))));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);

        ApiResponse apiResponse = songService.findById(id, user);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }

        Song song = (Song) apiResponse.getObject();
        if (song.getDeleted()) {
            return new ResponseEntity<>(new ApiResponse(false, "The song has already been deleted!"), HttpStatus.BAD_REQUEST);
        }
        song.setDeleted(true);
        songRepository.save(song);
        return new ResponseEntity<>(new ApiResponse(true, "Song has been deleted succesfully!", new SongDto(song)), HttpStatus.OK);
    }

    private Song initializeSongByDto(SongDto songDto) {
        Song song = null;
        if (songDto.getId() != null) {
            song = songRepository.getOne(songDto.getId());
        }
        if (song == null) song = new Song();
        song.setName(songDto.getName());
        song.setUrl(songDto.getUrl());

        Optional<Artist> songOptional = songDto.getArtistDto().getId() != null ? artistRepository.findById(songDto.getArtistDto().getId()) : Optional.empty();
        song.setArtist(songOptional.orElse(null));

        Optional<Genre> genreOptional = songDto.getGenreDto().getId() != null ? genreRepository.findById(songDto.getGenreDto().getId()) : Optional.empty();
        song.setGenre(genreOptional.orElse(null));

        Optional<Album> albumOptional = songDto.getAlbumDto().getId() != null ? albumRepository.findById(songDto.getAlbumDto().getId()) : Optional.empty();
        song.setAlbum(albumOptional.orElse(null));

        if (songDto.getFeaturedArtistIds() != null && !songDto.getFeaturedArtistIds().isEmpty()) {
            List<Artist> featuredArtistList = artistRepository.findAllById(songDto.getFeaturedArtistIds());
            song.setFeaturedArtists(new HashSet<>(featuredArtistList));
        }
        return song;
    }

}
