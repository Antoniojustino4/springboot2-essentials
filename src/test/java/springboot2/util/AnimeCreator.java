package springboot2.util;

import springboot2.domain.Anime;

public class AnimeCreator {
	
	public static Anime createAnimeToBeSaved() {
		return Anime.builder()
				.name("Naruto")
				.build();
	}
	
	public static Anime createValidAnime() {
		return Anime.builder()
				.name("Naruto")
				.id(1L)
				.build();
	}
	
	public static Anime createValidUpdateAnime() {
		return Anime.builder()
				.name("Naruto")
				.id(1L)
				.build();
	}
}
