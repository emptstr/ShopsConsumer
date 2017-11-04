package shpe.util.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigurationRepositoryFactory {

    private static final String PRODUCTION_PROPERTIES_PATH = "src/main/resources/config.properties";
    private static final String TEST_PROPERTIES_PATH = "/home/jordan/repos/shpeOpsApiTool/src/test/resources/config.properties";
    private static final Properties productionProperties = new Properties();
    private static final Properties testProperties = new Properties();

    static {
        try {
            productionProperties.load(new FileInputStream(PRODUCTION_PROPERTIES_PATH));
            testProperties.load(new FileInputStream(TEST_PROPERTIES_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize ConfigurationRepositoryFactory", e);
        }
    }


    public static ConfigurationRepository getConfigurationRepository(RuntimeContext runtimeContext) {
        switch (runtimeContext) {
            case PROD:
                return getProdConfiguration();
            case TEST:
                return getTestConfiguration();
            default:
                throw new IllegalArgumentException(String.format("Invalid RuntimeContext: %s must be one of the followin" +
                        "g values: \n%s", runtimeContext, String.join("\n", Arrays
                        .stream(RuntimeContext.values())
                        .map(context -> context.toString())
                        .collect(Collectors.toList()))));
        }
    }

    private static ConfigurationRepository getProdConfiguration() {
        return buildConfigurationRepository(productionProperties);
    }

    private static ConfigurationRepository getTestConfiguration() {
        return buildConfigurationRepository(testProperties);
    }

    private static ConfigurationRepository buildConfigurationRepository(Properties properties) {
        Map<String, String> configurationMap = new HashMap<>();
        properties.entrySet().stream().forEach(propertyEntry -> configurationMap.put((String) propertyEntry.getKey(), (String) propertyEntry.getValue()));
        return new ConfigurationRepository(configurationMap);
    }

    public static class ConfigurationRepository {

        private final Map<String, String> configurationRepository;

        private ConfigurationRepository(Map<String, String> configurationRepository) {
            this.configurationRepository = configurationRepository;
        }

        public String get(String key) {
            String value = configurationRepository.get(key);
            if (value == null)
                throw new IllegalArgumentException("Requested configuration value does not exist: " + key);
            return value;
        }
    }

    public static enum RuntimeContext {
        PROD,
        TEST;
    }
}
