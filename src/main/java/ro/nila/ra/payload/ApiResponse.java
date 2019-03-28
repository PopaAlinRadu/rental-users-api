package ro.nila.ra.payload;

import com.fasterxml.jackson.annotation.JsonView;
import ro.nila.ra.model.Account;
import ro.nila.ra.model.view.Views;

public class ApiResponse {

    private Boolean success;
    private String message;
    private String resourceLocation;

    public ApiResponse(Boolean success, String message, String resourceLocation) {
        this.success = success;
        this.message = message;
        this.resourceLocation = resourceLocation;
    }

    @JsonView(Views.WithRole.class)
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonView(Views.WithRole.class)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonView(Views.WithRole.class)
    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
