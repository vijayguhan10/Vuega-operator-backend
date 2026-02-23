package net.vuega.vuega_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> {

    private int status;
    private String message;
    private T  data;

    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> created(T data) {
        return ResponseDto.<T>builder()
                .status(201)
                .message("Created")
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> notFound(String message) {
        return ResponseDto.<T>builder()
                .status(404)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResponseDto<T> error(int status, String message) {
        return ResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
