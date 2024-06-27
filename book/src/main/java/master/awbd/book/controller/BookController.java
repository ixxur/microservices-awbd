package master.awbd.book.controller;

import master.awbd.book.config.PropertiesConfig;
import master.awbd.book.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    @Autowired
    private PropertiesConfig configuration;

//    @GetMapping("/book")
//    public Book getBook(@RequestParam(value = "title", defaultValue = "Unknown") String title) {
//        return new Book(title, configuration.getAuthor(), configuration.getGenre());
//    }

    @GetMapping("/book")
    public Book getBook() {
        return new Book(configuration.getTitle(), configuration.getAuthor(), configuration.getGenre(), configuration.getVersionId());
    }
}
