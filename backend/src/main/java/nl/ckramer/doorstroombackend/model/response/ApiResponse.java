package nl.ckramer.doorstroombackend.model.response;

import lombok.Data;

@Data
public class ApiResponse {

    private Boolean success;
    private String message;
    private Long createdId;
    private Object object;

    public ApiResponse(Boolean success) {
        this.success = success;
    }

    public ApiResponse(Boolean success, Object object) {
        this.success = success;
        this.object = object;
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(Boolean success, String message, Long createdId) {
        this.success = success;
        this.message = message;
        this.createdId = createdId;
    }

    public ApiResponse(Boolean success, String message, Object object) {
        this.success = success;
        this.message = message;
        this.object = object;
    }


}
