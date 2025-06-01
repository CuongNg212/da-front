package jwd.practice.shopservice.service.Service;

import jakarta.servlet.http.HttpServletResponse;
import jwd.practice.shopservice.dto.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class formatResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        ApiResponse<Object> apiResponse = new ApiResponse<Object>();
        apiResponse.setCode(status);
        // Kiểm tra xem dữ liệu trả về có phải là mảng byte (dữ liệu nhị phân, file)
        if (body instanceof byte[]) {
            // Trả về nguyên vẹn dữ liệu byte mà không thay đổi
            return body;
        }
        if (body instanceof String)
        {
            return body;
        }
        if(status >=400)
        {
            return body;
        }
        else {
            apiResponse.setResult(body);
            apiResponse.setMessage("call api success");
        }
        return apiResponse;

    }
}
