package vn.aicrm.shared;

import java.sql.SQLException;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Business validation must not expose SQL or turn a duplicate SKU into a server error. */
@RestControllerAdvice(assignableTypes=RetailController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RetailErrors {
  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<?> constraint(DataIntegrityViolationException error){
    String state="";for(Throwable cause=error;cause!=null;cause=cause.getCause())if(cause instanceof SQLException sql){state=sql.getSQLState();break;}
    boolean duplicate="23505".equals(state);
    int status=duplicate?409:422;
    return ResponseEntity.status(status).body(Map.of("status",status,"code",duplicate?"DUPLICATE_DATA":"INVALID_DATA","detail",duplicate?"Mã hoặc dữ liệu này đã tồn tại. Kiểm tra SKU, barcode, đối tác hoặc chứng từ liên quan.":"Dữ liệu không đáp ứng quy tắc lưu trữ. Kiểm tra tham chiếu, số tiền, số lượng và độ dài thông tin."));
  }
}
