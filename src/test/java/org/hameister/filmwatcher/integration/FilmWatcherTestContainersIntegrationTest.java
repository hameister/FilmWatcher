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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FilmWatcherApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = FilmWatcherTestContainersIntegrationTest.Initializer.class)
public class FilmWatcherTestContainersIntegrationTest {

    public static String mongoIp;

    private static int port;

    @LocalServerPort
    int springbootPort;

    @Autowired
    private FilmRepository filmRepository;

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

            mongoIp = mongoDB.getContainerIpAddress();
            port = mongoDB.getMappedPort(27017);
        }
    }


    @Test
    public void checkId() {

        Film film = new Film( "Star Wars", new Provider("MyDVD"));
        LocalDate watched = LocalDate.of(2018, 12, 31);
        film.setWatchdate(watched);

        Mono<Film> saved = filmRepository.save(film);

        Film block = saved.block();

        Assertions.assertNotNull(block.getId());
    }


    @Test
    public void aSavedFilmShouldBeFound() {
        Film film = new Film( "Star Wars", new Provider("MyDVD"));
        LocalDate watched = LocalDate.of(2018, 12, 31);
        film.setWatchdate(watched);

        Mono<Film> saved = filmRepository.save(film);

        Film block = saved.block();

        Flux<Film> myDVD = filmRepository.findByProvider(new Provider("MyDVD"));
        List<Film> filmList = myDVD.collectList().block();
        Assertions.assertEquals(1, filmList.size());
        Assertions.assertEquals(film, filmList.get(0));

    }


    @Test
    public void importedFilmsViaRestControllerShouldStoredInDB() {
        //Given
        Long uniqueID = UUID.randomUUID().getMostSignificantBits();
        List<Film> films = new ArrayList<>();
        films.add(new Film("Star Wars", new Provider("MyDVD2")));
        films.add(new Film( "Star Wars II", new Provider("MyDVD2")));
        films.add(new Film( "Star Wars III", new Provider("MyDVD2")));
        HttpEntity<List<Film>> request = new HttpEntity<>(films);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mongoIp + ":" + springbootPort + "/film";

        //When
        ResponseEntity<List> exchange = restTemplate.exchange(url, HttpMethod.POST, request, List.class);

        //Then - Check if import wassuccessful
        ResponseEntity<List> forObject = restTemplate.getForEntity(url, List.class);
        List<Film> filmList = forObject.getBody();
        // 6 default films and 3 from this test
        Assertions.assertEquals(9, filmList.size());
    }

    @Test
    public void filterFilmsByWatchYearShouldReturnListWithFilmsOfAYear() {
        //Given
        importTestdataSet();

        //When
        List<Film> films = filterFilmsOfYear(2025);

        //Then
        Assertions.assertEquals(1, films.size());
    }

    private List<Film> filterFilmsOfYear(int year) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mongoIp + ":" + springbootPort + "/film/year/" + year;

        ResponseEntity<List> forObject = restTemplate.getForEntity(url, List.class);
        List<Film> filmList = forObject.getBody();
        return filmList;
    }

    private void importTestdataSet() {
        List<Film> films = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Film f = new Film( "Film" + i, new Provider("DVD" + i));
            f.setWatchdate(LocalDate.of(2017, 10, 1));

            films.add(f);
        }

        Film f = new Film( "Star Wars", new Provider("MyDVD2"));
        f.setWatchdate(LocalDate.of(2018, 9, 4));
        films.add(f);

        Film f2 = new Film( "Star Trek", new Provider("MyDVD2"));
        f2.setWatchdate(LocalDate.of(2025, 7, 15));
        films.add(f2);

        HttpEntity<List<Film>> request = new HttpEntity<>(films);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mongoIp + ":" + springbootPort + "/film";

        restTemplate.exchange(url, HttpMethod.POST, request, List.class);

    }

    @Test
    public void  searchByName() {
        // Given
        List<Film> films = new ArrayList<>();
        Film f = new Film( "Star Wars", new Provider("MyDVD2"));
        f.setWatchdate(LocalDate.of(2019, 9, 4));
        films.add(f);

        Film f2 = new Film( "Star Trek", new Provider("MyDVD2"));
        f2.setWatchdate(LocalDate.of(2024, 7, 15));
        films.add(f2);

        HttpEntity<List<Film>> request = new HttpEntity<>(films);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mongoIp + ":" + springbootPort + "/film";

        restTemplate.exchange(url, HttpMethod.POST, request, List.class);

        //When
        List<Film> star_wars = findByName("Star Trek");

        //Then
        Assertions.assertEquals(1, star_wars.size());

    }

    private List<Film> findByName(String name) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mongoIp + ":" + springbootPort + "/film/name/" + name;

        ResponseEntity<List> forObject = restTemplate.getForEntity(url, List.class);
        List<Film> filmList = forObject.getBody();
        return filmList;
    }
}