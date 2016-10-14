package de.jeha.j7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jenshadlich@googlemail.com
 */
@SpringBootApplication
public class J7Application {

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(J7Application.class);
        application.run(args);
    }

}
