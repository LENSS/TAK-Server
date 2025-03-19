// Generated from mil/af/rl/rol/Rol.g4 by ANTLR 4.10.1
package mil.af.rl.rol;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RolParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RolVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RolParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(RolParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(RolParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperation(RolParser.OperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#resource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource(RolParser.ResourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity(RolParser.EntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(RolParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#assertions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertions(RolParser.AssertionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#assertion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertion(RolParser.AssertionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#constraintsClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintsClause(RolParser.ConstraintsClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#constraints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraints(RolParser.ConstraintsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constraintsLeaf}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintsLeaf(RolParser.ConstraintsLeafContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constraintsParen}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintsParen(RolParser.ConstraintsParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constraintsUnaryOp}
	 * labeled alternative in {@link RolParser#constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintsUnaryOp(RolParser.ConstraintsUnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#simpleLeafConstraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleLeafConstraint(RolParser.SimpleLeafConstraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#binaryOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOp(RolParser.BinaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#unaryOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOp(RolParser.UnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#role}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRole(RolParser.RoleContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#product}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProduct(RolParser.ProductContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(RolParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringParamValue(RolParser.StringParamValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code identParamValue}
	 * labeled alternative in {@link RolParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentParamValue(RolParser.IdentParamValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#valuearray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValuearray(RolParser.ValuearrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link RolParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(RolParser.ValueContext ctx);
}