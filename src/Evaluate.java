package edu.buffalo.www.cse4562;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.PrimitiveType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class Evaluate extends Eval {
	public static String STRING = "string";
	public String[] tuple;
	public List<String> columnNames;
	public List<ColumnDefinition> columnDefinitions;

	public Evaluate(String[] tuple, List<String> columnNames, List<ColumnDefinition> columnDefinitions) {
		this.tuple = tuple;
		this.columnNames = columnNames;
		this.columnDefinitions = columnDefinitions;
	}

	@Override
	public PrimitiveValue eval(Column col) throws SQLException {

		int index = columnNames.indexOf(col.getColumnName());
		String value = tuple[index];

		if (PrimitiveType.fromString(columnDefinitions.get(index).getColDataType().getDataType())
				.equals(PrimitiveType.DOUBLE)) {
			return new DoubleValue(value);
		} else if (PrimitiveType.fromString(columnDefinitions.get(index).getColDataType().getDataType())
				.equals(PrimitiveType.LONG)) {
			return new LongValue(value);
		} else if (PrimitiveType.fromString(columnDefinitions.get(index).getColDataType().getDataType())
				.equals(PrimitiveType.DATE)) {
			return new DateValue(value);
		} else {
			return new StringValue(value.toString());
		}
	}

	public static boolean isDouble(String s) {
		return s != null && s.matches("[-+]?\\d*\\.?\\d+");
	}

	public static boolean isLong(String s) {
		return s != null && s.matches("[-+]?[0-9]*");
	}

	public static boolean isValidDate(String s) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			df.parse(s);
			return true;
		} catch (java.text.ParseException e) {
			return false;
		}
	}
}
