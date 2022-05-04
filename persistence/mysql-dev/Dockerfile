FROM org.ignast.stock-investing/testutil.bash.bats:1.0-SNAPSHOT

#Test scrips prepending schema selection to SQL scripts
COPY mysql-dev/bash /code/
RUN bats test/makeScriptsUseSchemaTest.bats

FROM mysql:8.0.28-debian

#Copy schema migration scripts
COPY src/main/resources/db/migration /docker-entrypoint-initdb.d/

#Prepend schema selection to migration scripts 
COPY mysql-dev/bash/src/makeScriptsUseSchema.sh /makeScriptsUseSchema.sh
RUN /makeScriptsUseSchema.sh /docker-entrypoint-initdb.d/
RUN rm makeScriptsUseSchema.sh

RUN echo 'CREATE SCHEMA disbursements;' > /docker-entrypoint-initdb.d/V0__create_schema.sql
