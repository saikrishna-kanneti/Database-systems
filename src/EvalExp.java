package edu.buffalo.www.cse4562;

import java.sql.SQLException;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;

public class EvalExp {

	public static PrimitiveValue conditionalExp(BinaryExpression exp, Evaluate e) throws SQLException {
		if (exp != null) {
			if (exp instanceof GreaterThanEquals) {
				GreaterThanEquals gte = (GreaterThanEquals) exp;
				Expression left = gte.getLeftExpression();
				Expression right = gte.getRightExpression();
				return e.eval(new GreaterThanEquals(e.eval(left), e.eval(right)));
			} else if (exp instanceof GreaterThan) {
				GreaterThan gte = (GreaterThan) exp;
				Expression left = gte.getLeftExpression();
				Expression right = gte.getRightExpression();
				return e.eval(new GreaterThan(e.eval(left), e.eval(right)));
			} else if (exp instanceof MinorThanEquals) {
				MinorThanEquals gte = (MinorThanEquals) exp;
				Expression left = gte.getLeftExpression();
				Expression right = gte.getRightExpression();
				return e.eval(new MinorThanEquals(e.eval(left), e.eval(right)));
			} else if (exp instanceof MinorThan) {
				MinorThan gte = (MinorThan) exp;
				Expression left = gte.getLeftExpression();
				Expression right = gte.getRightExpression();
				return e.eval(new MinorThan(e.eval(left), e.eval(right)));
			} else if (exp instanceof EqualsTo) {
				EqualsTo gte = (EqualsTo) exp;
				Expression left = gte.getLeftExpression();
				Expression right = gte.getRightExpression();
				return e.eval(new EqualsTo(e.eval(left), e.eval(right)));
			} else if (exp instanceof AndExpression) {
				AndExpression andExpression = (AndExpression) exp;
				Expression left = andExpression.getLeftExpression();
				Expression right = andExpression.getRightExpression();
				return e.eval(new AndExpression(left, right));
			} else if (exp instanceof OrExpression) {
				OrExpression orExpression = (OrExpression) exp;
				Expression left = orExpression.getLeftExpression();
				Expression right = orExpression.getRightExpression();
				return e.eval(new OrExpression(left, right));
			}
		}
		return new LongValue("0");
	}

	public static PrimitiveValue arithmaticExp(BinaryExpression exp, Evaluate e) throws SQLException {

		if (exp instanceof Addition) {
			Addition gte = (Addition) exp;
			Expression left = gte.getLeftExpression();
			Expression right = gte.getRightExpression();
			return e.eval(new Addition(e.eval(left), e.eval(right)));
		} else if (exp instanceof Subtraction) {
			Subtraction gte = (Subtraction) exp;
			Expression left = gte.getLeftExpression();
			Expression right = gte.getRightExpression();
			return e.eval(new Subtraction(e.eval(left), e.eval(right)));
		} else if (exp instanceof Division) {
			Division gte = (Division) exp;
			Expression left = gte.getLeftExpression();
			Expression right = gte.getRightExpression();
			return e.eval(new Division(e.eval(left), e.eval(right)));
		} else if (exp instanceof Multiplication) {
			Multiplication gte = (Multiplication) exp;
			Expression left = gte.getLeftExpression();
			Expression right = gte.getRightExpression();
			return e.eval(new Multiplication(e.eval(left), e.eval(right)));
		} else if (exp instanceof Concat) {
			Concat gte = (Concat) exp;
			Expression left = gte.getLeftExpression();
			Expression right = gte.getRightExpression();
			return e.eval(new Concat(e.eval(left), e.eval(right)));
		}
		return null;
	}
}
