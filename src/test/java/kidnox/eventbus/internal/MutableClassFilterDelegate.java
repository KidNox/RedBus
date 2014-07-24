package kidnox.eventbus.internal;

import kidnox.eventbus.ClassFilter;

public class MutableClassFilterDelegate implements ClassFilter{

    ClassFilter internal;

    @Override public boolean skipClass(Class clazz) {
        return internal != null && internal.skipClass(clazz);
    }

    public void set(ClassFilter classFilter){
        internal = classFilter;
    }
}
