package vn.aicrm.viettelpost.order.dto;

import java.util.List;

public record OrdersPageResponse<T>(
    List<T> content,   
    int pageNumber,     // Trang hiện tại
    int pageSize,       // Số lượng bản ghi 1 trang
    long totalElements, // Tổng số đơn hàng có trong DB
    int totalPages      // Tổng số trang
) {
    
}
