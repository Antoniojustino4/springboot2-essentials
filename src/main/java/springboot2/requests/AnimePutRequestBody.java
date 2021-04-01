package springboot2.requests;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimePutRequestBody {
	@NotEmpty(message = "The anime id connot be empty")
	private Long id;
	@NotEmpty(message = "The anime name connot be empty")
	@Schema(description = "This is the Anime's name", example = "Naruto", required = true)
	private String name;
}
