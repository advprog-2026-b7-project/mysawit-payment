package id.ac.ui.cs.advprog.mysawit.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiSuccessResponse<T> {
    private String status;
    private T data;

    public ApiSuccessResponse() {
        this.status = "success";
    }

    public ApiSuccessResponse(T data) {
        this.status = "success";
        this.data = data;
    }
}
