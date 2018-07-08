package edu.buffalo.www.cse4562;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.PrimitiveValue.InvalidPrimitive;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

public class SelectQuery {

	private static int flag = 0;
	static String TABLENAME = null;
	public static List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
	static Map<String, List<ColumnDefinition>> projectedTableMap = new HashMap<String, List<ColumnDefinition>>();
	static List<String> columnNames = new ArrayList<String>();
	public static boolean limit_flag=false;
	public static Long row_count=(long) 0;
	public static Long counter=(long) 0;

	public static List<ColumnDefinition> selectFrom(Statement stmt, Map<String, List<ColumnDefinition>> tableMap)
			throws IOException, SQLException {

		SelectBody body = ((Select) stmt).getSelectBody();
		

		if (body instanceof PlainSelect) {
			PlainSelect plain = (PlainSelect) body;
			
			if(plain.getLimit() != null) {
				row_count=plain.getLimit().getRowCount();
				limit_flag=true;
				}
			
			if (plain.getFromItem() instanceof SubSelect) {
				SubSelect sub_select=(SubSelect) plain.getFromItem(); 
				return null;
			} else {
				return plainSelect(plain, tableMap);
			}
		} else {
			return unionSelect(body);

		}
	}

	private static List<ColumnDefinition> plainSelect(PlainSelect plain, Map<String, List<ColumnDefinition>> tableMap)
			throws SQLException, IOException {

		String TABLENAME = plain.getFromItem().toString();
		columnDefinitions = tableMap.get(TABLENAME);
		columnNames = Utilities.colNames(tableMap.get(TABLENAME));

		String tableCSVFile = null;
		File file = new File("data/" + TABLENAME + ".dat");
		if (file.exists()) {
			tableCSVFile = "data/" + TABLENAME + ".dat";
		} else {
			tableCSVFile = "data/" + TABLENAME + ".csv";
		}

		BinaryExpression whereExp = (BinaryExpression) plain.getWhere();

		List<SelectItem> targetColumns = plain.getSelectItems();

		List<ColumnDefinition> projectedColumns = new ArrayList<ColumnDefinition>();
		Iterable<SelectItem> targetColumnIterable = targetColumns;
		for (SelectItem s : targetColumnIterable) {
			if (s.toString().indexOf("AS") >= 0) {
				String[] parts = s.toString().split(" AS ");
				ColumnDefinition tempColumn = new ColumnDefinition();
				tempColumn.setColumnName(parts[1]);
				projectedColumns.add(tempColumn);
			} else {
				ColumnDefinition tempColumn = new ColumnDefinition();
				tempColumn.setColumnName(s.toString());
				projectedColumns.add(tempColumn);
			}
		}
		List<OrderByElement> orderBy = plain.getOrderByElements();
		List<Row> rowList = null;
		if (!(orderBy == null || orderBy.isEmpty())) {
			rowList = reader(tableCSVFile, targetColumns, whereExp, orderBy.get(0));
			Collections.sort(rowList, new OrderByAscending());
		} else {
			rowList = reader(tableCSVFile, targetColumns, whereExp, null);
		}

		printRows(rowList);
		return projectedColumns;
	}

	private static void printRows(List<Row> rowList) {
		Iterable<Row> rowListIterable = rowList;
		for (Row row : rowListIterable) {
			if(limit_flag==false) {
			System.out.println(row.rowAsString);
			System.out.flush();
			}
			else if(limit_flag==true&&counter<row_count) {
				System.out.println(row.rowAsString);
				System.out.flush();
				counter++;
			}
		}
		counter=(long)0;
	}

	private static List<ColumnDefinition> unionSelect(SelectBody body) {
		Union union = (Union) body;
		return null;
	}

