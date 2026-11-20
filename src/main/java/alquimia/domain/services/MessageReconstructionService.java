package alquimia.domain.services;

import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.models.MessageFragment;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Rebuilds the message by merging the fragments position by position. Empty words are ignored
 * (they are words lost to interference); the others must agree.
 */
public class MessageReconstructionService {

    public String reconstruct(Collection<MessageFragment> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            throw new InsufficientDataException("There are no message fragments to rebuild the message.");
        }
        int length = fragments.iterator().next().size();
        for (MessageFragment fragment : fragments) {
            if (fragment.size() != length) {
                throw new InvalidDeviceDataException(
                        "The fragments have different lengths (" + length + " and " + fragment.size() + ").");
            }
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Set<String> words = new LinkedHashSet<>();
            for (MessageFragment fragment : fragments) {
                String word = fragment.words().get(i);
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }
            if (words.isEmpty()) {
                throw new InsufficientDataException(
                        "There is not enough information: word " + (i + 1) + " was lost in every fragment.");
            }
            if (words.size() > 1) {
                throw new InvalidDeviceDataException(
                        "The fragments contradict each other at word " + (i + 1) + ": " + List.copyOf(words) + ".");
            }
            if (message.length() > 0) {
                message.append(' ');
            }
            message.append(words.iterator().next());
        }
        return message.toString();
    }
}
