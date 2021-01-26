package schemacrawler.tools.offline;

import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;

public final class OfflineCatalogLoader extends BaseCatalogLoader {

  public OfflineCatalogLoader() {
    super(-1);
  }

  @Override
  public void loadCatalog() throws Exception {

    if (isLoaded()) {
      return;
    }

    if (!getSchemaRetrievalOptions()
        .getDatabaseServerType()
        .getDatabaseSystemIdentifier()
        .equals("offline")) {
      return;
    }

    final Connection connection = getConnection();
    if (connection == null || !(connection instanceof OfflineConnection)) {
      return;
    }

    final OfflineConnection dbConnection;
    if (connection.isWrapperFor(OfflineConnection.class)) {
      dbConnection = connection.unwrap(OfflineConnection.class);
    } else {
      dbConnection = (OfflineConnection) connection;
    }

    final Path offlineDatabasePath = dbConnection.getOfflineDatabasePath();
    final FileInputStream inputFileStream = new FileInputStream(offlineDatabasePath.toFile());

    final JavaSerializedCatalog deserializedCatalog = new JavaSerializedCatalog(inputFileStream);

    final Catalog catalog = deserializedCatalog.getCatalog();
    reduceCatalog(catalog);

    setCatalog(catalog);
  }

  private void reduceCatalog(final Catalog catalog) {
    final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
    ((Reducible) catalog).reduce(Schema.class, getSchemaReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Table.class, getTableReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Routine.class, getRoutineReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Synonym.class, getSynonymReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Sequence.class, getSequenceReducer(schemaCrawlerOptions));
  }
}
