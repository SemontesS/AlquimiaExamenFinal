package alquimia.adapters.rest;

import alquimia.adapters.rest.dto.CompleteInformationRequest;
import alquimia.adapters.rest.dto.DeviceReportRequest;
import alquimia.adapters.rest.dto.DeviceReportResponse;
import alquimia.adapters.rest.dto.LaboratoryResponse;
import alquimia.adapters.rest.dto.PartialDeviceReportRequest;
import alquimia.domain.ports.in.ConsultDeviceReportsUseCase;
import alquimia.domain.ports.in.DeleteDeviceReportUseCase;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.ports.in.LocateLaboratoryUseCase;
import alquimia.domain.ports.in.ProcessCompleteInformationUseCase;
import alquimia.domain.ports.in.RegisterDeviceReportUseCase;
import alquimia.domain.ports.in.ReplaceDeviceReportUseCase;
import alquimia.domain.ports.in.UpdateDeviceReportUseCase;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input adapter. {device} accepts "star-labs", "ccpd" and "star-satellite" (also "STAR Labs",
 * "S.T.A.R. Satellite", etc.).
 */
@RestController
@RequestMapping("/api/v1/alchemy")
public class AlchemyRestController {

    private final ProcessCompleteInformationUseCase processComplete;
    private final RegisterDeviceReportUseCase register;
    private final ConsultDeviceReportsUseCase consult;
    private final LocateLaboratoryUseCase locate;
    private final ReplaceDeviceReportUseCase replace;
    private final UpdateDeviceReportUseCase update;
    private final DeleteDeviceReportUseCase delete;

    public AlchemyRestController(ProcessCompleteInformationUseCase processComplete,
                                 RegisterDeviceReportUseCase register,
                                 ConsultDeviceReportsUseCase consult,
                                 LocateLaboratoryUseCase locate,
                                 ReplaceDeviceReportUseCase replace,
                                 UpdateDeviceReportUseCase update,
                                 DeleteDeviceReportUseCase delete) {
        this.processComplete = processComplete;
        this.register = register;
        this.consult = consult;
        this.locate = locate;
        this.replace = replace;
        this.update = update;
        this.delete = delete;
    }

    /** 2.1 The three devices at once. Stores nothing. */
    @PostMapping("/topsecret")
    public LaboratoryResponse processComplete(@Valid @RequestBody CompleteInformationRequest request) {
        return RestMapper.toResponse(processComplete.process(RestMapper.toCommands(request)));
    }

    /** 2.2 Register one device. */
    @PostMapping("/devices/{device}")
    public ResponseEntity<DeviceReportResponse> register(@PathVariable String device,
                                                         @Valid @RequestBody DeviceReportRequest request) {
        var report = register.register(new DeviceReportCommand(device, request.distance(), request.message()));
        return ResponseEntity.status(HttpStatus.CREATED).body(RestMapper.toResponse(report));
    }

    @GetMapping("/devices")
    public List<DeviceReportResponse> findAll() {
        return consult.findAll().stream().map(RestMapper::toResponse).toList();
    }

    @GetMapping("/devices/{device}")
    public DeviceReportResponse findOne(@PathVariable String device) {
        return RestMapper.toResponse(consult.findByDevice(device));
    }

    /** 2.3 Processes what has been accumulated. 422 if the information is not enough. */
    @GetMapping("/laboratory")
    public LaboratoryResponse locate() {
        return RestMapper.toResponse(locate.locate());
    }

    /** 2.4 Full replacement. */
    @PutMapping("/devices/{device}")
    public DeviceReportResponse replace(@PathVariable String device,
                                        @Valid @RequestBody DeviceReportRequest request) {
        return RestMapper.toResponse(
                replace.replace(new DeviceReportCommand(device, request.distance(), request.message())));
    }

    /** 2.5 Partial update. */
    @PatchMapping("/devices/{device}")
    public DeviceReportResponse update(@PathVariable String device,
                                       @RequestBody PartialDeviceReportRequest request) {
        return RestMapper.toResponse(
                update.update(new DeviceReportCommand(device, request.distance(), request.message())));
    }

    /** 2.6 Delete one device. */
    @DeleteMapping("/devices/{device}")
    public ResponseEntity<Void> delete(@PathVariable String device) {
        delete.delete(device);
        return ResponseEntity.noContent().build();
    }
}
