package ro.nila.ra.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private Boolean status;
    private T data;

    public ApiResponse(Boolean status, T data) {
        this.status = status;
        this.data = data;
    }
}
