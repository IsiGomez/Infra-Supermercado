package cl.supermercado.promociones.controller;

import cl.supermercado.promociones.assemblers.PromocionModelAssembler;
import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import cl.supermercado.promociones.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/promociones")
@RequiredArgsConstructor
@Tag(name = "Módulo de Promociones", description = "Consulta y administración de códigos de promoción")
public class PromocionController {

    private final PromocionService promocionService;
    private final PromocionModelAssembler assembler;


    @Operation(summary = "Listar promociones vigentes",
               description = "Retorna las promociones cuya fecha actual está dentro del rango de vigencia.",
               tags = {"Módulo de Promociones → 1. Consultas"})
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping("/vigentes")
    public ResponseEntity<CollectionModel<EntityModel<PromocionResponseDto>>> listarVigentes() {
        List<EntityModel<PromocionResponseDto>> promos = promocionService.listarVigentes()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(promos,
                linkTo(methodOn(PromocionController.class).listarVigentes()).withSelfRel()));
    }


    @Operation(summary = "Listar todas las promociones",
               description = "Retorna todas las promociones registradas, vigentes o no.",
               tags = {"Módulo de Promociones → 1. Consultas"})
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PromocionResponseDto>>> listarPromociones() {
        List<EntityModel<PromocionResponseDto>> promos = promocionService.listarPromociones()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(promos,
                linkTo(methodOn(PromocionController.class).listarPromociones()).withSelfRel()));
    }


    @Operation(summary = "Obtener promoción por código",
               description = "Retorna los datos de una promoción específica con sus enlaces HATEOAS.",
               tags = {"Módulo de Promociones → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromocionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<EntityModel<PromocionResponseDto>> obtenerPorCodigo(
            @Parameter(description = "Código de la promoción", required = true, example = "PROMO10")
            @PathVariable String codigo) {
        return ResponseEntity.ok(assembler.toModel(promocionService.obtenerPorCodigo(codigo)));
    }


    @Operation(summary = "Crear promoción (solo FUNCIONARIO)",
               description = "Registra una nueva promoción con su código, descuento y vigencia.",
               tags = {"Módulo de Promociones → 2. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas o datos incompletos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @PostMapping("/user/create")
    public ResponseEntity<EntityModel<PromocionResponseDto>> crearPromocion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promoción a crear", required = true)
            @Valid @RequestBody PromocionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(promocionService.crearPromocion(request)));
    }

}
