package kr.co.seedit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SeeditApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication utilityApplication = new SpringApplication(SeeditApiApplication.class);
        utilityApplication.addListeners(new ApplicationPidFileWriter());
        utilityApplication.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SeeditApiApplication.class);
    }

}
