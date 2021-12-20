package com.casique.springapp.awssecretsmanager.mysql.configuration;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MySQLConfig {

    private final Gson gson = new Gson();

    @Value("${aurora_mysql.db_name:''}")
    private String dbName;

    @Value("${aurora_mysql.secret_name:''}")
    private String secretName;

    @Value("${aurora_mysql.package_to_load:''}")
    private String packageToLoad;

    @Value("${aurora_mysql.default_region:''}")
    private String defaultRegion;

    @Bean
    @ConditionalOnProperty(value = "aurora_mysql.enabled")
    public DataSource dataSource(){
        final AwsSecret dbCredentials = getSecret();
        return DataSourceBuilder
                .create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:"+dbCredentials.getEngine()+"://" +dbCredentials.getHost()+":"+dbCredentials.getPort()+"/"+dbName)
                .username(dbCredentials.username)
                .password(dbCredentials.password)
                .build();
    }

    private AwsSecret getSecret(){
        try {
            Region region = Region.of(defaultRegion);
            SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                    .region(region)
                    .build();
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
            String secret = valueResponse.secretString();
            return gson.fromJson(secret, AwsSecret.class);

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public class AwsSecret{
        private String username;
        private String password;
        private String host;
        private String engine;
        private String port;
        private String dbInstanceIdentifier;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getEngine() {
            return engine;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getDbInstanceIdentifier() {
            return dbInstanceIdentifier;
        }

        public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
            this.dbInstanceIdentifier = dbInstanceIdentifier;
        }
    }

    @Bean
    @ConditionalOnProperty(value = "aurora_mysql.enabled")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }

    @Bean
    @ConditionalOnProperty(value = "aurora_mysql.enabled")
    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        return jpaVendorAdapter;
    }

    @Bean
    @ConditionalOnProperty(value = "aurora_mysql.enabled")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
        String []packages = packageToLoad.split(";");
        lemfb.setDataSource(dataSource());
        lemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lemfb.setPackagesToScan(packages);
        return lemfb;
    }
}

