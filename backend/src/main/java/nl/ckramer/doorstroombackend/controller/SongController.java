package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/song")
public class SongController extends CrudController<Song>{

    @Autowired
    SongRepository songRepository;

    @Autowired
    SongService songService;

    @Override
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id) {
        User user = findUserByUserPrincipal(userPrincipal);

        ApiResponse apiResponse = songService.findById(id, user);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    @Transactional
    @GetMapping
    public ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal) {
        User user = findUserByUserPrincipal(userPrincipal);
        List<Song> songList = songRepository.findAllByUserAndDeletedFalse(user);
        return ResponseEntity.ok(new ApiResponse(true, songList));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody Song song) {

        ApiResponse apiResponse = songService.validateSong(song);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        song = songRepository.save(song);
        return ResponseEntity.ok(new ApiResponse(true, "Song has been succesfully created!", song));
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
        return new ResponseEntity<>(new ApiResponse(true, "Song has been deleted succesfully!", song), HttpStatus.OK);
    }
}
