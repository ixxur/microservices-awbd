package master.awbd.book.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("book")
@Getter
@Setter
public class PropertiesConfig {
    private String title;
    private String author;
    private String genre;
    private Integer versionId;
}
