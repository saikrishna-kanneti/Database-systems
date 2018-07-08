package edu.buffalo.www.cse4562;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

public class CreateQuery {

	public static Map<String, List<ColumnDefinition>> createTable(Statement stmt) {
		CreateTable table = (CreateTable) stmt;
//		System.out.println(table.getTable());

		List<ColumnDefinition> columns = table.getColumnDefinitions();
		String tableName = table.getTable().toString();

		Map<String, List<ColumnDefinition>> tableMap = new HashMap<String, List<ColumnDefinition>>();
		tableMap.put(tableName, columns);

		return tableMap;
	}
}
