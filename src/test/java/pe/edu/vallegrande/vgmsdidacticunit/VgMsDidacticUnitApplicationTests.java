package pe.edu.vallegrande.vgmsdidacticunit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.vallegrande.vgmsdidacticunit.application.service.DidacticUnitService;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitCreateDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.dto.DidacticUnitIdsDto;
import pe.edu.vallegrande.vgmsdidacticunit.domain.model.DidacticUnit;
import pe.edu.vallegrande.vgmsdidacticunit.presentation.controller.DidacticUnitController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(DidacticUnitController.class)
class VgMsDidacticUnitApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private DidacticUnitService didacticUnitServiceImpl;

	@Test
	@DisplayName("Creacion de una unidad didactica")
	void createDidacticUnitTest() {
		DidacticUnitCreateDto createDto = new DidacticUnitCreateDto();
		createDto.setName("Diseño y Patronaje");
		createDto.setCredit("3");
		createDto.setHours("12");
		createDto.setCondition("Obligatoria");
		createDto.setCorrection("N/A");
		createDto.setStudyProgramId("668c7313442c3919d6648444");

		DidacticUnit savedUnit = new DidacticUnit();
		savedUnit.setDidacticId("6698827d9a2dba16af99033d");
		savedUnit.setName("Diseño y Patronaje");
		savedUnit.setCredit("3");
		savedUnit.setHours("12");
		savedUnit.setCondition("Obligatoria");
		savedUnit.setCorrection("N/A");
		savedUnit.setStudyProgramId("668c7313442c3919d6648444");
		savedUnit.setStatus("A");

		when(didacticUnitServiceImpl.create(any(DidacticUnitCreateDto.class))).thenReturn(Mono.just(savedUnit));

		webTestClient.post()
				.uri("/didactic-unit/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createDto)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(DidacticUnit.class)
				.value(response -> {
					assert response.getDidacticId().equals("6698827d9a2dba16af99033d");
					assert response.getStatus().equals("A");
				});
	}

	@Test
	@DisplayName("Creacion de una unidad didactica sin nombre")
	void createDidacticUnitMissingNameTest() {
		DidacticUnitCreateDto createDto = new DidacticUnitCreateDto();
		createDto.setCredit("1");
		createDto.setHours("12");
		createDto.setCondition("Obligatoria");
		createDto.setCorrection("N/A");
		createDto.setStudyProgramId("668c7313442c3919d6648444");

		when(didacticUnitServiceImpl.create(any(DidacticUnitCreateDto.class))).thenReturn(Mono.error(new IllegalArgumentException("Name is required")));

		webTestClient.post()
				.uri("/didactic-unit/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createDto)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.message").isEqualTo("Name is required");
	}

	@Test
	@DisplayName("Creacion de una unidad didactica con programa de estudios invalido")
	void createDidacticUnitInvalidStudyProgramIdTest() {
		DidacticUnitCreateDto createDto = new DidacticUnitCreateDto();
		createDto.setName("Diseño y Patronaje");
		createDto.setCredit("3");
		createDto.setHours("12");
		createDto.setCondition("Obligatoria");
		createDto.setCorrection("N/A");
		createDto.setStudyProgramId("invalid-program-id");

		when(didacticUnitServiceImpl.create(any(DidacticUnitCreateDto.class))).thenReturn(Mono.error(new IllegalArgumentException("StudyProgramId does not exist")));

		webTestClient.post()
				.uri("/didactic-unit/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createDto)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.message").isEqualTo("StudyProgramId does not exist");
	}

	@Test
	@DisplayName("Asignacion de unidades didacticas a un programa de estudios")
	void assignDidacticUnitsToStudyProgramTest() {
		DidacticUnitIdsDto didacticUnitIdsDto = new DidacticUnitIdsDto();
		didacticUnitIdsDto.setDidacticUnitIds(List.of("66991d21c11086487c424583", "6698827d9a2dba16af99033d"));

		DidacticUnit unit1 = new DidacticUnit();
		unit1.setDidacticId("66991d21c11086487c424583");
		unit1.setName("Unidad 1");
		unit1.setStatus("A");

		DidacticUnit unit2 = new DidacticUnit();
		unit2.setDidacticId("6698827d9a2dba16af99033d");
		unit2.setName("Unidad 2");
		unit2.setStatus("A");

		when(didacticUnitServiceImpl.assignDidacticUnitsToStudyProgram("668c7313442c3919d6648444", didacticUnitIdsDto))
				.thenReturn(Flux.just(unit1, unit2));

		webTestClient.post()
				.uri("/didactic-unit/668c7313442c3919d6648444/units")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(didacticUnitIdsDto)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(DidacticUnit.class)
				.value(response -> {
					assert response.get(0).getDidacticId().equals("66991d21c11086487c424583");
					assert response.get(1).getDidacticId().equals("6698827d9a2dba16af99033d");
					assert response.get(0).getStatus().equals("A");
					assert response.get(1).getStatus().equals("A");
				});
	}

}
