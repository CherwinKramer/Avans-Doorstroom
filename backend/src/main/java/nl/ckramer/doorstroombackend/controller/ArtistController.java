package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Role;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.request.UserRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.ArtistRepository;
import nl.ckramer.doorstroombackend.repository.RoleRepository;
import nl.ckramer.doorstroombackend.repository.UserRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.JwtTokenProvider;
import nl.ckramer.doorstroombackend.service.ArtistService;
import nl.ckramer.doorstroombackend.service.RoleService;
import nl.ckramer.doorstroombackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/artist")
public class ArtistController implements CrudController<Artist>{

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistService artistService;

    @Override
    public ResponseEntity<?> view(CurrentUser user, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> viewAll(CurrentUser user) {
        return null;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@CurrentUser CurrentUser user, @Valid @RequestBody Artist artist) {

        ApiResponse apiResponse = artistService.validateArtist(artist);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "The daa is not valid, please try again!"), HttpStatus.BAD_REQUEST);
        }

        artist = artistRepository.save(artist);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@CurrentUser CurrentUser user, @PathVariable Long id) {
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
