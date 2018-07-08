package edu.buffalo.www.cse4562;
import java.util.Comparator;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.PrimitiveValue.InvalidPrimitive;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.PrimitiveType;

public class Row {

	String rowAsString;
	PrimitiveValue orderByElement;

	// Constructor
	public Row(String rowAsString, PrimitiveValue orderByElement) {
		this.rowAsString = rowAsString;
		this.orderByElement = orderByElement;
	}

	public Row(String rowAsString) {
		this.rowAsString = rowAsString;
		this.orderByElement = null;
	}
}

class OrderByAscending implements Comparator<Row> {
	@Override
	public int compare(Row row1, Row row2) {
		PrimitiveValue element1 = row1.orderByElement;
		PrimitiveValue element2 = row2.orderByElement;
		try {
			if (element1 instanceof DoubleValue || element2 instanceof LongValue) {
				if (element1 == null) {
					return -1;
				}
				if (element2 == null) {
					return 1;
				}
				if (element1.toDouble() == element2.toDouble()) {
					return 0;
				}
				if (element1.toDouble() < element2.toDouble())
					return -1;
				if (element1.toDouble() > element2.toDouble())
					return 1;
			} else if (element1 instanceof StringValue) {
				if (element1.toString() == null) {
					return -1;
				}
				if (element2.toString() == null) {
					return 1;
				}
				if (element1.toString().equals(element2.toString())) {
					return 0;
				}
				return element1.toString().compareTo(element2.toString());
			}

		} catch (InvalidPrimitive e) {
			e.printStackTrace();
		}
		return 0;
	}
}

class OrderByDecending implements Comparator<Row> {
	@Override
	public int compare(Row row1, Row row2) {
		return 0;
	}
}
