package sopt.study.testcode.geunpyo.spring.api;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.study.testcode.geunpyo.spring.api.controller.product.response.ProductResponse;

@Getter
public class ApiResponse<T> {
	private int code;
	private HttpStatus status;
	private String message;
	private T data;

	public ApiResponse(HttpStatus status, String message, T data) {
		this.code = status.value();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
		return new ApiResponse<>(httpStatus, message, data);
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
		return of(httpStatus, httpStatus.name(), data);
	}

	public static <T> ApiResponse<T> ok(T data) {
		return of(HttpStatus.OK, data);
	}
}
