package springboot2.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import springboot2.domain.Anime;
import springboot2.domain.Anime.AnimeBuilder;
import springboot2.requests.AnimePostRequestBody;
import springboot2.requests.AnimePutRequestBody;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-04-01T17:32:01-0300",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class AnimeMapperImpl extends AnimeMapper {

    @Override
    public Anime toAnime(AnimePostRequestBody animePostRequestBody) {
        if ( animePostRequestBody == null ) {
            return null;
        }

        AnimeBuilder anime = Anime.builder();

        anime.name( animePostRequestBody.getName() );

        return anime.build();
    }

    @Override
    public Anime toAnime(AnimePutRequestBody animePutRequestBody) {
        if ( animePutRequestBody == null ) {
            return null;
        }

        AnimeBuilder anime = Anime.builder();

        anime.id( animePutRequestBody.getId() );
        anime.name( animePutRequestBody.getName() );

        return anime.build();
    }
}
