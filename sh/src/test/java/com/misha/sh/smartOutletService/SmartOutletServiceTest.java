package com.misha.sh.smartOutletService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.SmartOutletMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.sensor.WeatherSensor;
import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.SmartOutletRepository;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.lastActivity.SmartOutletLastActivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.scheduling.SmartOutletScheduleResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.service.smartOutletService.SmartOutletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import jakarta.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SmartOutletServiceTest {

    @Mock
    private SmartOutletRepository smartOutletRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SmartOutletMapper smartOutletMapper;

    @InjectMocks
    private SmartOutletService smartOutletService;

    private User testUser;
    private SmartOutletRequest validRequest;
    private SmartOutlet smartOutlet1, smartOutlet2;
    private SmartOutletResponse smartOutletResponse1, smartOutletResponse2;
    private SmartOutlet mappedSmartOutlet;
    private SmartOutlet savedSmartOutlet;
    private SmartOutletResponse expectedResponse;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);

        validRequest = new SmartOutletRequest();
        validRequest.setDeviceName("Test Outlet");

        smartOutlet1 = new SmartOutlet();
        smartOutlet1.setId(1);
        smartOutlet1.setUser(testUser);

        smartOutlet2 = new SmartOutlet();
        smartOutlet2.setId(2);
        smartOutlet2.setUser(testUser);

        smartOutletResponse1 = new SmartOutletResponse();
        smartOutletResponse1.setDeviceId(1);

        smartOutletResponse2 = new SmartOutletResponse();
        smartOutletResponse2.setDeviceId(2);

        mappedSmartOutlet = new SmartOutlet();
        mappedSmartOutlet.setDeviceName("Test Outlet");

        savedSmartOutlet = new SmartOutlet();
        savedSmartOutlet.setId(1);
        savedSmartOutlet.setDeviceName("Test Outlet");
        savedSmartOutlet.setActive(true);
        savedSmartOutlet.setOn(true);
        savedSmartOutlet.setStatus(DeviceStatus.ACTIVE);

        expectedResponse = new SmartOutletResponse();
        expectedResponse.setDeviceId(1);
        expectedResponse.setDeviceName("Test Outlet");


    }

    @Test
    void findSmartOutletById_WhenOutletExistAndUserMatches_ShouldReturnSmartOutletResponse() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(smartOutletRepository.findById(1)).thenReturn(Optional.of(smartOutlet1));
        when(smartOutletMapper.toSmartOutletResponse(smartOutlet1)).thenReturn(smartOutletResponse1);

        SmartOutletResponse smartOutletResponse = smartOutletService.findOutletById(1, authentication);

        assertNotNull(smartOutletResponse);
        assertEquals(1, smartOutletResponse.getDeviceId());
        verify(smartOutletRepository).findById(1);
        verify(smartOutletMapper).toSmartOutletResponse(smartOutlet1);
    }

    @Test
    void findSmartOutletById_WhenOutletDoesntExist_ShouldReturnEntityNotFoundException() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> smartOutletService.findOutletById(1, authentication));
        verify(authentication).getPrincipal();
        verify(smartOutletRepository).findById(1);
    }

    @Test
    void findSmartOutletById_WhenOutletExistButUserNotAuthenticated_ShouldReturnAccessDeniedException() {
        User differentUser = new User();
        differentUser.setId(2);
        when(authentication.getPrincipal()).thenReturn(differentUser);
        when(smartOutletRepository.findById(1)).thenReturn(Optional.of(smartOutlet1));
        assertThrows(AccessDeniedException.class, () -> smartOutletService.findOutletById(1, authentication));
        verify(smartOutletRepository).findById(1);
    }

    @Nested
    class AddSmartOutletTests {

        @Test
        void whenValidRequest_thenShouldSaveAndReturnResponse() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletMapper.toSmartOutlet(any(SmartOutletRequest.class))).thenReturn(mappedSmartOutlet);
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(savedSmartOutlet);
            when(smartOutletMapper.toSmartOutletResponse(any(SmartOutlet.class))).thenReturn(expectedResponse);

            SmartOutletResponse result = smartOutletService.addSmartOutlet(validRequest, authentication);

            assertNotNull(result);
            assertEquals(expectedResponse.getDeviceId(), result.getDeviceId());
            assertEquals(expectedResponse.getDeviceName(), result.getDeviceName());

            ArgumentCaptor<SmartOutlet> smartOutletCaptor = ArgumentCaptor.forClass(SmartOutlet.class);
            verify(smartOutletRepository).save(smartOutletCaptor.capture());
            SmartOutlet capturedSmartOutlet = smartOutletCaptor.getValue();

            assertEquals(testUser, capturedSmartOutlet.getUser());
            assertTrue(capturedSmartOutlet.isActive());
            assertTrue(capturedSmartOutlet.isOn());
            assertEquals(DeviceStatus.ACTIVE, capturedSmartOutlet.getStatus());
        }



        @Test
        void whenMapperThrowsException_thenShouldPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletMapper.toSmartOutlet(any(SmartOutletRequest.class)))
                    .thenThrow(new RuntimeException("Mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.addSmartOutlet(validRequest, authentication)
            );
        }

        @Test
        void whenRepositorySaveThrowsException_thenShouldPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletMapper.toSmartOutlet(any(SmartOutletRequest.class))).thenReturn(mappedSmartOutlet);
            when(smartOutletRepository.save(any(SmartOutlet.class)))
                    .thenThrow(new RuntimeException("Save error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.addSmartOutlet(validRequest, authentication)
            );
        }

        @Test
        void whenResponseMapperThrowsException_thenShouldPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletMapper.toSmartOutlet(any(SmartOutletRequest.class))).thenReturn(mappedSmartOutlet);
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(savedSmartOutlet);
            when(smartOutletMapper.toSmartOutletResponse(any(SmartOutlet.class)))
                    .thenThrow(new RuntimeException("Response mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.addSmartOutlet(validRequest, authentication)
            );
        }
    }

    @Nested
    class FindAllOutletsTests {

        private Pageable pageable;
        private Page<SmartOutlet> outletPage;
        private List<SmartOutlet> outletList;

        @BeforeEach
        void setUp() {
            pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
            outletList = Arrays.asList(smartOutlet1, smartOutlet2);
            outletPage = new PageImpl<>(outletList, pageable, outletList.size());
        }

        @Test
        void whenUserHasOutlets_thenReturnAllOutlets() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findAllOutletsByUser(any(Pageable.class), eq(testUser.getId()))).thenReturn(outletPage);
            when(smartOutletMapper.toSmartOutletResponse(smartOutlet1)).thenReturn(smartOutletResponse1);
            when(smartOutletMapper.toSmartOutletResponse(smartOutlet2)).thenReturn(smartOutletResponse2);

            PageResponse<SmartOutletResponse> result = smartOutletService.findAllOutlets(authentication, 10, 0);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(0, result.getNumber());
            assertEquals(10, result.getSize());
            assertEquals(2, result.getTotalElement());
            assertEquals(1, result.getTotalPages());
            assertTrue(result.isFirst());
            assertTrue(result.isLast());

            verify(smartOutletRepository).findAllOutletsByUser(pageable, testUser.getId());
            verify(smartOutletMapper, times(2)).toSmartOutletResponse(any(SmartOutlet.class));
        }

        @Test
        void whenUserHasNoOutlets_thenReturnEmptyPage() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findAllOutletsByUser(any(Pageable.class), eq(testUser.getId())))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

            PageResponse<SmartOutletResponse> result = smartOutletService.findAllOutlets(authentication, 10, 0);

            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getNumber());
            assertEquals(10, result.getSize());
            assertEquals(0, result.getTotalElement());
            assertEquals(0, result.getTotalPages());
            assertTrue(result.isFirst());
            assertTrue(result.isLast());

            verify(smartOutletRepository).findAllOutletsByUser(pageable, testUser.getId());
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findAllOutletsByUser(any(Pageable.class), eq(testUser.getId())))
                    .thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.findAllOutlets(authentication, 10, 0)
            );

            verify(smartOutletRepository).findAllOutletsByUser(pageable, testUser.getId());
        }


        @Test
        void whenUserIdDoesNotMatchOutletUserId_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);
            SmartOutlet inaccessibleOutlet = new SmartOutlet();
            inaccessibleOutlet.setUser(differentUser);

            List<SmartOutlet> mixedList = Arrays.asList(smartOutlet1, inaccessibleOutlet);
            Page<SmartOutlet> mixedPage = new PageImpl<>(mixedList, pageable, mixedList.size());

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findAllOutletsByUser(any(Pageable.class), eq(testUser.getId()))).thenReturn(mixedPage);
            when(smartOutletMapper.toSmartOutletResponse(smartOutlet1)).thenReturn(smartOutletResponse1);

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.findAllOutlets(authentication, 10, 0)
            );

            verify(smartOutletRepository).findAllOutletsByUser(pageable, testUser.getId());
            verify(smartOutletMapper).toSmartOutletResponse(smartOutlet1);
        }

        @Test
        void whenRequestingSecondPage_thenReturnCorrectPage() {
            Pageable secondPageable = PageRequest.of(1, 5, Sort.by("createdDate").descending());
            Page<SmartOutlet> secondPage = new PageImpl<>(Collections.singletonList(smartOutlet2), secondPageable, 6);

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findAllOutletsByUser(eq(secondPageable), eq(testUser.getId()))).thenReturn(secondPage);
            when(smartOutletMapper.toSmartOutletResponse(smartOutlet2)).thenReturn(smartOutletResponse2);

            PageResponse<SmartOutletResponse> result = smartOutletService.findAllOutlets(authentication, 5, 1);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(1, result.getNumber());
            assertEquals(5, result.getSize());
            assertEquals(6, result.getTotalElement());
            assertEquals(2, result.getTotalPages());
            assertFalse(result.isFirst());
            assertTrue(result.isLast());

            verify(smartOutletRepository).findAllOutletsByUser(secondPageable, testUser.getId());
            verify(smartOutletMapper).toSmartOutletResponse(smartOutlet2);
        }
    }
    @Nested
    class TurnOnSmartOutletTests {

        private SmartOutlet outlet;
        private LocalDateTime turnOnTime;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            outlet.setOn(false);
            turnOnTime = LocalDateTime.now();
        }

        @Test
        void whenOutletExistsAndUserHasPermission_thenTurnOnOutlet() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenAnswer(invocation -> {
                SmartOutlet savedOutlet = invocation.getArgument(0);
                savedOutlet.setLastOnTime(turnOnTime);
                return savedOutlet;
            });

            SmartOutletTurnOnResponse response = smartOutletService.turnOnSmartOutlet(1, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertTrue(response.isOn());
            assertEquals(turnOnTime, response.getLastOnTime());

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.turnOnSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.turnOnSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenOutletIsAlreadyOn_thenUpdateLastOnTimeAndReturnResponse() {
            outlet.setOn(true);
            LocalDateTime oldOnTime = LocalDateTime.now().minusHours(1);
            outlet.setLastOnTime(oldOnTime);

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenAnswer(invocation -> {
                SmartOutlet savedOutlet = invocation.getArgument(0);
                savedOutlet.setLastOnTime(turnOnTime);
                return savedOutlet;
            });

            SmartOutletTurnOnResponse response = smartOutletService.turnOnSmartOutlet(1, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertTrue(response.isOn());
            assertNotEquals(oldOnTime, response.getLastOnTime());
            assertEquals(turnOnTime, response.getLastOnTime());

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.turnOnSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.turnOnSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }
    }

    @Nested
    class TurnOffSmartOutletTests {

        private SmartOutlet outlet;
        private LocalDateTime turnOffTime;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            outlet.setOn(true);
            turnOffTime = LocalDateTime.now();
        }

        @Test
        void whenOutletExistsAndUserHasPermission_thenTurnOffOutlet() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenAnswer(invocation -> {
                SmartOutlet savedOutlet = invocation.getArgument(0);
                savedOutlet.setLastOnTime(turnOffTime);
                return savedOutlet;
            });

            SmartOutletTurnOffResponse response = smartOutletService.turnOffSmartOutlet(1, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertFalse(response.isOn());
            assertEquals(turnOffTime, response.getLastOffTime());

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.turnOffSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.turnOffSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenOutletIsAlreadyOff_thenUpdateLastOnTimeAndReturnResponse() {
            outlet.setOn(false);
            LocalDateTime oldOffTime = LocalDateTime.now().minusHours(1);
            outlet.setLastOnTime(oldOffTime);

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenAnswer(invocation -> {
                SmartOutlet savedOutlet = invocation.getArgument(0);
                savedOutlet.setLastOnTime(turnOffTime);
                return savedOutlet;
            });

            SmartOutletTurnOffResponse response = smartOutletService.turnOffSmartOutlet(1, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertFalse(response.isOn());
            assertEquals(turnOffTime, response.getLastOffTime());

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.turnOffSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.turnOffSmartOutlet(1, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }
    }

    @Nested
    class ScheduleTurnOnTests {

        private SmartOutlet outlet;
        private LocalDateTime validFutureTime;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            validFutureTime = LocalDateTime.now().plusHours(1);
        }

        @Test
        void whenValidRequestAndUserHasPermission_thenScheduleTurnOn() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletScheduleResponse(any(SmartOutlet.class)))
                    .thenReturn(new SmartOutletScheduleResponse(1, validFutureTime, null)); // Предполагаем, что второй LocalDateTime может быть null

            SmartOutletScheduleResponse response = smartOutletService.scheduleTurnOn(1, validFutureTime, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertEquals(validFutureTime, response.getScheduledOn());
            // Убираем проверку статуса, так как его нет в ответе

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(argThat(savedOutlet ->
                    savedOutlet.getScheduledOn().equals(validFutureTime) &&
                            savedOutlet.getStatus() == DeviceStatus.SCHEDULED
            ));
            verify(smartOutletMapper).toSmartOutletScheduleResponse(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.scheduleTurnOn(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.scheduleTurnOn(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenScheduledTimeIsInPast_thenThrowAccessDeniedException() {
            LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.scheduleTurnOn(1, pastTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.scheduleTurnOn(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.scheduleTurnOn(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenMapperThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletScheduleResponse(any(SmartOutlet.class)))
                    .thenThrow(new RuntimeException("Mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.scheduleTurnOn(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
            verify(smartOutletMapper).toSmartOutletScheduleResponse(outlet);
        }
    }

    @Nested
    class ScheduleTurnOffTests {

        private SmartOutlet outlet;
        private LocalDateTime validFutureTime;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            validFutureTime = LocalDateTime.now().plusHours(1);
        }

        @Test
        void whenValidRequestAndUserHasPermission_thenScheduleTurnOff() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletScheduleResponse(any(SmartOutlet.class)))
                    .thenReturn(new SmartOutletScheduleResponse(1, null, validFutureTime));

            SmartOutletScheduleResponse response = smartOutletService.scheduleTurnOff(1, validFutureTime, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());
            assertEquals(validFutureTime, response.getScheduledOff());

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(argThat(savedOutlet ->
                    savedOutlet.getScheduledOff().equals(validFutureTime) &&
                            savedOutlet.getStatus() == DeviceStatus.SCHEDULED
            ));
            verify(smartOutletMapper).toSmartOutletScheduleResponse(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.scheduleTurnOff(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.scheduleTurnOff(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenScheduledTimeIsNull_thenThrowIllegalArgumentException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(IllegalArgumentException.class, () ->
                    smartOutletService.scheduleTurnOff(1, null, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenScheduledTimeIsInPast_thenThrowIllegalArgumentException() {
            LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(IllegalArgumentException.class, () ->
                    smartOutletService.scheduleTurnOff(1, pastTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.scheduleTurnOff(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.scheduleTurnOff(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenMapperThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletScheduleResponse(any(SmartOutlet.class)))
                    .thenThrow(new RuntimeException("Mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.scheduleTurnOff(1, validFutureTime, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
            verify(smartOutletMapper).toSmartOutletScheduleResponse(outlet);
        }
    }

    @Nested
    class CurrentPowerUsingTests {

        private SmartOutlet outlet;
        private SmartOutletEnergyConsumingResponse expectedResponse;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);

            expectedResponse = new SmartOutletEnergyConsumingResponse();
            expectedResponse.setOutletId(1);
            // Установите другие необходимые поля в expectedResponse
        }

        @Test
        void whenValidRequestAndUserHasPermission_thenReturnPowerConsumption() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet)).thenReturn(expectedResponse);

            SmartOutletEnergyConsumingResponse response = smartOutletService.currentPowerUsing(1, authentication);

            assertNotNull(response);
            assertEquals(expectedResponse.getOutletId(), response.getOutletId());
            // Добавьте другие проверки для полей response

            verify(smartOutletRepository).findById(1);
            verify(smartOutletMapper).toSmartOutletEnergyConsumingResponse(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.currentPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletMapper, never()).toSmartOutletEnergyConsumingResponse(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.currentPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletMapper, never()).toSmartOutletEnergyConsumingResponse(any(SmartOutlet.class));
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.currentPowerUsing(1, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletMapper, never()).toSmartOutletEnergyConsumingResponse(any(SmartOutlet.class));
        }

        @Test
        void whenMapperThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet))
                    .thenThrow(new RuntimeException("Mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.currentPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletMapper).toSmartOutletEnergyConsumingResponse(outlet);
        }

        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.currentPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletMapper, never()).toSmartOutletEnergyConsumingResponse(any(SmartOutlet.class));
        }
    }

    @Nested
    class SetCustomPowerUsingTests {

        private SmartOutlet outlet;
        private SmartOutletEnergyConsumingRequest request;
        private SmartOutletEnergyConsumingResponse expectedResponse;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            outlet.setOn(true);

            request = new SmartOutletEnergyConsumingRequest();
            // Установите необходимые поля в request

            expectedResponse = new SmartOutletEnergyConsumingResponse();
            expectedResponse.setOutletId(1);
            // Установите другие необходимые поля в expectedResponse
        }

        @Test
        void whenValidRequestAndOutletIsOn_thenUpdateAndReturnResponse() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet)).thenReturn(expectedResponse);

            SmartOutletEnergyConsumingResponse response = smartOutletService.setCustomPowerUsing(1, request, authentication);

            assertNotNull(response);
            assertEquals(expectedResponse.getOutletId(), response.getOutletId());
            // Добавьте другие проверки для полей response

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
            verify(smartOutletMapper).toSmartOutletEnergyConsumingResponse(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.setCustomPowerUsing(1, request, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.setCustomPowerUsing(1, request, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenOutletIsOff_thenThrowBusinessException() {
            outlet.setOn(false);
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(BusinessException.class, () ->
                    smartOutletService.setCustomPowerUsing(1, request, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.setCustomPowerUsing(1, request, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenRepositorySaveThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Save error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.setCustomPowerUsing(1, request, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(any(SmartOutlet.class));
        }
    }

    @Nested
    class SetDefaultPowerUsingTests {

        private SmartOutlet outlet;
        private SmartOutletEnergyConsumingResponse expectedResponse;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);

            expectedResponse = new SmartOutletEnergyConsumingResponse();
            expectedResponse.setOutletId(1);
            // Установите другие необходимые поля в expectedResponse, соответствующие значениям по умолчанию
        }

        @Test
        void whenValidRequest_thenResetToDefaultAndReturnResponse() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet)).thenReturn(expectedResponse);

            SmartOutletEnergyConsumingResponse response = smartOutletService.setDefaultPowerUsing(1, authentication);

            assertNotNull(response);
            assertEquals(expectedResponse.getOutletId(), response.getOutletId());
            // Добавьте другие проверки для полей response, соответствующих значениям по умолчанию

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
            verify(smartOutletMapper).toSmartOutletEnergyConsumingResponse(outlet);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.setDefaultPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.setDefaultPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.setDefaultPowerUsing(1, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
            verify(smartOutletRepository, never()).save(any(SmartOutlet.class));
        }

        @Test
        void whenRepositorySaveThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenThrow(new RuntimeException("Save error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.setDefaultPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
        }

        @Test
        void whenMapperThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));
            when(smartOutletRepository.save(any(SmartOutlet.class))).thenReturn(outlet);
            when(smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet)).thenThrow(new RuntimeException("Mapping error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.setDefaultPowerUsing(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
            verify(smartOutletRepository).save(outlet);
            verify(smartOutletMapper).toSmartOutletEnergyConsumingResponse(outlet);
        }
    }

    @Nested
    class GetLastActivityTests {

        private SmartOutlet outlet;
        private LocalDateTime scheduledOn;
        private LocalDateTime scheduledOff;

        @BeforeEach
        void setUp() {
            outlet = new SmartOutlet();
            outlet.setId(1);
            outlet.setUser(testUser);
            scheduledOn = LocalDateTime.now().plusHours(1);
            scheduledOff = LocalDateTime.now().plusHours(2);
            outlet.setScheduledOn(scheduledOn);
            outlet.setScheduledOff(scheduledOff);
            outlet.setOn(true);
        }

        @Test
        void whenValidRequest_thenReturnLastActivityResponse() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            SmartOutletLastActivityResponse response = smartOutletService.getLastActivity(1, authentication);

            assertNotNull(response);
            assertEquals(1, response.getOutletId());

            verify(smartOutletRepository).findById(1);
        }

        @Test
        void whenOutletDoesNotExist_thenThrowEntityNotFoundException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    smartOutletService.getLastActivity(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
        }

        @Test
        void whenUserDoesNotHavePermission_thenThrowAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2);

            when(authentication.getPrincipal()).thenReturn(differentUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            assertThrows(AccessDeniedException.class, () ->
                    smartOutletService.getLastActivity(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
        }

        @Test
        void whenAuthenticationPrincipalIsNotUser_thenThrowClassCastException() {
            when(authentication.getPrincipal()).thenReturn("Not a User object");

            assertThrows(ClassCastException.class, () ->
                    smartOutletService.getLastActivity(1, authentication)
            );

            verify(smartOutletRepository, never()).findById(anyInt());
        }

        @Test
        void whenOutletIsOff_thenReturnCorrectStatus() {
            outlet.setOn(false);
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenReturn(Optional.of(outlet));

            SmartOutletLastActivityResponse response = smartOutletService.getLastActivity(1, authentication);

            assertNotNull(response);
            assertFalse(response.isCurrentlyOn());

            verify(smartOutletRepository).findById(1);
        }



        @Test
        void whenRepositoryThrowsException_thenPropagateException() {
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(smartOutletRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () ->
                    smartOutletService.getLastActivity(1, authentication)
            );

            verify(smartOutletRepository).findById(1);
        }
    }
}