package nl.ckramer.doorstroombackend.controller.interf;

import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class CrudController<T> extends AbstractController {

    public abstract ResponseEntity<?> view(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id);

    public abstract ResponseEntity<?> viewAll(@CurrentUser UserPrincipal userPrincipal);

    public abstract ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @RequestBody T dto, @PathVariable Long id);

    public abstract ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, @RequestBody T dto);

    public abstract ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long id);

}
