package schemacrawler.tools.command.chatgpt.systemfunctions.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class TableDescription {

  private String name;
  private final List<ColumnDescription> columns;
  private String remarks;

  public TableDescription() {
    columns = new ArrayList<>();
  }

  public void addColumn(final ColumnDescription column) {
    if (column != null) {
      columns.add(column);
    }
  }

  public List<ColumnDescription> getColumns() {
    return columns;
  }

  public String getName() {
    return name;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setRemarks(final String remarks) {
    this.remarks = remarks;
  }
}
