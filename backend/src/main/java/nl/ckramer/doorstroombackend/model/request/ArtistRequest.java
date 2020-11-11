package nl.ckramer.doorstroombackend.model.request;

import lombok.Data;

@Data
public class ArtistRequest {

    private Long id;
    private String name;
    private String surname;
    private String place;

}
