package alquimia.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import alquimia.adapters.persistence.InMemoryDeviceReportRepository;
import alquimia.domain.exceptions.DeviceReportNotFoundException;
import alquimia.domain.exceptions.DuplicateDeviceReportException;
import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.exceptions.UnknownDeviceException;
import alquimia.domain.models.Point;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.models.SurveillanceResult;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.services.ConsultDeviceReportsService;
import alquimia.domain.services.DeleteDeviceReportService;
import alquimia.domain.services.LaboratoryAnalysisService;
import alquimia.domain.services.LocateLaboratoryService;
import alquimia.domain.services.MessageReconstructionService;
import alquimia.domain.services.ProcessCompleteInformationService;
import alquimia.domain.services.RegisterDeviceReportService;
import alquimia.domain.services.ReplaceDeviceReportService;
import alquimia.domain.services.TrilaterationService;
import alquimia.domain.services.UpdateDeviceReportService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceReportUseCasesTest {

    // Laboratory at (-58.5, 112.25)
    private static final double D_LABS = Math.hypot(-500 + 58.5, -200 - 112.25);
    private static final double D_CCPD = Math.hypot(100 + 58.5, -100 - 112.25);
    private static final double D_SAT = Math.hypot(500 + 58.5, 100 - 112.25);

    private InMemoryDeviceReportRepository repository;
    private ProcessCompleteInformationService processComplete;
    private RegisterDeviceReportService register;
    private ConsultDeviceReportsService consult;
    private LocateLaboratoryService locate;
    private ReplaceDeviceReportService replace;
    private UpdateDeviceReportService update;
    private DeleteDeviceReportService delete;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceReportRepository();
        LaboratoryAnalysisService analysis =
                new LaboratoryAnalysisService(new TrilaterationService(), new MessageReconstructionService());
        processComplete = new ProcessCompleteInformationService(analysis);
        register = new RegisterDeviceReportService(repository);
        consult = new ConsultDeviceReportsService(repository);
        locate = new LocateLaboratoryService(repository, analysis);
        replace = new ReplaceDeviceReportService(repository);
        update = new UpdateDeviceReportService(repository);
        delete = new DeleteDeviceReportService(repository);
    }

    private static List<String> words(String... w) {
        return Arrays.asList(w);
    }

    private static DeviceReportCommand labs() {
        return new DeviceReportCommand("STAR Labs", D_LABS, words("este", "", "", "mensaje", ""));
    }

    private static DeviceReportCommand ccpd() {
        return new DeviceReportCommand("CCPD", D_CCPD, words("", "es", "", "", "secreto"));
    }

    private static DeviceReportCommand satellite() {
        return new DeviceReportCommand("S.T.A.R. Satellite", D_SAT, words("este", "", "un", "", ""));
    }

    private void registerAll() {
        register.register(labs());
        register.register(ccpd());
        register.register(satellite());
    }

    @Test
    void processesTheCompleteInformation() {
        SurveillanceResult result = processComplete.process(List.of(labs(), ccpd(), satellite()));
        assertEquals(new Point(-58.5, 112.25), result.position());
        assertEquals("este es un mensaje secreto", result.message());
    }

    @Test
    void completeProcessingRejectsMissingRepeatedAndUnknownDevices() {
        assertThrows(InvalidDeviceDataException.class, () -> processComplete.process(List.of(labs(), ccpd())));
        assertThrows(InvalidDeviceDataException.class, () -> processComplete.process(List.of(labs(), labs(), ccpd())));
        assertThrows(UnknownDeviceException.class, () -> processComplete.process(
                List.of(labs(), ccpd(), new DeviceReportCommand("Zoom", 1.0, words("a")))));
    }

    @Test
    void registersInAnyOrderAndProcessesWhenAllHaveReported() {
        assertThrows(InsufficientDataException.class, locate::locate);
        register.register(satellite());
        register.register(labs());
        assertThrows(InsufficientDataException.class, locate::locate);
        register.register(ccpd());
        assertEquals("este es un mensaje secreto", locate.locate().message());
        assertEquals(3, consult.findAll().size());
    }

    @Test
    void registeringTwiceIsAConflictAndInvalidDataIsRejected() {
        register.register(ccpd());
        assertThrows(DuplicateDeviceReportException.class, () -> register.register(ccpd()));
        assertThrows(InvalidDeviceDataException.class,
                () -> register.register(new DeviceReportCommand("STAR Labs", -5.0, words("a"))));
        assertThrows(InvalidDeviceDataException.class,
                () -> register.register(new DeviceReportCommand("STAR Labs", 5.0, null)));
    }

    @Test
    void replaceChangesOnlyTheSelectedDevice() {
        registerAll();
        replace.replace(new DeviceReportCommand("CCPD", 10.0, words("x", "y", "z", "w", "v")));
        assertEquals(10.0, consult.findByDevice("CCPD").distance().value());
        assertEquals(D_LABS, consult.findByDevice("STAR Labs").distance().value());
        assertThrows(InvalidDeviceDataException.class,
                () -> replace.replace(new DeviceReportCommand("CCPD", 10.0, null)));
    }

    @Test
    void replaceOnADeviceWithoutInformationIsNotFound() {
        assertThrows(DeviceReportNotFoundException.class,
                () -> replace.replace(new DeviceReportCommand("CCPD", 1.0, words("a"))));
    }

    @Test
    void partialUpdateKeepsWhatIsNotProvided() {
        registerAll();
        update.update(new DeviceReportCommand("CCPD", 200.0, null));
        assertEquals(200.0, consult.findByDevice("CCPD").distance().value());
        assertEquals(words("", "es", "", "", "secreto"), consult.findByDevice("CCPD").message().words());

        update.update(new DeviceReportCommand("CCPD", null, words("a", "b", "c", "d", "e")));
        assertEquals(200.0, consult.findByDevice("CCPD").distance().value());
        assertEquals(words("a", "b", "c", "d", "e"), consult.findByDevice("CCPD").message().words());

        assertThrows(InvalidDeviceDataException.class,
                () -> update.update(new DeviceReportCommand("CCPD", null, null)));
    }

    @Test
    void deleteRemovesOnlyOneDeviceAndLeavesTheRestConsistent() {
        registerAll();
        delete.delete("ccpd");
        assertEquals(2, consult.findAll().size());
        assertEquals(D_LABS, consult.findByDevice(SurveillanceDevice.STAR_LABS.name()).distance().value());
        assertThrows(DeviceReportNotFoundException.class, () -> consult.findByDevice("CCPD"));
        assertThrows(DeviceReportNotFoundException.class, () -> delete.delete("CCPD"));
        assertThrows(InsufficientDataException.class, locate::locate);
    }

    @Test
    void deviceNamesAreFlexible() {
        assertEquals(SurveillanceDevice.STAR_SATELLITE, SurveillanceDevice.from("S.T.A.R. Satellite"));
        assertEquals(SurveillanceDevice.STAR_SATELLITE, SurveillanceDevice.from("star-satellite"));
        assertEquals(SurveillanceDevice.STAR_LABS, SurveillanceDevice.from("STAR Labs"));
        assertThrows(UnknownDeviceException.class, () -> SurveillanceDevice.from("Arrow Cave"));
    }
}
