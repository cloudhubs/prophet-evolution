package edu.university.ecs.lab.delta.utils;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class StatementEqualityUtils {
  /**
   * Check equality between two statements
   *
   * @param localStatement statement in local files
   * @param changeStatement changed line statement
   * @return true if statements are equal, false otherwise
   */
  public static boolean checkEquality(Statement localStatement, Statement changeStatement) {
    if (localStatement == null || changeStatement == null) {
      return false;
    }

    // use visitor pattern to make comparison via statement subtype
    StatementEqualityVisitor visitor = new StatementEqualityVisitor(changeStatement);
    localStatement.accept(visitor, null);
    return visitor.isEqual();
  }

  /** Visitor class for checking equality of statement subtypes. */
  private static class StatementEqualityVisitor extends VoidVisitorAdapter<Void> {
    private final Statement changeStatement;
    private boolean equal;

    public StatementEqualityVisitor(Statement changeStatement) {
      this.changeStatement = changeStatement;
      this.equal = false;
    }

    public boolean isEqual() {
      return equal;
    }

    @Override
    public void visit(ExpressionStmt exprStmt, Void arg) {
      if (changeStatement.isExpressionStmt()) {
        equal = exprStmt.getExpression().equals(changeStatement.asExpressionStmt().getExpression());
      }
    }

    @Override
    public void visit(BlockStmt blockStmt, Void arg) {
      if (changeStatement.isBlockStmt()) {
        equal = blockStmt.equals(changeStatement);
      }
    }

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
      if (changeStatement.isIfStmt()) {
        equal = ifStmt.equals(changeStatement);
      }
    }

    @Override
    public void visit(ForStmt forStmt, Void arg) {
      if (changeStatement.isForStmt()) {
        equal = forStmt.equals(changeStatement);
      }
    }
  }
}
