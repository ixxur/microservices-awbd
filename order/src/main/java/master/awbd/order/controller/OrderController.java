package master.awbd.order.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import master.awbd.order.model.Book;
import master.awbd.order.model.Order;
import master.awbd.order.service.BookServiceProxy;
import master.awbd.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    BookServiceProxy bookServiceProxy;


    @GetMapping(value= "/order/list")
    public CollectionModel<Order> getAll() {
        List<Order> orders = orderService.getAll();
        
        for(final Order order : orders){
            Link selfLink = linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel();
            order.add(selfLink);

            Link deleteLink = linkTo(methodOn(OrderController.class).deleteOrder(order.getId())).withRel("deleteOrder");
            order.add(deleteLink);

            Link postLink = linkTo(methodOn(OrderController.class).saveOrder(order)).withRel("saveOrder");
            order.add(postLink);

            Link putLink = linkTo(methodOn(OrderController.class).updateOrder(order)).withRel("updateOrder");
            order.add(putLink);
        }

        Link link = linkTo(methodOn(OrderController.class).getAll()).withSelfRel();
        CollectionModel<Order> result = CollectionModel.of(orders, link);
        return result;
    }

    @GetMapping("/order/{orderId}/bookTitle/{bookTitle}")
    public Order getByOrderIdAndBookTitle(@PathVariable Long orderId,
                                            @PathVariable String bookTitle){
        Order order = orderService.getByIdAndBookTitle(orderId, bookTitle);

        Link selfLink = linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel();
        order.add(selfLink);

        Book book = bookServiceProxy.getBook();
        log.info(String.valueOf(book.getVersionId()));
        order.setQuantity(order.getQuantity() + book.getVersionId());

        return order;
    }
    
    @PostMapping("/order")
    public ResponseEntity<Order> saveOrder(@Valid @RequestBody Order order){
        Order savedOrder = orderService.save(order);
        URI locationUri =ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderId}").buildAndExpand(savedOrder.getId())
                .toUri();

        Link selfLink = linkTo(methodOn(OrderController.class).getOrder(savedOrder.getId())).withSelfRel();
        savedOrder.add(selfLink);

        return ResponseEntity.created(locationUri).body(savedOrder);
    }

    @PutMapping("/order")
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody Order order){
        Order updatedOrder = orderService.save(order);

        Link selfLink = linkTo(methodOn(OrderController.class).getOrder(updatedOrder.getId())).withSelfRel();
        updatedOrder.add(selfLink);

        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "delete order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "order deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)})
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId){

        boolean deleted = orderService.delete(orderId);

        if (deleted) {
            log.info("Order " + orderId + " successfully deleted.");
            return ResponseEntity.noContent().build();
        } else {
            log.error("Could not delete order " + orderId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/order/{orderId}")
    @CircuitBreaker(name="bookById", fallbackMethod = "getOrderFallback")
    public Order getOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);

        Book book = bookServiceProxy.getBook();
        log.info(String.valueOf(book.getVersionId()));

        if(book.getVersionId() != null){
            if(book.getTitle().equalsIgnoreCase(order.getBookTitle())){
                log.info("This title has a 20% discount today!!");
                order.setPrice(order.getPrice() * 0.8);
            } else {
                order.setPrice(order.getPrice() * book.getVersionId());
                log.info("You should've bought a "+ book.getAuthor() + " book :(");
            }
        }

        return order;
    }

    public Order getOrderFallback(Long orderId, Throwable throwable) {
        Order order = orderService.getById(orderId);
        return order;
    }
}
