/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.systemfunctions;

import static schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType.SYSTEM;
import java.util.function.Function;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.AbstractFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.NoFunctionParameters;

public class SchemaFunctionDefinition extends AbstractFunctionDefinition<NoFunctionParameters> {

  public SchemaFunctionDefinition() {
    super(
        "Called when the user is done with their research, wants to end the chat session.",
        NoFunctionParameters.class);
  }

  @Override
  public Function<NoFunctionParameters, FunctionReturn> getExecutor() {
    if (catalog == null) {
      throw new ExecutionRuntimeException("Catalog is not provided");
    }

    return args -> {
      final CatalogDescription catalogDescription = createCatalogDescription();
      return new SchemaFunctionReturn(catalogDescription);
    };
  }

  @Override
  public FunctionType getFunctionType() {
    return SYSTEM;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }

  protected CatalogDescription createCatalogDescription() {
    final CatalogDescription catalogDescription = new CatalogDescription();
    for (final Table table : catalog.getTables()) {
      final TableDescription tableDescription = new TableDescription();
      tableDescription.setSchema(table.getSchema().getFullName());
      tableDescription.setName(table.getName());
      tableDescription.setRemarks(table.getRemarks());
      for (final Column column : table.getColumns()) {
        final ColumnDescription columnDescription = new ColumnDescription();
        columnDescription.setName(column.getName());
        columnDescription.setDataType(column.getColumnDataType().getName());
        columnDescription.setRemarks(column.getRemarks());
        tableDescription.addColumn(columnDescription);
      }
      catalogDescription.addTable(tableDescription);
    }
    return catalogDescription;
  }
}
