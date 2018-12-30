package org.hameister.filmwatcher.integration;

import org.hameister.filmwatcher.FilmWatcherApplication;
import org.hameister.filmwatcher.data.FilmRepository;
import org.hameister.filmwatcher.domain.Film;
import org.hameister.filmwatcher.domain.Provider;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FilmWatcherApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = FilmWatcherIntegrationTest.Initializer.class)
public class FilmWatcherIntegrationTest {

    public static String ip;

    private static int port;

    @LocalServerPort
    int springbootPort;

    @Autowired
    private FilmRepository filmRepository;

//  Elastic
//    @ClassRule
//    public static GenericContainer elasticsearch =
//            new GenericContainer("elasticsearch:5.6.10")
//                    .withExposedPorts(9300);


    @ClassRule
    public static GenericContainer mongoDB =
            new GenericContainer("mongo:3.1.5")
                    .withExposedPorts(27017);


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDB.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongoDB.getMappedPort(27017),
                    "spring.data.mongodb.database=TestDB"
            );
            values.applyTo(configurableApplicationContext);

            ip = mongoDB.getContainerIpAddress();
            port = mongoDB.getMappedPort(27017);
        }
    }

//    public static class ElasticInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        @Override
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues values = TestPropertyValues.of(
//                    "spring.elasticsearch.host=" + elasticsearch.getContainerIpAddress(),
//                    "elasticsearch.port=" + elasticsearch.getMappedPort(9300)
//            );
//            values.applyTo(configurableApplicationContext);
//
//            ip = elasticsearch.getContainerIpAddress();
//            port = elasticsearch.getMappedPort(9300);
//        }


    @Test
    public void aSavedFilmShouldBeFound() {
        Long uniqueID = UUID.randomUUID().getMostSignificantBits();
        Film film = new Film(uniqueID,"Star Wars", new Provider("MyDVD"));
        LocalDate watched = LocalDate.of(2018,12,31);
        film.setWatchdate(watched);

        Mono<Film> saved = filmRepository.save(film);

        Film block = saved.block();
        System.out.println(block);

        Flux<Film> myDVD = filmRepository.findByProvider(new Provider("MyDVD"));
        List<Film> filmList = myDVD.collectList().block();
        Assertions.assertEquals(1, filmList.size());
        Assertions.assertEquals(film,filmList.get(0));

    }

    @Test
    public void aSavedFilmShouldBeFoundViaRestController() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://"+ip+":"+springbootPort+"/film/0";
        ResponseEntity<Film> forObject = restTemplate.getForEntity(url, Film.class);
        Film film = forObject.getBody();
        Assertions.assertEquals(film.getName(),"Terminator");
    }
}