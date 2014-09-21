package kidnox.eventbus.test.bad;

import kidnox.eventbus.Execute;

import static org.junit.Assert.fail;

public class BadTask {

    @Execute public void execute() {
        fail("class not annotated with @EventTask!");
    }

}
