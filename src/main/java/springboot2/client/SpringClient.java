package springboot2.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
import springboot2.domain.Anime;

@Log4j2
public class SpringClient {
	public static void main(String[] args) {
		ResponseEntity<Anime> entity= new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class,2);
		log.info(entity);
		
		
		Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class,2);
		log.info(object);
		
		
		Anime[] animes= new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
		log.info(Arrays.toString(animes));
		
		
		ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
				HttpMethod.GET, 
				null, 
				new ParameterizedTypeReference<>() {});
		log.info(exchange.getBody());
		
		
		Anime kingdom= Anime.builder().name("kingdom").build();
		Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes/", kingdom, Anime.class);
		log.info(kingdomSaved);
		
		
		Anime samurai = Anime.builder().name("samurai").build();
		ResponseEntity<Anime> samuraiSaved = new RestTemplate().exchange("http://localhost:8080/animes",
				HttpMethod.POST, 
				new HttpEntity<>(samurai, createJsonHeader()), 
				Anime.class);
		log.info(samuraiSaved);
		
		
		Anime animeUpdated = samuraiSaved.getBody();
		animeUpdated.setName("Samurai Champloo 2");
		ResponseEntity<Void> samuraiUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
				HttpMethod.PUT, 
				new HttpEntity<>(animeUpdated, createJsonHeader()), 
				Void.class);
		log.info(samuraiUpdated);
		
		ResponseEntity<Void> samuraiDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
				HttpMethod.DELETE, 
				null, 
				Void.class,
				animeUpdated.getId());
		log.info(samuraiDeleted);
	}
	
	private static HttpHeaders createJsonHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
