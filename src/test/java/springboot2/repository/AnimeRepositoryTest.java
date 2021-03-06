package springboot2.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import springboot2.domain.Anime;
import springboot2.util.AnimeCreator;

@DataJpaTest
@DisplayName("Test for Anime Repository")
class AnimeRepositoryTest {
	
	@Autowired
	private AnimeRepository animeRespository;

	@Test
	@DisplayName("Save persists anime when successful")
	void save_PersistAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRespository.save(animeToBeSaved);
		
		Assertions.assertThat(animeSaved).isNotNull();
		Assertions.assertThat(animeSaved.getId()).isNotNull();
		Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());		
	}
	
	@Test
	@DisplayName("Save updates anime when successful")
	void save_UpdatesAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRespository.save(animeToBeSaved);
		
		animeSaved.setName("Boruto");
		Anime animeUpdated = this.animeRespository.save(animeSaved);
		
		Assertions.assertThat(animeUpdated).isNotNull();
		Assertions.assertThat(animeUpdated.getId()).isNotNull();
		Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());		
	}
	
	@Test
	@DisplayName("Delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRespository.save(animeToBeSaved);
		
		this.animeRespository.delete(animeSaved);
		
		Optional<Anime> animeOptinal=this.animeRespository.findById(animeSaved.getId());
		
		Assertions.assertThat(animeOptinal).isEmpty();
	}
	
	@Test
	@DisplayName("Find By Name returns list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRespository.save(animeToBeSaved);
		
		List<Anime> animes= this.animeRespository.findByName(animeSaved.getName());
		
		Assertions.assertThat(animes)
				.isNotEmpty()
				.contains(animeSaved);
	}
	
	@Test
	@DisplayName("Find By Name returns empty list when no anime is found")
	void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
		List<Anime> animes= this.animeRespository.findByName("One Name Any");
		
		Assertions.assertThat(animes).isEmpty();
	}
	
	@Test
	@DisplayName("Save throw ConstraintViolationException when name is empty")
	void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
		Anime anime = new Anime();
		
//		Assertions.assertThatThrownBy(() -> this.animeRespository.save(anime))
//				.isInstanceOf(ConstraintViolationException.class);
		
		Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
				.isThrownBy(() -> this.animeRespository.save(anime))
				.withMessageContaining("The anime name connot be empty");
		
	}
	

}
