package net.dashflight.data.config;

import java.util.Properties;

public class PropertiesData implements ConfigurationData<Properties> {

    private Properties props;

    public PropertiesData(Properties props) {
        this.props = props;
    }


    @Override
    public Properties getData() {
        return this.props;
    }

    @Override
    public Object get(String key) {
        return props.get(key);
    }
}
