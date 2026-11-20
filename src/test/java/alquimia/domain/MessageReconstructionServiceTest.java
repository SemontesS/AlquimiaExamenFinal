package alquimia.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.models.MessageFragment;
import alquimia.domain.services.MessageReconstructionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class MessageReconstructionServiceTest {

    private final MessageReconstructionService service = new MessageReconstructionService();

    private static MessageFragment fragment(String... words) {
        return MessageFragment.of(Arrays.asList(words));
    }

    @Test
    void mergesFragmentsIgnoringEmptyWords() {
        String message = service.reconstruct(List.of(
                fragment("este", "", "", "mensaje", ""),
                fragment("", "es", "", "", "secreto"),
                fragment("este", "", "un", "", "")));
        assertEquals("este es un mensaje secreto", message);
    }

    @Test
    void failsWhenAWordWasLostInEveryFragment() {
        assertThrows(InsufficientDataException.class,
                () -> service.reconstruct(List.of(fragment("a", ""), fragment("a", ""))));
    }

    @Test
    void failsWhenFragmentsContradictEachOther() {
        assertThrows(InvalidDeviceDataException.class,
                () -> service.reconstruct(List.of(fragment("a"), fragment("b"))));
    }

    @Test
    void failsWhenFragmentsHaveDifferentLengths() {
        assertThrows(InvalidDeviceDataException.class,
                () -> service.reconstruct(List.of(fragment("a"), fragment("a", "b"))));
    }
}