	private static List<Row> reader(String csvFile, List<SelectItem> targetColumns, BinaryExpression wherEexp,
			OrderByElement orderByElement) throws IOException, SQLException {
		String line = "";
		String cvsSplitBy = "\\|";
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		List<Row> rowList = new ArrayList<>();
		if (orderByElement != null) {
			while ((line = br.readLine()) != null) {
				// use pipe as delimiter/ separator
				String[] tuple = line.split(cvsSplitBy, -1);
				Row row = orderByReader(tuple, targetColumns, wherEexp, orderByElement);
				if (row != null) {
					rowList.add(row);
				}
			}
		} else {
			while ((line = br.readLine()) != null) {
				// use pipe as delimiter/ separator
				String[] tuple = line.split(cvsSplitBy, -1);
				Row row = orderLessReader(tuple, targetColumns, wherEexp);
				if (row != null) {
					rowList.add(row);
				}
			}
		}
		br.close();
		return rowList;
	}

	private static Row orderByReader(String[] tuple, List<SelectItem> targetColumns, BinaryExpression whereExp,
			OrderByElement orderByElement) throws InvalidPrimitive, SQLException {
		Evaluate e = new Evaluate(tuple, columnNames, columnDefinitions);
		Iterable<SelectItem> targetColumnIterable = targetColumns;
		StringBuilder stringBuilder = new StringBuilder();
		String orderByColumnName = orderByElement.getExpression().toString();
		PrimitiveValue orderByPvalue = null;
		String prefix = "";
		if (EvalExp.conditionalExp(whereExp, e).toBool()) {
			for (SelectItem s : targetColumnIterable) {
				stringBuilder.append(prefix);
				prefix = "|";
				if (s.toString().indexOf("AS") >= 0) {
					SelectExpressionItem aliasItem = (SelectExpressionItem) s;
					if (aliasItem.getExpression() instanceof BinaryExpression) {
						stringBuilder.append(EvalExp.arithmaticExp((BinaryExpression) aliasItem.getExpression(), e));
					} else {

						PrimitiveValue pvalue = e
								.eval(new Column(new Table(TABLENAME), aliasItem.getExpression().toString()));
						if (orderByColumnName.equals(s.toString())) {
							orderByPvalue = pvalue;
						}
						stringBuilder.append(pvalue);
					}

				} else {
					PrimitiveValue pvalue = e.eval(new Column(new Table(TABLENAME), s.toString()));
					if (orderByColumnName.equals(s.toString())) {
						orderByPvalue = pvalue;
					}
					stringBuilder.append(pvalue);
				}

			}

			Row r = new Row(stringBuilder.toString(), orderByPvalue);
			return r;
		}
		return null;
	}

	private static Row orderLessReader(String[] tuple, List<SelectItem> targetColumns, BinaryExpression whereExp)
			throws SQLException {

		Evaluate e = new Evaluate(tuple, columnNames, columnDefinitions);
		Iterable<SelectItem> targetColumnIterable = targetColumns;
		StringBuilder stringBuilder = new StringBuilder();

		String prefix = "";
		if (EvalExp.conditionalExp(whereExp, e).toBool()) {
			for (SelectItem s : targetColumnIterable) {
				stringBuilder.append(prefix);
				prefix = "|";
				if (s.toString().indexOf("AS") >= 0) {
					SelectExpressionItem aliasItem = (SelectExpressionItem) s;
					if (aliasItem.getExpression() instanceof BinaryExpression) {
						stringBuilder.append(EvalExp.arithmaticExp((BinaryExpression) aliasItem.getExpression(), e));
					} else {
						PrimitiveValue pvalue = e
								.eval(new Column(new Table(TABLENAME), aliasItem.getExpression().toString()));
						stringBuilder.append(pvalue);
					}

				} else {
					PrimitiveValue pvalue = e.eval(new Column(new Table(TABLENAME), s.toString()));
					stringBuilder.append(pvalue);
				}

			}
			Row r = new Row(stringBuilder.toString(), null);
			return r;
		}
		return null;
	}

	public static ColDataType getcolumnDataType(String columnName) {

		List<ColumnDefinition> columns = Main.tableMap.get(TABLENAME);
		Iterable<ColumnDefinition> columnIterable = columns;
		for (ColumnDefinition s : columnIterable) {
			if (s.getColumnName().equals(columnName)) {
				return s.getColDataType();
			} else {
				return new ColDataType();
			}
		}
		return null;

	}
}
