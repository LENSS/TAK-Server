// Generated from mil/af/rl/rol/Rol.g4 by ANTLR 4.10.1
package mil.af.rl.rol;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RolParser}.
 */
public interface RolListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RolParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(RolParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(RolParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(RolParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(RolParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(RolParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(RolParser.OperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(RolParser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(RolParser.ResourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#entity}.
	 * @param ctx the parse tree
	 */
	void enterEntity(RolParser.EntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#entity}.
	 * @param ctx the parse tree
	 */
	void exitEntity(RolParser.EntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(RolParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(RolParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#assertions}.
	 * @param ctx the parse tree
	 */
	void enterAssertions(RolParser.AssertionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#assertions}.
	 * @param ctx the parse tree
	 */
	void exitAssertions(RolParser.AssertionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#assertion}.
	 * @param ctx the parse tree
	 */
	void enterAssertion(RolParser.AssertionContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#assertion}.
	 * @param ctx the parse tree
	 */
	void exitAssertion(RolParser.AssertionContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#constraintsClause}.
	 * @param ctx the parse tree
	 */
	void enterConstraintsClause(RolParser.ConstraintsClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#constraintsClause}.
	 * @param ctx the parse tree
	 */
	void exitConstraintsClause(RolParser.ConstraintsClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#constraints}.
	 * @param ctx the parse tree
	 */
	void enterConstraints(RolParser.ConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#constraints}.
	 * @param ctx the parse tree
	 */
	void exitConstraints(RolParser.ConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constraintsLeaf}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterConstraintsLeaf(RolParser.ConstraintsLeafContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constraintsLeaf}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitConstraintsLeaf(RolParser.ConstraintsLeafContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constraintsParen}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterConstraintsParen(RolParser.ConstraintsParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constraintsParen}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitConstraintsParen(RolParser.ConstraintsParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constraintsUnaryOp}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterConstraintsUnaryOp(RolParser.ConstraintsUnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constraintsUnaryOp}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitConstraintsUnaryOp(RolParser.ConstraintsUnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#simpleLeafConstraint}.
	 * @param ctx the parse tree
	 */
	void enterSimpleLeafConstraint(RolParser.SimpleLeafConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#simpleLeafConstraint}.
	 * @param ctx the parse tree
	 */
	void exitSimpleLeafConstraint(RolParser.SimpleLeafConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#binaryOp}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOp(RolParser.BinaryOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#binaryOp}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOp(RolParser.BinaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOp(RolParser.UnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOp(RolParser.UnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#role}.
	 * @param ctx the parse tree
	 */
	void enterRole(RolParser.RoleContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#role}.
	 * @param ctx the parse tree
	 */
	void exitRole(RolParser.RoleContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#product}.
	 * @param ctx the parse tree
	 */
	void enterProduct(RolParser.ProductContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#product}.
	 * @param ctx the parse tree
	 */
	void exitProduct(RolParser.ProductContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(RolParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(RolParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterStringParamValue(RolParser.StringParamValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitStringParamValue(RolParser.StringParamValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code identParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterIdentParamValue(RolParser.IdentParamValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code identParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitIdentParamValue(RolParser.IdentParamValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#valuearray}.
	 * @param ctx the parse tree
	 */
	void enterValuearray(RolParser.ValuearrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#valuearray}.
	 * @param ctx the parse tree
	 */
	void exitValuearray(RolParser.ValuearrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link RolParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(RolParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link RolParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(RolParser.ValueContext ctx);
}