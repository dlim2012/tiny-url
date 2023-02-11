package com.dlim2012.shorturl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CassandraShortUrlConfig extends AbstractCassandraConfiguration {

    public final String keyspace;
    public final String contactPoints;
    public final int port;

    CassandraShortUrlConfig(
            @Value("${spring.cassandra_shorturl.contactPoints}") String contactPoints,
            @Value("${spring.cassandra_shorturl.port}") int port,
            @Value("${spring.cassandra_shorturl.keyspace}") String keyspace
    ){
        this.contactPoints = contactPoints;
        this.port = port;
        this.keyspace = keyspace;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.createKeyspace(keyspace)
                .ifNotExists(true);
        return Arrays.asList(specification);
    }

    @Override
    protected List<DropKeyspaceSpecification> getKeyspaceDrops() {
        return Arrays.asList();
    }

    @Override
    public String getContactPoints() {
        return contactPoints;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.dlim2012.shorturl"};
    }

}
