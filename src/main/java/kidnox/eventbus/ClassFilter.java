package kidnox.eventbus;

public interface ClassFilter {

    boolean skipClass(Class clazz);

    ClassFilter JAVA = new ClassFilter() {
        @Override public boolean skipClass(Class clazz) {
            return clazz.getName().startsWith("java.");
        }
    };

    ClassFilter ANDROID = new ClassFilter() {
        @Override public boolean skipClass(Class clazz) {
            String name = clazz.getName();
            return name.startsWith("java.") || name.startsWith("android.");
        }
    };

}
