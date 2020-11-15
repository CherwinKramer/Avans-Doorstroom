package nl.ckramer.doorstroombackend.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class AlbumRequest {

    private Long id;
    private String name;

    @JsonAlias("artist")
    private ArtistRequest artistRequest;

}
