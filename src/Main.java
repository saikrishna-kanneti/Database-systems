package edu.buffalo.www.cse4562;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class Main {

	static String prompt = "$> "; // expected prompt
	static Map<String, List<ColumnDefinition>> tableMap = new HashMap<String, List<ColumnDefinition>>();

	public static void main(String[] args) throws SQLException, IOException {

		while (true) {
			System.out.print(prompt);
			System.out.flush();
			Scanner scanner = new Scanner(System.in);
			InputStream input = new ByteArrayInputStream(scanner.nextLine().getBytes("UTF-8"));

			try {
				CCJSqlParser parser = new CCJSqlParser(input);
				Statement stmt;

				while ((stmt = parser.Statement()) != null) {
					if (stmt instanceof CreateTable) {
						tableMap.putAll(CreateQuery.createTable(stmt));
					} else if (stmt instanceof Select) {
						SelectQuery.selectFrom(stmt, tableMap);
					} else {
						System.out.println("PANIC! I don't know how to handle this:" + stmt);
						System.out.flush();

					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
	}
}
