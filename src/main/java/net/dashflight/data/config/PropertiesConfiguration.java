package net.dashflight.data.config;

import java.util.Properties;

public class PropertiesConfiguration implements ConfigurationData<Properties> {

    private Properties props;

    public PropertiesConfiguration(Properties props) {
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
