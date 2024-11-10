package whynotthis.domain.common;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true,"요청이 성공적으로 처리되었습니다.",data);
    }

    public static <T> ApiResponse<T> success(String message,T data) {
        return new ApiResponse<>(true,message,data);
    }

    public static <T> ApiResponse<T> failure(T data) {
        return new ApiResponse<>(false,"요청에 실패했습니다.",data);
    }

    public static <T> ApiResponse<T> failure(String message,T data) {
        return new ApiResponse<>(false,message,data);
    }
}
