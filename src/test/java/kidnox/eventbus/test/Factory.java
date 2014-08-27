package kidnox.eventbus.test;

public interface Factory<Instance, Parameter> {
    Instance get(Parameter parameter);



}
