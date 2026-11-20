package alquimia.domain.models;

import alquimia.domain.exceptions.InvalidDeviceDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of Alchemy's message as heard by one device. A word lost to the Philosopher's Stone is
 * an empty string.
 */
public record MessageFragment(List<String> words) {

    public MessageFragment {
        if (words == null || words.isEmpty()) {
            throw new InvalidDeviceDataException("The message fragment is required and cannot be empty.");
        }
        List<String> clean = new ArrayList<>(words.size());
        for (String word : words) {
            clean.add(word == null ? "" : word.trim());
        }
        words = List.copyOf(clean);
    }

    public static MessageFragment of(List<String> words) {
        return new MessageFragment(words);
    }

    public int size() {
        return words.size();
    }
}
