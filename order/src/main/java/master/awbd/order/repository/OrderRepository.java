package master.awbd.order.repository;

import master.awbd.order.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findById(Long id);
    Order findByIdAndBookTitle(Long id, String bookTitle);
}
