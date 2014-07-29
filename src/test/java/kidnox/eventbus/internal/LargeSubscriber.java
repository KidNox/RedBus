package kidnox.eventbus.internal;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

import java.util.Date;

@Subscriber
public class LargeSubscriber {

    Object object;
    String string;
    Date date;
    Event event;

    @Subscribe public void obtainEvent(Object o) {
        object = o;
    }

    @Subscribe public void obtainEvent(Event e) {
        event = e;
    }

    @Subscribe public void obtainString(String s) {
        string = s;
    }

    @Subscribe public void obtainDate(Date d) {
        date = d;
    }

    public void stubMethod(Object o) {

    }

    void stubMethod(Event o) {

    }

    public Object getObject() {
        return object;
    }

    public String getString() {
        return string;
    }

    public Date getDate() {
        return date;
    }

    public Event getEvent() {
        return event;
    }

}
