package springboot2.requests;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AnimePutRequestBody {
	@NotEmpty(message = "The anime id connot be empty")
	private Long id;
	@NotEmpty(message = "The anime name connot be empty")
	private String name;
}
