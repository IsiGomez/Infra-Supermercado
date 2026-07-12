package cl.supermercado.promociones.controller;

import cl.supermercado.promociones.assemblers.PromocionModelAssembler;
import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.ExceptionDto;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = PromocionCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = PromocionCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
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
                         schema = @Schema(implementation = PromocionHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
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
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = PromocionHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas o datos incompletos",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/user/create")
    public ResponseEntity<EntityModel<PromocionResponseDto>> crearPromocion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promoción a crear", required = true)
            @Valid @RequestBody PromocionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(promocionService.crearPromocion(request)));
    }



    class PromocionHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para la promoción",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8087/api/v1/promociones/PROMO10\" },\n" +
                        "  \"promociones\": { \"href\": \"http://localhost:8087/api/v1/promociones\" },\n" +
                        "  \"promociones-vigente\": { \"href\": \"http://localhost:8087/api/v1/promociones/vigentes\" },\n" +
                        "  \"crear-promocion\": { \"href\": \"http://localhost:8087/api/v1/promociones/user/create\" }\n" +
                        "}"
        )
        public Object _links;

        @Schema(example = "5", description = "Id de la promoción")
        public Long id;

        @Schema(example = "PROMO10", description = "Codigo de la promoción")
        public String codigo;

        @Schema(example = "10.0", description = "Descuento que realiza la promoción")
        public Double descuento;

        @Schema(example = "2026-07-01", description = "Fecha de Inicio de la promocion")
        public String fechaInicio;

        @Schema(example = "2026-07-31", description = "Fecha de vencimiento de la promoción")
        public String fechaFin;

        @Schema(example = "true", description = "Si es que es acumulable")
        public Boolean acumulable;
    }


    class PromocionCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de promociones",
                example = "{\"self\":{\"href\":\"http://localhost:8087/api/v1/promociones\"}}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<PromocionHateoasOpenApi> promociones;
        }
    }

}
