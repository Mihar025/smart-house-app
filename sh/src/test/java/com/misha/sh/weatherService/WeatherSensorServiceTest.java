package com.misha.sh.weatherService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessCreatingFailedException;
import com.misha.sh.devicemanagementmicroservice.exception.BusinessDeleteException;
import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.WeatherMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.sensor.WeatherSensor;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.WeatherSensorRepository;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataResponse;
import com.misha.sh.devicemanagementmicroservice.service.weatherSensor.WeatherSensorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WeatherSensorServiceTest {

    @Mock
    private WeatherSensorRepository weatherSensorRepository;

    @Mock
    private WeatherMapper weatherMapper;

    @Mock
    private Authentication authentication;
    @InjectMocks
    private WeatherSensorService weatherSensorService;

    private User testUser;


    private int size;
    private int page;
    private WeatherSensorRequest testWeatherSensorRequest;

    private WeatherSensor testSensor1, testSensor2;
    private WeatherSensorResponse testSensorResponse1, testSensorResponse2;


     @BeforeEach
    void setUp(){
         testUser = new User();
         testUser.setId(1);

         testWeatherSensorRequest = new WeatherSensorRequest();


         testSensor1 = new WeatherSensor();
         testSensor1.setId(1);
         testSensor1.setUser(testUser);

         testSensor2 = new WeatherSensor();
         testSensor2.setId(2);
         testSensor2.setUser(testUser);

         testSensorResponse1 = new WeatherSensorResponse();
         testSensorResponse1.setId(1);

         testSensorResponse2 = new WeatherSensorResponse();
         testSensorResponse2.setId(2);
     }

    @Test
    void findAllSensors_WhenAllSensorsExist() {
        // Arrange
        int page = 0;
        int size = 10;
        User testUser = new User();
        testUser.setId(1);

        WeatherSensor testSensor1 = new WeatherSensor();
        testSensor1.setId(1);
        testSensor1.setUser(testUser);

        WeatherSensor testSensor2 = new WeatherSensor();
        testSensor2.setId(2);
        testSensor2.setUser(testUser);

        List<WeatherSensor> sensorList = Arrays.asList(testSensor1, testSensor2);
        Page<WeatherSensor> sensorPage = new PageImpl<>(sensorList, PageRequest.of(page, size), sensorList.size());

        WeatherSensorResponse testSensorResponse1 = new WeatherSensorResponse();
        WeatherSensorResponse testSensorResponse2 = new WeatherSensorResponse();

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findAll(any(Pageable.class))).thenReturn(sensorPage);
        when(weatherMapper.toWeatherSensorResponse(testSensor1)).thenReturn(testSensorResponse1);
        when(weatherMapper.toWeatherSensorResponse(testSensor2)).thenReturn(testSensorResponse2);

        // Act
        PageResponse<WeatherSensorResponse> result = weatherSensorService.findAllAvailableSensors(page, size, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElement());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        // Verify
        verify(weatherSensorRepository).findAll(any(Pageable.class));
        verify(weatherMapper, times(2)).toWeatherSensorResponse(any(WeatherSensor.class));
    }

    @Test
    void findAllSensors_WhenUserDoesntHaveAccess_ShouldThrowBusinessException() {
        // Arrange
        int page = 0;
        int size = 10;
        User testUser = new User();
        testUser.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        WeatherSensor sensorWithDifferentUser = new WeatherSensor();
        sensorWithDifferentUser.setId(3);
        sensorWithDifferentUser.setUser(differentUser);

        List<WeatherSensor> sensorList = List.of(sensorWithDifferentUser);
        Page<WeatherSensor> sensorPage = new PageImpl<>(sensorList, PageRequest.of(page, size), sensorList.size());

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findAll(any(Pageable.class))).thenReturn(sensorPage);

        // Act & Assert
        assertThrows(BusinessException.class, () -> weatherSensorService.findAllAvailableSensors(page, size, authentication));

        // Verify
        verify(weatherSensorRepository).findAll(any(Pageable.class));
        verify(authentication).getPrincipal();
    }
    @Test
    void findAllAvailableSensors_WhenUserDoesntHaveAccess_ShouldThrowBusinessException() {
        // Arrange
        int size = 10;
        int page = 0;
        User testUser = new User();
        testUser.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        WeatherSensor sensorWithDifferentUser = new WeatherSensor();
        sensorWithDifferentUser.setId(3);
        sensorWithDifferentUser.setUser(differentUser);

        List<WeatherSensor> sensorList = List.of(sensorWithDifferentUser);
        Page<WeatherSensor> sensorPage = new PageImpl<>(sensorList, PageRequest.of(page, size), sensorList.size());

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findAll(any(Pageable.class))).thenReturn(sensorPage);

        // Act & Assert
        assertThrows(BusinessException.class, () -> weatherSensorService.findAllAvailableSensors(page, size, authentication));

        // Verify
        verify(weatherSensorRepository).findAll(any(Pageable.class));
        verify(authentication).getPrincipal();
        verifyNoInteractions(weatherMapper);
    }

     @Test
    void findSensorById_WhenSensorExistAndUserMatches_ShouldReturnSensorResponse() {
         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(1)).thenReturn(Optional.of(testSensor1));
         when(weatherMapper.toWeatherSensorResponse(testSensor1)).thenReturn(testSensorResponse1);

         WeatherSensorResponse result = weatherSensorService.findSensorById(1, authentication);

         // Assert
         assertNotNull(result);
         assertEquals(1, result.getId());
         verify(weatherSensorRepository).findById(1);
         verify(weatherMapper).toWeatherSensorResponse(testSensor1);
     }

     @Test
    void findSensorById_WhenSensorDoesntExist_ShouldThrowBusinessException() {
         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(1)).thenReturn(Optional.empty());

         assertThrows(BusinessException.class,
                 () -> weatherSensorService.findSensorById(1, authentication));
         verify(weatherSensorRepository).findById(1);
     }

     @Test
     void findSensorById_WhenUserNotMatch_ShouldThrow(){
         User differentUser = new User();
         differentUser.setId(2);
         when(authentication.getPrincipal()).thenReturn(differentUser);
         when(weatherSensorRepository.findById(1)).thenReturn(Optional.of(testSensor1));

         assertThrows(EntityNotFoundException.class, () ->
                 weatherSensorService.findSensorById(1, authentication));
         verify(weatherSensorRepository).findById(1);
     }

    @Test
    void addSensor_WhenValidRequest_ShouldSaveAndReturnSensor() {

        WeatherSensorRequest sensorRequest = new WeatherSensorRequest();
        WeatherSensor mappedSensor = new WeatherSensor();
        WeatherSensor savedSensor = new WeatherSensor();
        savedSensor.setId(1);
        savedSensor.setActive(true);
        savedSensor.setConnected(true);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherMapper.toWeatherSensor(sensorRequest)).thenReturn(mappedSensor);
        when(weatherSensorRepository.save(any(WeatherSensor.class))).thenReturn(savedSensor);
        when(weatherMapper.toWeatherSensorResponse(savedSensor)).thenReturn(testSensorResponse1);


        WeatherSensorResponse result = weatherSensorService.addSensor(sensorRequest, authentication);


        assertNotNull(result);
        assertEquals(testSensorResponse1.getId(), result.getId());

        verify(weatherMapper).toWeatherSensor(sensorRequest);
        verify(weatherSensorRepository).save(argThat(sensor ->
                sensor.getUser().equals(testUser) &&
                        sensor.isActive() &&
                        sensor.isConnected()
        ));
        verify(weatherMapper).toWeatherSensorResponse(savedSensor);
    }

    @Test
    void addSensor_WhenRepositoryThrowsException_ShouldThrowException() {
         WeatherSensorRequest sensorRequest = new WeatherSensorRequest();
         WeatherSensor mappedSensor = new WeatherSensor();

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherMapper.toWeatherSensor(sensorRequest)).thenReturn(mappedSensor);
         when(weatherSensorRepository.save(any(WeatherSensor.class))).thenThrow(RuntimeException.class);

         assertThrows(RuntimeException.class, () -> weatherSensorService.addSensor(sensorRequest, authentication));

         verify(weatherMapper).toWeatherSensor(sensorRequest);
         verify(weatherSensorRepository).save(any(WeatherSensor.class));
         verify(weatherMapper, never()).toWeatherSensorResponse(any(WeatherSensor.class));
    }
    @Test
    void addSensor_WhenMapperThrowsException_ShouldThrowException() {
         WeatherSensorRequest sensorRequest = new WeatherSensorRequest();

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherMapper.toWeatherSensor(sensorRequest)).thenThrow(RuntimeException.class);

         assertThrows(RuntimeException.class, () -> weatherSensorService.addSensor(sensorRequest, authentication));

         verify(weatherMapper).toWeatherSensor(sensorRequest);
         verify(weatherSensorRepository, never()).save(any(WeatherSensor.class));

    }


    @Test
    void removeSensor_WhenValidRequest_ShouldRemoveSensor() {
         Integer sensorId =1;
         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));

        assertDoesNotThrow(() -> weatherSensorService.removeSensor(sensorId, authentication));

         verify(weatherSensorRepository).findById(1);
         verify(weatherSensorRepository).delete(testSensor1);
    }

    @Test
    void removeSensor_WhenUserDoesNotOwnSensor_ShouldThrowBusinessDeleteException() {
        Integer sensorId = 1;
        User differentUser = new User();
        differentUser.setId(2);
        testSensor1.setUser(differentUser);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));

        assertThrows(BusinessDeleteException.class, () -> weatherSensorService.removeSensor(sensorId, authentication));

        verify(weatherSensorRepository).findById(sensorId);
        verify(weatherSensorRepository, never()).delete(any(WeatherSensor.class));
    }

    @Test
    void removeSensor_WhenSensorNotFound_ShouldThrowBusinessException() {
    Integer sensorId = 1;

    when(authentication.getPrincipal()).thenReturn(testUser);
    when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> weatherSensorService.removeSensor(sensorId, authentication));

    verify(weatherSensorRepository).findById(sensorId);
    verify(weatherSensorRepository, never()).delete(any(WeatherSensor.class));
    }

   @Test
    void getWeatherSensorEnergyConsuming_ShouldHaveDataAndReturnIt(){
         Integer sensorId = 1;
         testSensor1.setUser(testUser);

         WeatherSensorEnergyResponse expectedResponse = new WeatherSensorEnergyResponse();
         expectedResponse.setSensorId(sensorId);
         expectedResponse.setOwnerId(testUser.getId());
         expectedResponse.setAmps(5);
         expectedResponse.setVoltage(20.0);
         expectedResponse.setEnergyConsumingPerHours("0.5KW");

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));
         when(weatherMapper.toWeatherSensorEnergyResponse(testSensor1)).thenReturn(expectedResponse);

       WeatherSensorEnergyResponse response = weatherSensorService.getWeatherSensorEnergyConsuming(sensorId, authentication);

       assertNotNull(response);
        assertEquals(expectedResponse, response);
       verify(weatherSensorRepository).findById(sensorId);
       verify(weatherMapper).toWeatherSensorEnergyResponse(testSensor1);
   }
    @Test
    void getWeatherSensorEnergyConsuming_WhenSensorNotFound_ShouldThrowBusinessException() {
         Integer sensorId = 1;
         testSensor1.setUser(testUser);

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.empty());
         assertThrows(BusinessException.class, () -> weatherSensorService.getWeatherSensorEnergyConsuming(sensorId, authentication));

         verify(weatherSensorRepository).findById(sensorId);

    }
    @Test
    void getWeatherSensorEnergyConsuming_WhenUserDoesNotOwnSensor_ShouldThrowBusinessException() {
         Integer sensorId = 1;
         User differentUser = new User();
         differentUser.setId(2);
         testSensor1.setUser(differentUser);

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));

         assertThrows(RuntimeException.class, () -> weatherSensorService.getWeatherSensorEnergyConsuming(sensorId, authentication));

         verify(weatherSensorRepository).findById(sensorId);
    }

    @Test
    void getWeatherSensorEnergyConsuming_WhenSensorDontHaveAnyDataAndReturnEmpty_ShouldThrowBusinessException() {
         Integer sensorId = 1;
         testSensor1.setUser(testUser);

         testSensor1.setVoltage(null);
         testSensor1.setAmps(null);
         testSensor1.setEnergyConsumingPerHours(null);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));
        when(weatherMapper.toWeatherSensorEnergyResponse(testSensor1)).thenReturn(new WeatherSensorEnergyResponse());

        assertThrows(BusinessException.class, () -> weatherSensorService.getWeatherSensorEnergyConsuming(sensorId, authentication));

        verify(authentication).getPrincipal();
        verify(weatherSensorRepository).findById(sensorId);
        verify(weatherMapper).toWeatherSensorEnergyResponse(testSensor1);
    }


    @Test
    void changeOnCustomEnergyConsumingMode_ShouldHaveDataAndReturnIt() {
        Integer sensorId = 1;
        testSensor1.setUser(testUser);
        testSensor1.setActive(true);
        testSensor1.setConnected(true);

        WeatherSensorEnergyRequest request = new WeatherSensorEnergyRequest();
        request.setAmps(5);
        request.setVoltage(20.0);
        request.setEnergyConsumingPerHours("0.5KW");

        WeatherSensorEnergyResponse expectedResponse = new WeatherSensorEnergyResponse();
        expectedResponse.setAmps(5);
        expectedResponse.setVoltage(20.0);
        expectedResponse.setEnergyConsumingPerHours("0.5KW");

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));
        when(weatherSensorRepository.save(any(WeatherSensor.class))).thenReturn(testSensor1);
        when(weatherMapper.toWeatherSensorEnergyResponse(any(WeatherSensor.class))).thenReturn(expectedResponse);

        WeatherSensorEnergyResponse response = weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication);

        assertNotNull(response);
        assertEquals(expectedResponse.getAmps(), response.getAmps());
        assertEquals(expectedResponse.getVoltage(), response.getVoltage());
        assertEquals(expectedResponse.getEnergyConsumingPerHours(), response.getEnergyConsumingPerHours());

        verify(weatherSensorRepository).findById(sensorId);
        verify(authentication).getPrincipal();
        verify(weatherSensorRepository).save(any(WeatherSensor.class));
        verify(weatherMapper).toWeatherSensorEnergyResponse(any(WeatherSensor.class));
    }

    @Test
    void changeOnCustomEnergyConsumingMode_WhenUserDoesNotOwnSensor_ShouldThrowBusinessException() {

        Integer sensorId = 1;
        User differentUser = new User();
        differentUser.setId(2);
        testSensor1.setUser(testUser);

        WeatherSensorEnergyRequest request = new WeatherSensorEnergyRequest();
        request.setAmps(5);
        request.setVoltage(20.0);
        request.setEnergyConsumingPerHours("0.5KW");

        when(authentication.getPrincipal()).thenReturn(differentUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));


        assertThrows(BusinessException.class, () ->
                weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication));

        verify(authentication).getPrincipal();
        verify(weatherSensorRepository).findById(sensorId);
        verify(weatherSensorRepository, never()).save(any(WeatherSensor.class));
        verifyNoInteractions(weatherMapper);
    }

    @Test
    void changeOnCustomEnergyConsumingMode_WhenSensorNotFound_ShouldThrowBusinessException() {
         Integer sensorId = 1;
         WeatherSensorEnergyRequest request = new WeatherSensorEnergyRequest();
         request.setAmps(5);
         request.setVoltage(20.0);
         request.setEnergyConsumingPerHours("0.5KW");

         when(authentication.getPrincipal()).thenReturn(testUser);
         when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.empty());

         assertThrows(BusinessException.class, () -> weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication));

         verify(authentication).getPrincipal();
         verify(weatherSensorRepository).findById(sensorId);
         verifyNoMoreInteractions(weatherSensorRepository);
         verifyNoInteractions(weatherMapper);
    }
    @Test
    void changeOnCustomEnergyConsumingMode_WhenSensorDontHaveAnyData_ShouldThrowBusinessException() {
         Integer sensorId = 1;

        WeatherSensorEnergyRequest request = new WeatherSensorEnergyRequest();
        request.setAmps(null);
        request.setVoltage(null);
        request.setEnergyConsumingPerHours(null);

        testSensor1.setUser(testUser);
        testSensor1.setActive(true);
        testSensor1.setConnected(true);


        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));

        assertThrows(BusinessException.class, () -> weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication));

        verify(authentication).getPrincipal();
        verify(weatherSensorRepository).findById(sensorId);
        verifyNoMoreInteractions(weatherSensorRepository);
        verifyNoInteractions(weatherMapper);
    }

    @Test
    void changeOnCustomEnergyConsumingMode_WhenSensorNotActiveAndNotConnected_ShouldThrowBusinessException() {

        Integer sensorId = 1;

        WeatherSensorEnergyRequest request = new WeatherSensorEnergyRequest();
        request.setAmps(5);
        request.setVoltage(20.0);
        request.setEnergyConsumingPerHours("0.5KW");

        testSensor1.setUser(testUser);
        testSensor1.setActive(false);
        testSensor1.setConnected(false);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(testSensor1));


        assertFalse(testSensor1.isActive(), "Sensor should be inactive");
        assertFalse(testSensor1.isConnected(), "Sensor should be disconnected");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication));


        assertEquals("Sensor is not active or not connected!", exception.getMessage());


        verify(authentication).getPrincipal();
        verify(weatherSensorRepository).findById(sensorId);
        verifyNoMoreInteractions(weatherSensorRepository);
        verifyNoInteractions(weatherMapper);


        assertFalse(testSensor1.isActive(), "Sensor should still be inactive");
        assertFalse(testSensor1.isConnected(), "Sensor should still be disconnected");
    }

    @Test
    void sendAndUpdateWeatherData_Success() {
        // Arrange
        Integer sensorId = 1;
        WeatherDataRequest request = new WeatherDataRequest();
        request.setLatitude(48.9226);
        request.setLongitude(24.7111);
        request.setTemperature(22.5);
        request.setHumidity(60.0);
        request.setPressure(1012.3);
        request.setWindSpeed(5.5);
        request.setWindDirection("NW");
        request.setPrecipitation(0.2);
        request.setLastUpdateTime(LocalDateTime.now());

        WeatherSensor sensor = new WeatherSensor(); // заполнить данными
        sensor.setUser(testUser);

        WeatherDataResponse expectedResponse = new WeatherDataResponse();
        expectedResponse.setSensorId(123);
        expectedResponse.setLatitude(48.9226);
        expectedResponse.setLongitude(24.7111);
        expectedResponse.setTemperature(22.5);
        expectedResponse.setHumidity(60.0);
        expectedResponse.setPressure(1012.3);
        expectedResponse.setWindSpeed(5.5);
        expectedResponse.setWindDirection("NW");
        expectedResponse.setPrecipitation(0.2);
        expectedResponse.setLastUpdateTime(LocalDateTime.now());
        expectedResponse.setOwnerId(456);


        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(weatherSensorRepository.save(any(WeatherSensor.class))).thenReturn(sensor);
        when(weatherMapper.toWeatherDataResponse(sensor)).thenReturn(expectedResponse);

        WeatherDataResponse result = weatherSensorService.sendAndUpdateWeatherData(request, sensorId, authentication);


        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(weatherSensorRepository).findById(sensorId);
        verify(weatherSensorRepository).save(sensor);
        verify(weatherMapper).toWeatherDataResponse(sensor);
    }

    @Test
    void sendAndUpdateWeatherData_SensorNotFound() {
        // Arrange
        Integer sensorId = 1;
        WeatherDataRequest request = new WeatherDataRequest();
        request.setLatitude(48.9226);
        request.setLongitude(24.7111);
        request.setTemperature(22.5);
        request.setHumidity(60.0);
        request.setPressure(1012.3);
        request.setWindSpeed(5.5);
        request.setWindDirection("NW");
        request.setPrecipitation(0.2);
        request.setLastUpdateTime(LocalDateTime.now());

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                weatherSensorService.sendAndUpdateWeatherData(request, sensorId, authentication));

        verify(weatherSensorRepository).findById(sensorId);
        verifyNoMoreInteractions(weatherSensorRepository);
        verifyNoInteractions(weatherMapper);
    }

    @Test
    void sendAndUpdateWeatherData_UserNotAuthorized() {
        // Arrange
        Integer sensorId = 1;
        WeatherDataRequest request = new WeatherDataRequest();
        request.setLatitude(48.9226);
        request.setLongitude(24.7111);
        request.setTemperature(22.5);
        request.setHumidity(60.0);
        request.setPressure(1012.3);
        request.setWindSpeed(5.5);
        request.setWindDirection("NW");
        request.setPrecipitation(0.2);
        request.setLastUpdateTime(LocalDateTime.now());

        WeatherSensor sensor = new WeatherSensor();
        sensor.setLatitude(48.9226);
        sensor.setLongitude(24.7111);
        sensor.setTemperature(22.5);
        sensor.setHumidity(60.0);
        sensor.setPressure(1012.3);
        sensor.setWindSpeed(5.5);
        sensor.setWindDirection("NW");
        sensor.setPrecipitation(0.2);



        User differentUser = new User();
        differentUser.setId(2);
        sensor.setUser(differentUser);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));


        assertThrows(BusinessException.class, () ->
                weatherSensorService.sendAndUpdateWeatherData(request, sensorId, authentication));

        verify(weatherSensorRepository).findById(sensorId);
        verifyNoMoreInteractions(weatherSensorRepository);
        verifyNoInteractions(weatherMapper);
    }

    @Test
    void findWeatherData_Success() {
        // Arrange
        Integer sensorId = 1;
        WeatherSensor sensor = new WeatherSensor();
        sensor.setId(sensorId);
        sensor.setUser(testUser);
        WeatherDataResponse expectedResponse = new WeatherDataResponse();
        expectedResponse.setSensorId(123);
        expectedResponse.setLatitude(48.9226);
        expectedResponse.setLongitude(24.7111);
        expectedResponse.setTemperature(22.5);
        expectedResponse.setHumidity(60.0);
        expectedResponse.setPressure(1012.3);
        expectedResponse.setWindSpeed(5.5);
        expectedResponse.setWindDirection("NW");
        expectedResponse.setPrecipitation(0.2);
        expectedResponse.setLastUpdateTime(LocalDateTime.now());
        expectedResponse.setOwnerId(456);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(weatherMapper.toWeatherDataResponse(sensor)).thenReturn(expectedResponse);

        // Act
        WeatherDataResponse result = weatherSensorService.findWeatherData(sensorId, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(weatherSensorRepository).findById(sensorId);
        verify(weatherMapper).toWeatherDataResponse(sensor);
    }



    @Test
    void findWeatherData_SensorNotFound() {
        // Arrange
        Integer sensorId = 1;

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> weatherSensorService.findWeatherData(sensorId, authentication));

        assertEquals("Sensor not found", exception.getMessage());
        verify(weatherSensorRepository).findById(sensorId);
        verifyNoInteractions(weatherMapper);
    }


    @Test
    void findWeatherData_UserNotAuthorized() {
        // Arrange
        Integer sensorId = 1;
        WeatherSensor sensor = new WeatherSensor();
        sensor.setId(sensorId);
        User differentUser = new User();
        differentUser.setId(2);
        sensor.setUser(differentUser);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(weatherSensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        // Act & Assert
        BusinessDeleteException exception = assertThrows(BusinessDeleteException.class,
                () -> weatherSensorService.findWeatherData(sensorId, authentication));

        assertEquals("You dont have permission to access this sensor", exception.getMessage());
        verify(weatherSensorRepository).findById(sensorId);
        verifyNoInteractions(weatherMapper);
    }


}
