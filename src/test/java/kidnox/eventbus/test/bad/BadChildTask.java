package kidnox.eventbus.test.bad;

import kidnox.eventbus.Execute;
import kidnox.eventbus.test.SimpleTask;

import static org.junit.Assert.fail;

public class BadChildTask extends SimpleTask {

    @Execute public void execute() {
        fail("class not annotated with @EventTask!");
    }

}
