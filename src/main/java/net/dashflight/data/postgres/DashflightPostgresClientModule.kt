package net.dashflight.data.postgres;

import com.google.inject.AbstractModule;

public class DashflightPostgresClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PostgresConnectionOptionProvider.class).to(DashflightPostgresConnectionOptionProvider.class);
    }

}
