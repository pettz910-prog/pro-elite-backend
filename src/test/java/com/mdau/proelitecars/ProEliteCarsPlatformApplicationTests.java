package com.mdau.proelitecars;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/proelite_db",
    "spring.jpa.hibernate.ddl-auto=none"
})
class ProEliteCarsPlatformApplicationTests {

    @Test
    void contextLoads() {
    }
}