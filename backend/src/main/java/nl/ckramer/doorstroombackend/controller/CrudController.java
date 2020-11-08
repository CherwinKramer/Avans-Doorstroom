package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;

public abstract class CrudController<T> extends AbstractController {

    @Transactional
    @GetMapping("/{id}")
    public abstract ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id);

    public abstract ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal);

    public abstract ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @RequestBody T request);

    public abstract ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id);

}
