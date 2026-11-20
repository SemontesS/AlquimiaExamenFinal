package alquimia.domain.ports.in;

/** 2.6 Deletes the information of one device, leaving the others untouched. */
public interface DeleteDeviceReportUseCase {
    void delete(String device);
}
