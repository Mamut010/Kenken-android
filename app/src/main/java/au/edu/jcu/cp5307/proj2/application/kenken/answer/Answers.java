package au.edu.jcu.cp5307.proj2.application.kenken.answer;

import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import java.util.Collection;
import java.util.Objects;

public final class Answers {
    private Answers() {}
    
    public static CageAnswer newCageAnswer(Collection<Integer> numbers) {
        CageAnswer answer = new CageAnswer();
        if (CollectionHelpers.isNullOrEmpty(numbers)) {
            return answer;
        }
        
        numbers.stream().filter(Objects::nonNull).forEach(answer::add);
        return answer;
    }
}
