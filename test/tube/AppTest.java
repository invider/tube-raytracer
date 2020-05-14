package tube;

import org.junit.Test;
import static org.junit.Assert.*;

import org.tube.Tube;

public class AppTest {
    @Test public void testTube() {
        Tube t = new Tube();
        assertNotNull(t.getVersion());
    }
}
