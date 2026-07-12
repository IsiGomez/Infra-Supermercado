package cl.duoc.carrito.controller;

import cl.duoc.carrito.assemblers.CartModelAssembler;
import cl.duoc.carrito.config.SecurityUtil;
import cl.duoc.carrito.dto.request.CartItemRequestDto;
import cl.duoc.carrito.dto.request.CartRequestDto;
import cl.duoc.carrito.dto.response.CartItemResponseDto;
import cl.duoc.carrito.dto.response.CartResponseDto;
import cl.duoc.carrito.dto.response.ExceptionDto;
import cl.duoc.carrito.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Módulo de Carrito", description = "Operaciones para gestionar el carrito de compras")
public class CartController {

    private final CartService cartService;
    private final CartModelAssembler assembler;


    @Operation(summary = "Obtener total del carrito",
               description = "Devuelve el monto total actual del carrito del usuario.",
               tags = {"Módulo de Carrito → 1. Consultas de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes acceder al carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<?> getTotal(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        CartResponseDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart.getTotal() != null ? cart.getTotal().doubleValue() : 0.0);
    }


    @Operation(summary = "Obtener todo el carrito de un usuario",
               description = "Devuelve el carrito completo del usuario con todos sus items y enlaces HATEOAS.",
               tags = {"Módulo de Carrito → 1. Consultas de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes acceder al carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCart(
            @Parameter(description = "Id del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(cartService.getCart(userId)));
    }


    @Operation(summary = "Añadir producto a carrito",
               description = "Agrega un nuevo producto al carrito del usuario. " +
                             "Si el producto ya está en el carrito retorna error.",
               tags = {"Módulo de Carrito → 2. Acciones de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Producto ya en el carrito o stock insuficiente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/user/{userId}/item")
    public ResponseEntity<?> addItem(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Id del producto y cantidad a agregar", required = true)
            @Valid @RequestBody CartRequestDto request) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(cartService.addItem(userId, request)));
    }


    @Operation(summary = "Actualizar cantidad de un producto que ya existe en el carrito",
               description = "Modifica la cantidad de un producto que ya existe en el carrito.",
               tags = {"Módulo de Carrito → 2. Acciones de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/user/{userId}/item/{productId}")
    public ResponseEntity<?> updateQuantity(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId,
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
            @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nueva cantidad del producto", required = true)
            @Valid @RequestBody CartItemRequestDto request) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(cartService.updateQuantity(userId, productId, request)));
    }


    @Operation(summary = "Eliminar un producto del carrito",
               description = "Elimina un producto específico del carrito del usuario.",
               tags = {"Módulo de Carrito → 2. Acciones de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado del carrito",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/user/{userId}/item/{productId}")
    public ResponseEntity<?> removeItem(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId,
            @Parameter(description = "ID del producto a eliminar del carrito", required = true, example = "1")
            @PathVariable Long productId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(cartService.removeItem(userId, productId)));
    }


    @Operation(summary = "Dejar el carrito vacio",
               description = "Elimina todos los productos del carrito y reinicia el total a 0.",
               tags = {"Módulo de Carrito → 2. Acciones de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<?> clearCart(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Aplicar promoción al carrito",
               description = "Aplica un código de promoción vigente al total del carrito.",
               tags = {"Módulo de Carrito → 2. Acciones de Carrito"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción aplicada correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Promoción inválida, expirada o carrito vacío",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/user/{userId}/promocion")
    public ResponseEntity<?> aplicarPromocion(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId,
            @Parameter(description = "Código de la promoción", required = true, example = "PROMO10")
            @RequestParam String codigo) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(cartService.aplicarPromocion(userId, codigo)));
    }


    @Operation(summary = "Simular canje de puntos (paso 1 de 2)",
               description = "Consulta cuántos puntos tiene el usuario, cuántos puede canjear " +
                             "(solo múltiplos de 10) y cuánto ahorraría en el carrito actual. " +
                             "No modifica nada. Usar para mostrar la pantalla de confirmación al cliente.",
               tags = {"Módulo de Carrito → 3. Canje de Puntos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulación calculada correctamente"),
            @ApiResponse(responseCode = "400", description = "El carrito está vacío",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes acceder al carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/user/{userId}/canje/simular")
    public ResponseEntity<?> simularCanjePuntos(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(cartService.simularCanjePuntos(userId));
    }


    @Operation(summary = "Confirmar canje de puntos (paso 2 de 2)",
               description = "Descuenta los puntos canjeables de la cuenta del usuario y aplica " +
                             "el equivalente en pesos como descuento al total del carrito. " +
                             "Solo llamar si el cliente confirmó en el paso anterior.",
               tags = {"Módulo de Carrito → 3. Canje de Puntos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canje aplicado al carrito correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CartEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "No hay suficientes puntos o el carrito esta vacío",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes modificar el carrito de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/user/{userId}/canje/confirmar")
    public ResponseEntity<?> confirmarCanjePuntos(
            @Parameter(description = "ID del usuario dueño del carrito", required = true, example = "2")
            @PathVariable Long userId) {

        ResponseEntity<?> forbidden = verificarPropietario(userId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(cartService.confirmarCanjePuntos(userId)));
    }


    private ResponseEntity<?> verificarPropietario(Long userIdDeLaUrl) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(userIdDeLaUrl)) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre el carrito de otro usuario"
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(error);
        }

        return null;
    }


    class CartEntityModelDto {
        @Schema(
                description = "Enlaces HATEOAS del carrito",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8084/api/v1/carts/user/2\" },\n" +
                        "  \"total\": { \"href\": \"http://localhost:8084/api/v1/carts/user/2/total\" },\n" +
                        "  \"add-item\": { \"href\": \"http://localhost:8084/api/v1/carts/user/2/item\" },\n" +
                        "  \"clear\": { \"href\": \"http://localhost:8084/api/v1/carts/user/2/clear\" }\n" +
                        "}"
        )

        public Object _links;

        public Long id;
        public Long userId;
        public List<CartItemResponseDto> items;
        public Integer total;
    }
}
