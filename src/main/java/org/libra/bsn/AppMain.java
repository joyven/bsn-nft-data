package org.libra.bsn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@SpringBootApplication
@MapperScan(basePackages = "org.libra.bsn.dao")
public class AppMain {
    public static void main(String[] args) {

        SpringApplication.run(AppMain.class, args);
    }
}
