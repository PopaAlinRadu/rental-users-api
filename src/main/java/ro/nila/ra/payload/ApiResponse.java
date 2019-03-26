package ro.nila.ra.payload;

import com.fasterxml.jackson.annotation.JsonView;
import ro.nila.ra.model.Account;

public class ApiResponse {

    private Boolean success;
    private String message;
    private String resourceLocation;

    public ApiResponse(Boolean success, String message, String resourceLocation) {
        this.success = success;
        this.message = message;
        this.resourceLocation = resourceLocation;
    }

    @JsonView(Account.WithoutPasswordView.class)
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonView(Account.WithoutPasswordView.class)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonView(Account.WithoutPasswordView.class)
    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
