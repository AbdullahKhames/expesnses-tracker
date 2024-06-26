package name.expenses.utils.property_loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFile extends Properties {

    private long lastModifiedDate;

    public ConfigFile() {
    }

    public ConfigFile(long lastModifiedDate, FileInputStream fis) {
        this.lastModifiedDate = lastModifiedDate;
        try {
            this.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}