package com.misha.sh.thermostatService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.ThermostatMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import com.misha.sh.devicemanagementmicroservice.model.thermostat.Thermostat;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.ThermostatRepository;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeResponse;
import com.misha.sh.devicemanagementmicroservice.service.thermostatService.ThermostatService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ThermostatServiceTest {
    @Mock
   private ThermostatRepository thermostatRepository;

    @Mock
    private ThermostatMapper thermostatMapper;

    @Mock
    private Authentication authentication;


    @InjectMocks
    private ThermostatService thermostatService;

    private User testUser;

    private ThermostatResponse thermostatResponse1;
    private ThermostatResponse thermostatResponse2;

    private Thermostat testThermostat1, testThermostat2;
    @BeforeEach
     void setUp(){
        testUser = new User();
        testUser.setId(1);

        testThermostat1 = new Thermostat();
        testThermostat1.setId(1);
        testThermostat1.setUser(testUser);

        testThermostat2 = new Thermostat();
        testThermostat2.setId(2);
        testThermostat2.setUser(testUser);

        thermostatResponse1 = new ThermostatResponse();
        thermostatResponse1.setDeviceId(1);

        thermostatResponse2 = new ThermostatResponse();
        thermostatResponse2.setDeviceId(2);


    }

    @Test
    void findAllThermostats_WhenAllThermostatsExist() {
        int page = 0;
        int size = 10;

        List<Thermostat> thermostatList = Arrays.asList(testThermostat1, testThermostat2);
        Page<Thermostat> thermostatPage = new PageImpl<>(thermostatList, PageRequest.of(page, size), thermostatList.size());

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findAllByUserId(eq(testUser.getId()), any(Pageable.class))).thenReturn(thermostatPage);
        when(thermostatMapper.toThermostatResponse(testThermostat1)).thenReturn(thermostatResponse1);
        when(thermostatMapper.toThermostatResponse(testThermostat2)).thenReturn(thermostatResponse2);

        PageResponse<ThermostatResponse> result = thermostatService.findAllUserThermostats(page, size, authentication);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElement());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(thermostatRepository).findAllByUserId(eq(testUser.getId()), any(Pageable.class));
        verify(thermostatMapper, times(2)).toThermostatResponse(any(Thermostat.class));
    }

    @Test
    void findAllUserThermostats_WhenUserDoesNotHaveAccess_ShouldThrowBusinessException() {
        // Arrange
        int page = 0;
        int size = 10;
        User testUser = new User();
        testUser.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        Thermostat thermostatWithDifferentUser = new Thermostat();
        thermostatWithDifferentUser.setId(3);
        thermostatWithDifferentUser.setUser(differentUser);

        List<Thermostat> thermostatList = List.of(thermostatWithDifferentUser);
        Page<Thermostat> thermostatPage = new PageImpl<>(thermostatList, PageRequest.of(page, size), thermostatList.size());

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findAllByUserId(eq(testUser.getId()), any(Pageable.class))).thenReturn(thermostatPage);

        // Act & Assert
        assertThrows(BusinessException.class, () -> thermostatService.findAllUserThermostats(page, size, authentication));

        // Verify
        verify(thermostatRepository).findAllByUserId(eq(testUser.getId()), any(Pageable.class));
        verify(authentication).getPrincipal();
        verifyNoInteractions(thermostatMapper);
    }


    @Test
    void findAllThermostats_WhenNoThermostats_ShouldReturnEmptyPageResponse() {
        int size = 10;
        int page = 0;

        Page<Thermostat> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findAllByUserId(eq(testUser.getId()), any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<ThermostatResponse> result = thermostatService.findAllUserThermostats(page, size, authentication);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(0, result.getTotalElement());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(thermostatRepository).findAllByUserId(eq(testUser.getId()), any(Pageable.class));
        verify(thermostatMapper, never()).toThermostatResponse(any(Thermostat.class));
    }

    @Test
    void findThermostatById_WhenThermostatExists_AndUserMatches_ShouldReturnThermostat() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.of(testThermostat1));
        when(thermostatMapper.toThermostatResponse(testThermostat1)).thenReturn(thermostatResponse1);

        ThermostatResponse result = thermostatService.findThermostatById(1, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDeviceId());
        verify(thermostatRepository).findById(1);
        verify(thermostatMapper).toThermostatResponse(testThermostat1);
    }

    @Test
    void findThermostatById_WhenThermostatDoesntExist_ShouldReturnEntityNotFoundException(){

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> thermostatService.findThermostatById(1, authentication));
        verify(thermostatRepository).findById(1);
    }

    @Test
    void findThermostatById_WhenUserNotMatches_ShouldReturnEntityNotFoundException(){

        User differentUser = new User();

        differentUser.setId(2);

        when(authentication.getPrincipal()).thenReturn(differentUser);

        when(thermostatRepository.findById(1)).thenReturn(Optional.of(testThermostat1));

        assertThrows(EntityNotFoundException.class, () -> thermostatService.findThermostatById(1, authentication));

        verify(thermostatRepository).findById(1);
    }

    @Test
    void removeThermostatById_WhenThermostatExist(){
        Integer thermostatId = 1;

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.of(testThermostat1));

        assertDoesNotThrow(() -> thermostatService.removeThermostat(thermostatId, authentication));

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository, never()).delete(any(Thermostat.class));
    }
    @Test
    void removeThermostatById_WhenThermostatDoesntExist(){
        Integer thermostatId = 1;

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> thermostatService.removeThermostat(thermostatId, authentication));

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository, never()).delete(any(Thermostat.class));
    }
    @Test
    void removeThermostatById_WhenUserDoesntMatch_ShouldThrowEntityNotFoundException(){
        Integer thermostatId = 1;
        User differentUser = new User();
        differentUser.setId(2);
        testThermostat1.setUser(differentUser);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(testThermostat1));

        assertThrows(EntityNotFoundException.class, () -> thermostatService.removeThermostat(thermostatId, authentication));

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository, never()).delete(any(Thermostat.class));
    }

    @Test
    void addThermostat_SuccessfullyAddsThermostat() {
        // Создаем и заполняем ThermostatRequest
        ThermostatRequest testRequest = ThermostatRequest.builder()
                .deviceName("Test Thermostat")
                .deviceType(DeviceType.THERMOSTAT)
                .deviceDescription("A test thermostat")
                .manufacturer("Test Manufacturer")
                .deviceModel("TM-2000")
                .status(DeviceStatus.ACTIVE)
                .location("Living Room")
                .isConnected(true)
                .voltage(220.0)
                .amps(10)
                .energyConsumingPerHours("0.5 kWh")
                .ownerId(1)
                .build();

        // Создаем и заполняем Thermostat
        Thermostat testThermostat = new Thermostat();
        testThermostat.setId(1);
        testThermostat.setDeviceName("Test Thermostat");
        testThermostat.setDeviceType(DeviceType.THERMOSTAT);
        testThermostat.setDeviceDescription("A test thermostat");
        testThermostat.setManufacturer("Test Manufacturer");
        testThermostat.setDeviceModel("TM-2000");
        testThermostat.setStatus(DeviceStatus.ACTIVE);
        testThermostat.setLocation("Living Room");
        testThermostat.setConnected(true);
        testThermostat.setVoltage(220.0);
        testThermostat.setAmps(10);
        testThermostat.setEnergyConsumingPerHours("0.5 kWh");
        testThermostat.setUser(testUser);

        // Создаем и заполняем ThermostatResponse
        ThermostatResponse testResponse = ThermostatResponse.builder()
                .deviceId(1)
                .deviceName("Test Thermostat")
                .deviceType(DeviceType.THERMOSTAT)
                .deviceDescription("A test thermostat")
                .manufacturer("Test Manufacturer")
                .deviceModel("TM-2000")
                .status(DeviceStatus.ACTIVE)
                .location("Living Room")
                .isConnected(true)
                .voltage(220.0)
                .amps(10)
                .energyConsumingPerHours("0.5 kWh")
                .ownerId(1)
                .build();

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatMapper.toThermostat(testRequest)).thenReturn(testThermostat);
        when(thermostatRepository.save(any(Thermostat.class))).thenReturn(testThermostat);
        when(thermostatMapper.toThermostatResponse(testThermostat)).thenReturn(testResponse);

        ThermostatResponse result = thermostatService.addThermostat(testRequest, authentication);

        assertNotNull(result);
        assertEquals(testResponse, result);

        verify(thermostatMapper).toThermostat(testRequest);
        verify(thermostatRepository).save(any(Thermostat.class));
        verify(thermostatMapper).toThermostatResponse(testThermostat);

        assertEquals(testUser, testThermostat.getUser());
        assertNotNull(testThermostat.getCreatedDate());
        assertNotNull(testThermostat.getUpdatedAt());
    }

    @Test
    void addThermostat_WithNullAuthentication_ThrowsException() {
        ThermostatRequest testRequest = new ThermostatRequest();
        assertThrows(NullPointerException.class, () ->
                thermostatService.addThermostat(testRequest, null));
    }

    @Test
    void addThermostat_WithInvalidRequest_ThrowsException() {
        ThermostatRequest invalidRequest = new ThermostatRequest(); // Пустой запрос, который должен быть недопустимым

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatMapper.toThermostat(invalidRequest)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () ->
                thermostatService.addThermostat(invalidRequest, authentication));
    }

    @Test
    void addThermostat_ChecksDateTimeFields() {
        ThermostatRequest testRequest = ThermostatRequest.builder()
                .deviceName("Test Thermostat")
                .deviceType(DeviceType.THERMOSTAT)
                .status(DeviceStatus.ACTIVE)
                .manufacturer("Test Manufacturer")
                .deviceModel("TM-2000")
                .location("Living Room")
                .ownerId(1)
                .build();

        Thermostat testThermostat = new Thermostat();
        testThermostat.setId(1);
        testThermostat.setDeviceName("Test Thermostat");

        ThermostatResponse testResponse = new ThermostatResponse();
        testResponse.setDeviceId(1);
        testResponse.setDeviceName("Test Thermostat");

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatMapper.toThermostat(testRequest)).thenReturn(testThermostat);
        when(thermostatRepository.save(any(Thermostat.class))).thenReturn(testThermostat);
        when(thermostatMapper.toThermostatResponse(testThermostat)).thenReturn(testResponse);

        LocalDateTime before = LocalDateTime.now();
        thermostatService.addThermostat(testRequest, authentication);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(testThermostat.getCreatedDate());
        assertNotNull(testThermostat.getUpdatedAt());
        assertTrue(testThermostat.getCreatedDate().isAfter(before) || testThermostat.getCreatedDate().isEqual(before));
        assertTrue(testThermostat.getCreatedDate().isBefore(after) || testThermostat.getCreatedDate().isEqual(after));
        assertTrue(testThermostat.getUpdatedAt().isAfter(before) || testThermostat.getUpdatedAt().isEqual(before));
        assertTrue(testThermostat.getUpdatedAt().isBefore(after) || testThermostat.getUpdatedAt().isEqual(after));
    }

    @Test
    void turnOffThermostat_SuccessfullyTurnsOffThermostat() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.of(testThermostat1));

        thermostatService.turnOffThermostat(1, authentication);

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository).save(testThermostat1);
    }


    @Test
    void turnOffThermostat_ThrowsExceptionWhenThermostatNotFound() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.turnOffThermostat(1, authentication));

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository, never()).save(any(Thermostat.class));
    }

    @Test
    void turnOffThermostat_ThrowsExceptionWhenUserDoesNotOwnThermostat() {
        User differentUser = new User();
        differentUser.setId(2);

        Thermostat thermostatOwnedByDifferentUser = new Thermostat();
        thermostatOwnedByDifferentUser.setId(1);
        thermostatOwnedByDifferentUser.setUser(differentUser);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.of(thermostatOwnedByDifferentUser));

        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.turnOffThermostat(1, authentication));

        verify(thermostatRepository).findById(1);
        verify(thermostatRepository, never()).save(any(Thermostat.class));
    }

    @Test
    void turnOffThermostat_HandleNullAuthentication() {
        assertThrows(NullPointerException.class, () ->
                thermostatService.turnOffThermostat(1, null));

        verify(thermostatRepository, never()).findById(anyInt());
        verify(thermostatRepository, never()).save(any(Thermostat.class));
    }

    @Test
    void turnOffThermostat_EnsureTurnOffMethodCalled() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(thermostatRepository.findById(1)).thenReturn(Optional.of(testThermostat1));

        thermostatService.turnOffThermostat(1, authentication);

        verify(thermostatRepository).save(testThermostat1);
    }
    @Test
    void setThermostatCoolingMode_ValidRequest_Success() {
        // Arrange
        ThermostatCoolingModeRequest request = new ThermostatCoolingModeRequest();
        request.setCurrentTemperature(75.0);
        request.setTargetTemperature(72.0);
        request.setTemporaryMode(true);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.Cool);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));
        when(thermostatRepository.save(any(Thermostat.class))).thenReturn(thermostat);
        when(thermostatMapper.toThermostatCoolingModeResponse(any(Thermostat.class)))
                .thenReturn(new ThermostatCoolingModeResponse());

        // Act
        ThermostatCoolingModeResponse response = thermostatService.setThermostatCoolingMode(request, thermostatId, authentication);

        // Assert
        assertNotNull(response);
        verify(thermostatRepository).findById(thermostatId);
        verify(thermostatRepository).save(any(Thermostat.class));
        verify(thermostatMapper).toThermostatCoolingModeResponse(any(Thermostat.class));
    }

    @Test
    void setThermostatCoolingMode_ThermostatNotFound_ThrowsException() {
        // Arrange
        ThermostatCoolingModeRequest request = new ThermostatCoolingModeRequest();
        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.setThermostatCoolingMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatCoolingMode_UserNotOwner_ThrowsException() {
        // Arrange
        ThermostatCoolingModeRequest request = new ThermostatCoolingModeRequest();
        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        User otherUser = new User();
        otherUser.setId(2);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(otherUser);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.setThermostatCoolingMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatCoolingMode_InvalidTemperature_ThrowsException() {
        // Arrange
        ThermostatCoolingModeRequest request = new ThermostatCoolingModeRequest();
        request.setCurrentTemperature(65.0); // Below COOLING_MIN
        request.setTargetTemperature(70.0);
        request.setTemporaryMode(true);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.Cool);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                thermostatService.setThermostatCoolingMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatHeatMode_ValidRequest_Success() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        request.setCurrentTemperature(65.0);
        request.setTargetTemperature(70.0);
        request.setTemporaryMode(true);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.HEAT);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));
        when(thermostatRepository.save(any(Thermostat.class))).thenReturn(thermostat);
        when(thermostatMapper.toThermostatHeatModeResponse(any(Thermostat.class)))
                .thenReturn(new ThermostatHeatModeResponse());

        // Act
        ThermostatHeatModeResponse response = thermostatService.setThermostatHeatMode(request, thermostatId, authentication);

        // Assert
        assertNotNull(response);
        verify(thermostatRepository).findById(thermostatId);
        verify(thermostatRepository).save(any(Thermostat.class));
        verify(thermostatMapper).toThermostatHeatModeResponse(any(Thermostat.class));
    }

    @Test
    void setThermostatHeatMode_ThermostatNotFound_ThrowsException() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.setThermostatHeatMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatHeatMode_UserNotOwner_ThrowsException() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        User otherUser = new User();
        otherUser.setId(2);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(otherUser);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                thermostatService.setThermostatHeatMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatHeatMode_CurrentTemperatureTooHigh_ThrowsException() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        request.setCurrentTemperature(85.0); // Above HEATING_MAX
        request.setTargetTemperature(75.0);
        request.setTemporaryMode(true);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.HEAT);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                thermostatService.setThermostatHeatMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatHeatMode_TargetTemperatureOutOfRange_ThrowsException() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        request.setCurrentTemperature(70.0);
        request.setTargetTemperature(85.0); // Above HEATING_MAX
        request.setTemporaryMode(true);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.HEAT);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                thermostatService.setThermostatHeatMode(request, thermostatId, authentication)
        );
    }

    @Test
    void setThermostatHeatMode_InvalidModeSettings_ThrowsException() {
        // Arrange
        ThermostatHeatModeRequest request = new ThermostatHeatModeRequest();
        request.setCurrentTemperature(70.0);
        request.setTargetTemperature(75.0);
        request.setTemporaryMode(false);
        request.setAutoMode(false);
        request.setTemperatureMode(TemperatureMode.HEAT);

        Integer thermostatId = 1;
        User user = new User();
        user.setId(1);

        Thermostat thermostat = new Thermostat();
        thermostat.setId(thermostatId);
        thermostat.setUser(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(thermostatRepository.findById(thermostatId)).thenReturn(Optional.of(thermostat));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                thermostatService.setThermostatHeatMode(request, thermostatId, authentication)
        );
    }
}
