package com.misha.sh.devicemanagementmicroservice.service.deviceService;



import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.DeviceMapper;
import com.misha.sh.devicemanagementmicroservice.mapper.ModeMapper;
import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.DeviceRepository;
import com.misha.sh.devicemanagementmicroservice.request.device.battery.DeviceBatteryResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOffRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOnRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devReq.DeviceRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.devReq.DeviceResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devTechReq.DeviceTechnicalResponse;
import com.misha.sh.devicemanagementmicroservice.service.modeService.ModeService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;



@Service
@RequiredArgsConstructor
public class DevicService {
    private static final double LOW_BATTERY_THRESHOLD = 20.0;
    private static final double CRITICAL_BATTERY_THRESHOLD = 10.0;
    private static final Logger log = LoggerFactory.getLogger(DevicService.class);
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final ModeService modeService;
    private final ModeMapper modeMapper;



    public void editDevice (DeviceRequest deviceRequest) {
        var device = deviceRepository.findById(deviceRequest.getId())
                .orElseThrow(() -> new BusinessException("Device not found"));
         mergeDevice(device, deviceRequest);
         this.deviceRepository.save(device);
    }

    public DeviceResponse getDeviceById(Integer id) {
        var device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Device not found"));
        return deviceMapper.toDeviceResponse(device);
    }

    public PageResponse<DeviceResponse> getAllDevices(int size, int page) {
        Pageable pageable = PageRequest.of(size, page, Sort.by("createdDate").descending());
        Page<Device> devices = deviceRepository.findAll(pageable);
        List<DeviceResponse> deviceResponses = devices.map(deviceMapper::toDeviceResponse).stream().toList();
        return new PageResponse<>(
                deviceResponses,
                devices.getNumber(),
                devices.getSize(),
                devices.getTotalElements(),
                devices.getTotalPages(),
                devices.isFirst(),
                devices.isLast()
        );
    }

    public DeviceResponse findDeviceById(Integer id) {
        var device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Device not found"));
        return deviceMapper.toDeviceResponse(device);
    }


    public DeviceTurnOnResponse turnOnDevice(DeviceTurnOnRequest request, Integer deviceId, String defaultMode){
        var device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device not found"));
        if(device.isActive() && request.isTurnOn()){
            throw new BusinessException("Device with id: " + deviceId + " is already turned on");
        }
        device.setActive(request.isTurnOn());
        var defaultEnergyConsumingMode = modeService.setDefaultEnergyConsumingMode(defaultMode);
        var mapped = modeMapper.toModeDefault(defaultEnergyConsumingMode);
        device.setMode(mapped);
        deviceRepository.save(device);
        return deviceMapper.toTurnedOnDevice(device);
    }


    public DeviceTurnOffResponse turnOffDevice(DeviceTurnOffRequest request, Integer deviceId){
        var device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device not found"));
        if(device.isActive()) {
            device.setTurnOff(request.isTurnOff());
        }
        device.setMode(null);
        deviceRepository.save(device);
        return deviceMapper.toTurnedOffDevice(device);
    }


    public DeviceTechnicalResponse getEnergyConsuming(Integer deviceId) {
        var foundedDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Cannot find device with provided Id"));
        log.info("Founded device with id {}", deviceId);
        return deviceMapper.toTechnicalDeviceResponse(foundedDevice);
    }



    public DeviceBatteryResponse getBatteryLevel(Integer deviceId) {
        var foundedDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Cannot find device with provided Id"));
        validateDeviceStatus(foundedDevice);
        return  deviceMapper.toDeviceBatteryResponse(foundedDevice);
    }


    // Implement save battery Mode
    @Transactional(rollbackOn = BusinessException.class)
    public DeviceBatteryResponse lowBatteryMode(Integer deviceId, String lowMode) {
        var foundedDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Cannot find device with provided Id"));
        validateDeviceStatus(foundedDevice);
        if(foundedDevice.getBatteryLevel() <= LOW_BATTERY_THRESHOLD || foundedDevice.getBatteryLevel() <= CRITICAL_BATTERY_THRESHOLD){
          var x =  modeService.setLowEnergyConsumingMode(lowMode);
          var mappedMode = modeMapper.toModeLow(x);
          foundedDevice.setMode(mappedMode);
          deviceRepository.save(foundedDevice);
          return deviceMapper.toDeviceBatteryResponse(foundedDevice);
        }
        else{
            throw new BusinessException("Low battery mode is not supported");
        }
    }

    private void validateDeviceStatus(Device foundedDevice){
        if(foundedDevice.getBatteryLevel() == null) {
            throw new BusinessException("Battery level is null");
        }
        if(!foundedDevice.isActive()){
            throw new BusinessException("Battery level is not active");
        }
        if(!foundedDevice.isConnected()){
            throw new BusinessException("Battery level is not connected");
        }
    }


    private void mergeDevice(Device device, DeviceRequest deviceRequest){
        if(StringUtils.isNotBlank(deviceRequest.getDeviceName())){
            device.setDeviceName(deviceRequest.getDeviceName());
        }
        if(StringUtils.isNotBlank(deviceRequest.getDeviceType().name())){
            device.setDeviceType(deviceRequest.getDeviceType());
        }
        if(StringUtils.isNotBlank(deviceRequest.getDeviceDescription())){
            device.setDeviceDescription(deviceRequest.getDeviceDescription());
        }
        if(StringUtils.isNotBlank(deviceRequest.getManufacturer())){
            device.setManufacturer(deviceRequest.getManufacturer());
        }
        if(StringUtils.isNotBlank(deviceRequest.getDeviceModel())){
            device.setDeviceModel(deviceRequest.getDeviceModel());
        }
        if(StringUtils.isNotBlank(deviceRequest.getSerialNumber())){
            device.setSerialNumber(deviceRequest.getSerialNumber());
        }
    }









}
