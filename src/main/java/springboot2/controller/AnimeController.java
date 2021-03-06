package springboot2.controller;

import java.util.List;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import springboot2.domain.Anime;
import springboot2.requests.AnimePostRequestBody;
import springboot2.requests.AnimePutRequestBody;
import springboot2.service.AnimeService;

@Log4j2
@RestController
@RequestMapping("animes")
@RequiredArgsConstructor
public class AnimeController {

	private final AnimeService animeService;

	@GetMapping
	@Operation(summary = "List all animes paginated", description = "The default size is 20, use the parameter size to change the default value", 
		tags = {"anime"})
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful Operation") })
	public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
		return ResponseEntity.ok(animeService.listAll(pageable));
	}

	@GetMapping(path = "/all")
	@Operation(summary = "List all animes", tags = { "anime" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful Operation")})
	public ResponseEntity<List<Anime>> listAll() {
		return ResponseEntity.ok(animeService.listAllNonPageable());
	}

	@GetMapping(path = "/{id}")
	@Operation(summary = "Find anime by id", tags = { "anime" })
	@ApiResponses(value= {
			@ApiResponse (responseCode="200", description="Successful Operation"),
			@ApiResponse (responseCode="400", description="Anime(id) does not exist in DataBase")
		})
	public ResponseEntity<Anime> findById(@PathVariable long id) {
		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}

	@GetMapping(path = "by-id/{id}")
	@Operation(summary = "Find anime by id used authentication principal", tags = { "anime" })
	@ApiResponses(value= {
			@ApiResponse (responseCode="200", description="Successful Operation"),
			@ApiResponse (responseCode="400", description="Anime(id) does not exist in DataBase")
		})
	public ResponseEntity<Anime> findByIdAuthenticationPrincipal(@PathVariable long id,
			@AuthenticationPrincipal UserDetails userDetails) {
		log.info(userDetails);
		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}

	@GetMapping(path = "/find")
	@Operation(summary = "Find anime by name", tags = { "anime" })
	@ApiResponses(value= {
			@ApiResponse (responseCode="200", description="Successful Operation"),
			@ApiResponse (responseCode="400", description="Name does not exist in request")
		})
	public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
		return ResponseEntity.ok(animeService.findByName(name));
	}

	@PostMapping
	@Operation(summary = "Save anime", tags = { "anime" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Successful Operation"),
			@ApiResponse (responseCode="400", description="Invalid Fields")})
	public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody anime) {
		return new ResponseEntity<>(animeService.save(anime), HttpStatus.CREATED);
	}

	@DeleteMapping(path = "/admin/{id}")
	@Operation(summary = "Delete anime by id", tags = { "anime" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "When Anime does not exist in DataBase") })
	public ResponseEntity<Void> delete(@PathVariable long id) {
		animeService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping
	@Operation(summary = "Replace anime", tags = { "anime" })
	@ApiResponses(value= {
			@ApiResponse (responseCode="204", description="Successful Operation"),
			@ApiResponse (responseCode="400", description="When Anime does not exist in DataBase")
		})
	public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody anime) {
		animeService.replace(anime);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
