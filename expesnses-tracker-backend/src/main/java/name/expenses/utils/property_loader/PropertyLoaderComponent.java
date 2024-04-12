package name.expenses.utils.property_loader;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Slf4j
public class PropertyLoaderComponent {

    static Map<String, ConfigFile> configFileMap = new HashMap<String, ConfigFile>();
    String userHome = "";
    String project = "";

    private final ProjectConfigLoader projectConfigLoader;


    /*
     * Loads a property from the default configuration file application.properties,
     * located in /config/services/${spring.application.name}/application.properties
     * if not found, will load application.properties from the class path
     */
    public Integer getPropertyAsInteger(String propertyName) {
        String propertyValue = load(propertyName);
        return Integer.parseInt(propertyValue);
    }

    public Integer getPropertyAsInteger(String propertyName, String folderName) {
        String propertyValue = loadFromFile(folderName, propertyName);
        return Integer.parseInt(propertyValue);
    }

    public Integer getPropertyAsInteger(String propertyName, String folderName, String fileName) {
        String propertyValue = loadFromFile(propertyName, folderName, fileName);
        return Integer.parseInt(propertyValue);
    }

    public Long getPropertyAsLong(String propertyName) {
        String propertyValue = load(propertyName);
        return Long.parseLong(propertyValue);
    }

    public Long getPropertyAsLong(String propertyName, String folderName, String fileName) {
        String propertyValue = loadFromFile(propertyName, folderName, fileName);
        return Long.parseLong(propertyValue);
    }

    public Long getPropertyAsLong(String propertyName, String folderName) {
        String propertyValue = loadFromFile(folderName, propertyName);
        return Long.parseLong(propertyValue);
    }

    public Boolean getPropertyAsBoolean(String propertyName) {
        String propertyValue = load(propertyName);
        return Boolean.parseBoolean(propertyValue);

    }

    public Boolean getPropertyAsBoolean(String propertyName, String folderName) {
        String propertyValue = loadFromFile(folderName, propertyName);
        return Boolean.parseBoolean(propertyValue);
    }

    public Boolean getPropertyAsBoolean(String propertyName, String folderName, String fileName) {
        String propertyValue = loadFromFile(propertyName, folderName, fileName);
        return Boolean.parseBoolean(propertyValue);
    }

    public Double getPropertyAsDouble(String propertyName) {
        String propertyValue = load(propertyName);
        return Double.parseDouble(propertyValue);
    }

    public Double getPropertyAsDouble(String propertyName, String folderName) {
        String propertyValue = loadFromFile(folderName, propertyName);
        return Double.parseDouble(propertyValue);
    }

    public Double getPropertyAsDouble(String propertyName, String folderName, String fileName) {
        String propertyValue = loadFromFile(propertyName, folderName, fileName);
        return Double.parseDouble(propertyValue);
    }

    public String getPropertyAsString(String propertyName) {
        String propertyValue = load(propertyName);
        return propertyValue;
    }

    public String getPropertyAsString(String propertyName, String folderName) {
        String propertyValue = loadFromFile(folderName, propertyName);
        return propertyValue;
    }

    public String getPropertyAsString(String propertyName, String folderName, String fileName) {
        String propertyValue = loadFromFile(propertyName, folderName, fileName);
        return propertyValue;
    }

    private String load(String property) {
        String applicationName = "application";
        // environment.getProperty("spring.application.name").trim();
        return loadFromFile(property, applicationName, "application");
    }

    /*
     * Loads a property from the default configuration file application.properties,
     * located in /config/services/folderName/application.properties if not found,
     * will load application.properties from the class path
     */
    private String loadFromFile(String folderName, String property) {
        return loadFromFile(property, folderName, "application");
    }

    /*
     * Loads a property from the specified file name located in
     * /config/services/folderName/fileName.properties if not found, will look for
     * fileName.properties from the class path
     */
    private boolean isProjectConfigFileExist(String folderName, String fileName) {
        userHome = projectConfigLoader.getUserHome();
        project = projectConfigLoader.getProjectName();
        StringBuilder filePath = new StringBuilder(userHome + File.separator + (project + "-config") + File.separator);
        filePath.append(folderName).append(File.separator);
        filePath.append(fileName).append(".properties");
        File currentFile = new File(filePath.toString());
        return currentFile.exists();
    }

    private String loadFromFile(String property, String folderName, String fileName) {
        File file = null;
        String filePath = "";
        try {
            String configFolder = "";
            if (isProjectConfigFileExist(folderName, fileName)) {

                configFolder = project + "-config";
            }
            StringBuilder configDir = new StringBuilder(projectConfigLoader.getUserHome())
                    .append(File.separator + configFolder);
            configDir.append(File.separator + folderName).append(File.separator);
            configDir.append(fileName).append(".properties");
            filePath = configDir.toString();
        } catch (Exception e) {
            log.info("Couldn't get the configuration from the class path");
            return "";
        }

        file = new File(filePath);
        if (file == null || !file.exists()) {
            return "";
        }
        ConfigFile configFile = configFileMap.get(folderName + "_" + fileName);
        long currentModifiedTimestamp = file.lastModified();
        if (configFile == null || currentModifiedTimestamp != configFile.getLastModifiedDate()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                configFile = new ConfigFile(currentModifiedTimestamp, fis);
                configFileMap.put(folderName + "_" + fileName, configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (configFile != null) {
            return configFile.getProperty(property);
        }
        return "";
    }

}