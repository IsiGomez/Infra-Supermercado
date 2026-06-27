package cl.supermercado.promociones.service;

import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import cl.supermercado.promociones.mapper.PromocionMapper;
import cl.supermercado.promociones.model.Promocion;
import cl.supermercado.promociones.repository.PromocionRepository;
import cl.supermercado.promociones.service.impl.PromocionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - PromocionesImpl")
public class PromocionImplTest {

    @Mock
    private PromocionRepository repository;

    private PromocionServiceImpl promocionService;

    private PromocionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        PromocionMapper mapper = new PromocionMapper();
        promocionService = new PromocionServiceImpl(repository, mapper);

        requestDto = new PromocionRequestDto(
                "PROMO10", 10.0,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 12, 31),
                false);
    }


    @Test
    @DisplayName("crearPromocion: debería crear la promoción cuando las fechas son válidas")
    void crearPromocion_deberiaCrearPromocion_cuandoFechasSonValidas() {
        when(repository.save(any(Promocion.class))).thenAnswer(invocation -> {
            Promocion p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PromocionResponseDto result = promocionService.crearPromocion(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("PROMO10");
        assertThat(result.getDescuento()).isEqualTo(10.0);
        verify(repository, times(1)).save(any(Promocion.class));
    }


    @Test
    @DisplayName("crearPromocion: debería lanzar excepción cuando la fecha fin es anterior a la fecha inicio")
    void crearPromocion_deberiaLanzarExcepcion_cuandoFechaFinEsAnteriorAFechaInicio() {
        PromocionRequestDto requestInvalido = new PromocionRequestDto(
                "PROMO_INVALIDA", 15.0,
                LocalDate.of(2026, 12, 31), // inicio
                LocalDate.of(2026, 6, 1),   // fin ANTES del inicio
                false);

        assertThatThrownBy(() -> promocionService.crearPromocion(requestInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha fin no puede ser anterior a la fecha inicio");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorCodigo: debería retornar la promoción cuando el código existe")
    void obtenerPorCodigo_deberiaRetornarPromocion_cuandoExiste() {
        Promocion promocion = new Promocion(1L, "PROMO10", 10.0,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31), false);
        when(repository.findByCodigo("PROMO10")).thenReturn(Optional.of(promocion));

        PromocionResponseDto result = promocionService.obtenerPorCodigo("PROMO10");

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("PROMO10");
    }


    @Test
    @DisplayName("obtenerPorCodigo: debería lanzar excepción cuando el código no existe")
    void obtenerPorCodigo_deberiaLanzarExcepcion_cuandoNoExiste() {
        // Given
        when(repository.findByCodigo("NO_EXISTE")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> promocionService.obtenerPorCodigo("NO_EXISTE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Promoción no encontrada");
    }


    @Test
    @DisplayName("listarPromociones: debería retornar todas las promociones registradas")
    void listarPromociones_deberiaRetornarTodasLasPromociones() {
        Promocion promocion = new Promocion(1L, "PROMO10", 10.0,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31), false);
        when(repository.findAll()).thenReturn(List.of(promocion));

        List<PromocionResponseDto> result = promocionService.listarPromociones();

        assertThat(result).hasSize(1);
    }


    @Test
    @DisplayName("listarVigentes: debería incluir solo las promociones cuyo rango de fechas cubre hoy")
    void listarVigentes_deberiaIncluirSoloPromocionesVigentesHoy() {
        LocalDate hoy = LocalDate.now();

        Promocion vigente = new Promocion(1L, "VIGENTE", 10.0,
                hoy.minusDays(5), hoy.plusDays(5), false); // hoy está dentro del rango

        Promocion vencida = new Promocion(2L, "VENCIDA", 20.0,
                hoy.minusDays(30), hoy.minusDays(10), false); // terminó antes de hoy

        Promocion futura = new Promocion(3L, "FUTURA", 30.0,
                hoy.plusDays(10), hoy.plusDays(20), false); // empieza después de hoy

        when(repository.findAll()).thenReturn(List.of(vigente, vencida, futura));

        List<PromocionResponseDto> result = promocionService.listarVigentes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("VIGENTE");
    }


    @Test
    @DisplayName("listarVigentes: debería incluir una promoción cuyo último día es exactamente hoy")
    void listarVigentes_deberiaIncluirPromocion_cuandoHoyEsElUltimoDia() {
        LocalDate hoy = LocalDate.now();
        Promocion terminaHoy = new Promocion(1L, "TERMINA_HOY", 10.0,
                hoy.minusDays(10), hoy, false);

        when(repository.findAll()).thenReturn(List.of(terminaHoy));

        List<PromocionResponseDto> result = promocionService.listarVigentes();

        assertThat(result).hasSize(1);
    }

}
