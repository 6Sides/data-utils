package net.dashflight.data;

public enum RuntimeEnvironment {

    DEVELOPMENT("development"),
    STAGING("staging"),
    PRODUCTION("production");


    private String name;

    RuntimeEnvironment(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RuntimeEnvironment fromString(String name) {
        for (RuntimeEnvironment val : RuntimeEnvironment.values()) {
            if (val.name.equalsIgnoreCase(name)) {
                return val;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid environment specified (%s). Must be one of [development, staging, production]", name));
    }

    public static RuntimeEnvironment getCurrentEnvironment() {
        try {
            return RuntimeEnvironment.fromString(System.getenv("environment"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("The application is not running with a valid environment specified."
                    + "Try running it again with an environment variable specifying the current environment. E.g. environment=staging");
        }
    }
}
