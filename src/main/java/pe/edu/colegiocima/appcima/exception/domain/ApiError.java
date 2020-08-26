package pe.edu.colegiocima.appcima.exception.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import pe.edu.colegiocima.appcima.exception.LowerCaseClassNameResolver;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT,use = JsonTypeInfo.Id.CUSTOM,property = "error",visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ApiError {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    public ApiError() { timestamp = LocalDateTime.now(); }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Error inesperado";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status,String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    private void addSubError(ApiSubError subError){
        if(subError == null){
            subErrors = new ArrayList<>();
        }
        subErrors.add(subError);
    }

    public void addValidationError(String object, String field, Object rejectedValue, String message){
        this.addSubError(new ApiValidationError(object,field,rejectedValue,message));
    }

    public void addValidationError(String object, String message){
        this.addSubError(new ApiValidationError(object,message));
    }

    private void addValidationError(FieldError fieldError){
        this.addSubError(new ApiValidationError(fieldError.getObjectName(),fieldError.getField(),fieldError.getRejectedValue(),fieldError.getDefaultMessage()));
    }

    public  void addValidationErrors(List<FieldError> lError){
        lError.forEach(this::addValidationError);
    }

    private void addValidationError(ObjectError objectError){
        this.addSubError(new ApiValidationError(objectError.getObjectName(),objectError.getDefaultMessage()));
    }

    public void addValidationError(List<ObjectError> globalErrors){
        globalErrors.forEach(this::addValidationError);
    }

    private void addValidationError(ConstraintViolation<?> constraintViolation){
        this.addValidationError(
                constraintViolation.getRootBeanClass().getSimpleName(),
                ((PathImpl)constraintViolation.getPropertyPath()).getLeafNode().asString(),
                constraintViolation.getInvalidValue(),
                constraintViolation.getMessage()
        );
    }

    public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations){
        constraintViolations.forEach(this::addValidationError);
    }


}

