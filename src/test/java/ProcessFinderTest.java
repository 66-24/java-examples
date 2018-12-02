
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Environment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProcessFinderTest {

    private ProcessFinder processFinder;

    @BeforeEach
    void setUp() {
        processFinder = new ProcessFinder();
    }

    @Test
    void givenRunningProcessesShouldReturnMatchingPids() {
        List<Integer> pids = processFinder.getPids(System.getenv().get("USERNAME"), "idea.sh");
        System.out.println(pids);
        Condition<Integer> positiveInteger = new Condition<>((integer)-> integer > 0, "Pids cannot be negative");
        assertThat(pids).isNotEmpty().are(positiveInteger);
    }

}