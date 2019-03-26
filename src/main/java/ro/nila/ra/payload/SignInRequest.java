package ro.nila.ra.payload;

import javax.validation.constraints.NotBlank;

public class SignInRequest {

    @NotBlank
    private String usernameOrEmail;
    @NotBlank
    private String password;

    public SignInRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

}
