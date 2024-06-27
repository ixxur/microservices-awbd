package master.awbd.order.service;

import master.awbd.order.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {
    Order getById(Long id);
    Order getByIdAndBookTitle(Long id, String bookTitle);
    Order save(Order order);
    List<Order> getAll();
    boolean delete(Long id);
}
