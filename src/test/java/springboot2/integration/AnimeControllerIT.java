package springboot2.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import springboot2.domain.Anime;
import springboot2.domain.DevDojoUser;
import springboot2.repository.AnimeRepository;
import springboot2.repository.DevDojoUserRepository;
import springboot2.requests.AnimePostRequestBody;
import springboot2.util.AnimeCreator;
import springboot2.util.AnimePostRequestBodyCreator;
import springboot2.wrapper.PageableResponse;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
	
	@Autowired
	@Qualifier(value="testRestTemplateRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;
	@Autowired
	@Qualifier(value="testRestTemplateRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;
	@Autowired
	private AnimeRepository animeRepository;
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;
	
	private static final DevDojoUser USER = DevDojoUser.builder()
			.name("Player 2")
			.password("{bcrypt}$2a$10$Z7o38o5soVFSdQGzAEDNf.xR0det9zQ0DMXAeVisZ3kLej9sRNYdG")
			.username("Player")
			.authorities("ROLE_USER")
			.build();
	private static final DevDojoUser ADMIN = DevDojoUser.builder()
			.name("Antonio ADMIN")
			.password("{bcrypt}$2a$10$Z7o38o5soVFSdQGzAEDNf.xR0det9zQ0DMXAeVisZ3kLej9sRNYdG")
			.username("Antonio")
			.authorities("ROLE_USER,ROLE_ADMIN")
			.build();
	
	@TestConfiguration
	@Lazy
	static class Config{
		
		@Bean(name="testRestTemplateRoleUser")
		public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") String port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("Player", "test");
			return new TestRestTemplate(restTemplateBuilder);
		}
		
		@Bean(name="testRestTemplateRoleAdmin")
		public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") String port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+ port)
					.basicAuthentication("Antonio", "test");
			return new TestRestTemplate(restTemplateBuilder);
		}
		
	}
	
	@Test
	@DisplayName("list returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		String expectedName = animeSaved.getName();

		PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null, 
				new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

		Assertions.assertThat(animePage).isNotNull();
		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("listAll returns list of anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		String expectedName = animeSaved.getName();

		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		Assertions.assertThat(animes)
				.isNotNull()
				.isNotEmpty()
				.hasSize(1);
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		Long expectedId = animeSaved.getId();

		Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

		Assertions.assertThat(anime).isNotNull();
		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
	}

	@Test
	@DisplayName("findByName returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		String expectedName = animeSaved.getName();
		
		String url = String.format("/animes/find?name=%s", expectedName);

		List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("findByName returns an empty list of anime when anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		devDojoUserRepository.save(USER);
		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=01218sahbdas", HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		Assertions.assertThat(animes).isNotNull().isEmpty();
	}

	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		devDojoUserRepository.save(USER);
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
		
		ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
		Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
		
	}

	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		
		animeSaved.setName("new anime");
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT,
				new HttpEntity<>(animeSaved),Void.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(ADMIN);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE,
				new HttpEntity<>(animeSaved),Void.class, animeSaved.getId());
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete returns 403 when user is not admin")
	void delete_Returns403_WhenUserIsNotAdmin() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE,
				new HttpEntity<>(animeSaved),Void.class, animeSaved.getId());
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}


}
