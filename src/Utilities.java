package edu.buffalo.www.cse4562;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class Utilities {

	public static List<String> colNames(List<ColumnDefinition> colDef) {

		List<String> columnNames = new ArrayList<String>();

		Iterable<ColumnDefinition> columnIterable = colDef;

		for (ColumnDefinition s : columnIterable) {
			columnNames.add(s.getColumnName());
		}

		return columnNames;

	}

}
