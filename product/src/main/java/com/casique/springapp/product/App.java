package com.casique.springapp.product;

import com.casique.springapp.awssecretsmanager.mysql.configuration.MySQLConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@Import(MySQLConfig.class)
public class App
{
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
