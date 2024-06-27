package master.awbd.order.service;

import master.awbd.order.model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "book")
public interface BookServiceProxy {
    @GetMapping("/book")
    Book getBook();
}
