package edu.buffalo.www.cse4562;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ResolveColumnName {

	public static List<Integer> resolve(List<String> targetColumns, List<String> columnNames) {

		Iterable<String> targetColumnIterable = targetColumns;
		List<Integer> indexes = new ArrayList<Integer>();
		for (String s : targetColumnIterable) {
			if (s.indexOf(".*") >= 0 | s.indexOf("*") >= 0) {
				for (int i = 0; i < columnNames.size(); i++) {
					indexes.add(i); // Needs Attention: Assumption These is only R.* and not R.*,S.A etc
				}
			} else if (s.indexOf(".") >= 0) {
				String[] parts = s.split("\\.");
				indexes.add(columnNames.indexOf(parts[1]));
			} else if(s.indexOf(" AS ") >= 0){
				String[] parts = s.split(" AS ");
				indexes.add(columnNames.indexOf(parts[0]));
			}else{
				indexes.add(columnNames.indexOf(s));
			}
		}
		return indexes;

	}

	public static List<String> columnNames(List<SelectItem> targetColumns) {
		Iterable<SelectItem> targetColumnIterable = targetColumns;
		List<String> columNameList = new ArrayList<String>();

		for (SelectItem s : targetColumnIterable) {
			columNameList.add(s.toString());
		}
		return columNameList;
	}

	public static List<String> resolveColumnName(List<SelectItem> targetColumns, List<String> columnNames) {

		Iterable<SelectItem> targetColumnIterable = targetColumns;
		List<String> columNameList = new ArrayList<String>();
		for (SelectItem s : targetColumnIterable) {
			if (s.toString().indexOf(".*") >= 0 | s.toString().indexOf("*") >= 0) {
				columNameList.addAll(columnNames);
			} else if (s.toString().indexOf(".") >= 0) {
				String[] parts = s.toString().split("\\.");
				columNameList.add(parts[1]);
			} else {
				columNameList.add(s.toString());
			}
		}
		return columNameList;

	}
}
