package master.awbd.order.service;

import master.awbd.order.exceptions.OrderNotFound;
import master.awbd.order.model.Order;
import master.awbd.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    public Order save(Order order){
        Order orderSave = orderRepository.save(order);
        return orderSave;
    }

    public List<Order> getAll() {
        return (List<Order>) orderRepository.findAll();
    }

    public boolean delete(Long id){
        Optional<Order> orderOptional = orderRepository.findById(id);

        if(! orderOptional.isPresent())
            throw new OrderNotFound("Order " + id + " not found!");

        orderRepository.delete(orderOptional.get());
        return true;
    }

    public Order getById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(! orderOptional.isPresent())
            throw new OrderNotFound("Order " + id + " not found!");

        return orderOptional.get();
    }

    public Order getByIdAndBookTitle(Long id, String bookTitle) {
        Order order = orderRepository.findByIdAndBookTitle(id, bookTitle);
        if(order == null)
            throw new OrderNotFound("Order " + id + " with book title " + bookTitle +" not found!");

        return order;
    }
}
