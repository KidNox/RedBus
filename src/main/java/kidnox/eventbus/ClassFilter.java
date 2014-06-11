package kidnox.eventbus;

public interface ClassFilter {
    boolean skipClass(Class clazz);


    public enum Filters implements ClassFilter {
        DEFAULT {
            @Override
            public boolean skipClass(Class clazz) {
                return clazz == Object.class;
            }
        },
        JAVA {
            @Override
            public boolean skipClass(Class clazz) {
                return clazz.getName().startsWith("java.");
            }
        },
        ANDROID {
            @Override
            public boolean skipClass(Class clazz) {
                final String name = clazz.getName();
                return name.startsWith("java.") || name.startsWith("android.");
            }
        };

        @Override
        public boolean skipClass(Class clazz) {
            throw new AbstractMethodError();
        }
    }

}
