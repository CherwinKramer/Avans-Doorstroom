package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface CrudController<T> {

    public abstract ResponseEntity<?> view(@CurrentUser CurrentUser user, @PathVariable Long id);

    public abstract ResponseEntity<?> viewAll(@CurrentUser CurrentUser user);

    public abstract ResponseEntity<?> create(@CurrentUser CurrentUser user, @RequestBody T request);

    public abstract ResponseEntity<?> delete(@CurrentUser CurrentUser user, @PathVariable Long id);

}
