// Generated from mil/af/rl/rol/Rol.g4 by ANTLR 4.10.1
package mil.af.rl.rol;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RolParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, IDENT=47, STRING=48, NUMBER=49, SEMI=50, LPAREN=51, RPAREN=52, 
		LCBRACE=53, RCBRACE=54, LSBRACKET=55, RSBRACKET=56, DBLQUOTE=57, WS=58;
	public static final int
		RULE_program = 0, RULE_statement = 1, RULE_operation = 2, RULE_resource = 3, 
		RULE_entity = 4, RULE_assignment = 5, RULE_assertions = 6, RULE_assertion = 7, 
		RULE_constraintsClause = 8, RULE_constraints = 9, RULE_constraint = 10, 
		RULE_simpleLeafConstraint = 11, RULE_binaryOp = 12, RULE_unaryOp = 13, 
		RULE_role = 14, RULE_product = 15, RULE_parameters = 16, RULE_parameter = 17, 
		RULE_valuearray = 18, RULE_value = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "statement", "operation", "resource", "entity", "assignment", 
			"assertions", "assertion", "constraintsClause", "constraints", "constraint", 
			"simpleLeafConstraint", "binaryOp", "unaryOp", "role", "product", "parameters", 
			"parameter", "valuearray", "value"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'create'", "'add'", "'remove'", "'update'", "'assign'", "'revoke'", 
			"'get'", "'post'", "'put'", "'delete'", "'contains'", "'request'", "'announce'", 
			"'disperse'", "'store'", "'enable'", "'disable'", "'query'", "'subscription'", 
			"'staticsubscription'", "'role'", "'priority'", "'resource'", "'package'", 
			"'mission'", "'input'", "'federation'", "'federate'", "'federated_connection'", 
			"'data_feed'", "'MSG'", "'PUBLISHER'", "'CONSUMER'", "'for'", "'matchrole'", 
			"'match attribute'", "'constraint'", "'min'", "'max'", "'and'", "'or'", 
			"'not'", "','", "':'", "'true'", "'false'", null, null, null, "';'", 
			"'('", "')'", "'{'", "'}'", "'['", "']'", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, "IDENT", 
			"STRING", "NUMBER", "SEMI", "LPAREN", "RPAREN", "LCBRACE", "RCBRACE", 
			"LSBRACKET", "RSBRACKET", "DBLQUOTE", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Rol.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RolParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(RolParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(40);
				statement();
				}
				}
				setState(43); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0) );
			setState(45);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(RolParser.SEMI, 0); }
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ConstraintsClauseContext constraintsClause() {
			return getRuleContext(ConstraintsClauseContext.class,0);
		}
		public AssertionsContext assertions() {
			return getRuleContext(AssertionsContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			operation();
			setState(48);
			resource();
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__33) {
				{
				setState(49);
				assignment();
				}
			}

			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__36) {
				{
				setState(52);
				constraintsClause();
				}
			}

			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__35) | (1L << T__41) | (1L << LPAREN))) != 0)) {
				{
				setState(55);
				assertions();
				}
			}

			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENT || _la==LCBRACE) {
				{
				setState(58);
				parameters();
				}
			}

			setState(61);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperationContext extends ParserRuleContext {
		public OperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationContext operation() throws RecognitionException {
		OperationContext _localctx = new OperationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_operation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ResourceContext extends ParserRuleContext {
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public ResourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterResource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitResource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitResource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResourceContext resource() throws RecognitionException {
		ResourceContext _localctx = new ResourceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_resource);
		try {
			setState(80);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				match(T__17);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				match(T__18);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(67);
				match(T__19);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(68);
				match(T__20);
				setState(69);
				role();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(70);
				match(T__20);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(71);
				match(T__21);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(72);
				match(T__22);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(73);
				match(T__23);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(74);
				match(T__24);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(75);
				match(T__25);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(76);
				match(T__26);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(77);
				match(T__27);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(78);
				match(T__28);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(79);
				match(T__29);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntityContext extends ParserRuleContext {
		public EntityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterEntity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitEntity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitEntity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityContext entity() throws RecognitionException {
		EntityContext _localctx = new EntityContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_entity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__30) | (1L << T__31) | (1L << T__32))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public EntityContext entity() {
			return getRuleContext(EntityContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(T__33);
			setState(85);
			entity();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssertionsContext extends ParserRuleContext {
		public List<AssertionContext> assertion() {
			return getRuleContexts(AssertionContext.class);
		}
		public AssertionContext assertion(int i) {
			return getRuleContext(AssertionContext.class,i);
		}
		public List<BinaryOpContext> binaryOp() {
			return getRuleContexts(BinaryOpContext.class);
		}
		public BinaryOpContext binaryOp(int i) {
			return getRuleContext(BinaryOpContext.class,i);
		}
		public AssertionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterAssertions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitAssertions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitAssertions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssertionsContext assertions() throws RecognitionException {
		AssertionsContext _localctx = new AssertionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assertions);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			assertion();
			setState(93);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(88);
					binaryOp();
					setState(89);
					assertion();
					}
					} 
				}
				setState(95);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssertionContext extends ParserRuleContext {
		public RoleContext role() {
			return getRuleContext(RoleContext.class,0);
		}
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(RolParser.LPAREN, 0); }
		public AssertionsContext assertions() {
			return getRuleContext(AssertionsContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(RolParser.RPAREN, 0); }
		public UnaryOpContext unaryOp() {
			return getRuleContext(UnaryOpContext.class,0);
		}
		public AssertionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterAssertion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitAssertion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitAssertion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssertionContext assertion() throws RecognitionException {
		AssertionContext _localctx = new AssertionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_assertion);
		try {
			setState(107);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__34:
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				match(T__34);
				setState(97);
				role();
				}
				break;
			case T__35:
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				match(T__35);
				setState(99);
				parameter();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				match(LPAREN);
				setState(101);
				assertions();
				setState(102);
				match(RPAREN);
				}
				break;
			case T__41:
				enterOuterAlt(_localctx, 4);
				{
				setState(104);
				unaryOp();
				setState(105);
				assertions();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintsClauseContext extends ParserRuleContext {
		public ConstraintsContext constraints() {
			return getRuleContext(ConstraintsContext.class,0);
		}
		public ConstraintsClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintsClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterConstraintsClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitConstraintsClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitConstraintsClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintsClauseContext constraintsClause() throws RecognitionException {
		ConstraintsClauseContext _localctx = new ConstraintsClauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_constraintsClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(T__36);
			setState(110);
			constraints();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintsContext extends ParserRuleContext {
		public List<ConstraintContext> constraint() {
			return getRuleContexts(ConstraintContext.class);
		}
		public ConstraintContext constraint(int i) {
			return getRuleContext(ConstraintContext.class,i);
		}
		public List<BinaryOpContext> binaryOp() {
			return getRuleContexts(BinaryOpContext.class);
		}
		public BinaryOpContext binaryOp(int i) {
			return getRuleContext(BinaryOpContext.class,i);
		}
		public ConstraintsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraints; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterConstraints(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitConstraints(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitConstraints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintsContext constraints() throws RecognitionException {
		ConstraintsContext _localctx = new ConstraintsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_constraints);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			constraint();
			setState(118);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(113);
					binaryOp();
					setState(114);
					constraint();
					}
					} 
				}
				setState(120);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintContext extends ParserRuleContext {
		public ConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint; }
	 
		public ConstraintContext() { }
		public void copyFrom(ConstraintContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ConstraintsLeafContext extends ConstraintContext {
		public SimpleLeafConstraintContext simpleLeafConstraint() {
			return getRuleContext(SimpleLeafConstraintContext.class,0);
		}
		public ConstraintsLeafContext(ConstraintContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterConstraintsLeaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitConstraintsLeaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitConstraintsLeaf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstraintsParenContext extends ConstraintContext {
		public TerminalNode LPAREN() { return getToken(RolParser.LPAREN, 0); }
		public ConstraintsContext constraints() {
			return getRuleContext(ConstraintsContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(RolParser.RPAREN, 0); }
		public ConstraintsParenContext(ConstraintContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterConstraintsParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitConstraintsParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitConstraintsParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstraintsUnaryOpContext extends ConstraintContext {
		public UnaryOpContext unaryOp() {
			return getRuleContext(UnaryOpContext.class,0);
		}
		public ConstraintsContext constraints() {
			return getRuleContext(ConstraintsContext.class,0);
		}
		public ConstraintsUnaryOpContext(ConstraintContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterConstraintsUnaryOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitConstraintsUnaryOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitConstraintsUnaryOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintContext constraint() throws RecognitionException {
		ConstraintContext _localctx = new ConstraintContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_constraint);
		try {
			setState(129);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__37:
			case T__38:
				_localctx = new ConstraintsLeafContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(121);
				simpleLeafConstraint();
				}
				break;
			case LPAREN:
				_localctx = new ConstraintsParenContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(122);
				match(LPAREN);
				setState(123);
				constraints();
				setState(124);
				match(RPAREN);
				}
				break;
			case T__41:
				_localctx = new ConstraintsUnaryOpContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(126);
				unaryOp();
				setState(127);
				constraints();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleLeafConstraintContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public SimpleLeafConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleLeafConstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterSimpleLeafConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitSimpleLeafConstraint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitSimpleLeafConstraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleLeafConstraintContext simpleLeafConstraint() throws RecognitionException {
		SimpleLeafConstraintContext _localctx = new SimpleLeafConstraintContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_simpleLeafConstraint);
		try {
			setState(135);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__37:
				enterOuterAlt(_localctx, 1);
				{
				setState(131);
				match(T__37);
				setState(132);
				value();
				}
				break;
			case T__38:
				enterOuterAlt(_localctx, 2);
				{
				setState(133);
				match(T__38);
				setState(134);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryOpContext extends ParserRuleContext {
		public BinaryOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binaryOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterBinaryOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitBinaryOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitBinaryOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryOpContext binaryOp() throws RecognitionException {
		BinaryOpContext _localctx = new BinaryOpContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_binaryOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_la = _input.LA(1);
			if ( !(_la==T__39 || _la==T__40) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryOpContext extends ParserRuleContext {
		public UnaryOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterUnaryOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitUnaryOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitUnaryOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOpContext unaryOp() throws RecognitionException {
		UnaryOpContext _localctx = new UnaryOpContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_unaryOp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(T__41);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RoleContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(RolParser.IDENT, 0); }
		public TerminalNode STRING() { return getToken(RolParser.STRING, 0); }
		public RoleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_role; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterRole(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitRole(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitRole(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoleContext role() throws RecognitionException {
		RoleContext _localctx = new RoleContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_role);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==STRING) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProductContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(RolParser.IDENT, 0); }
		public ProductContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_product; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterProduct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitProduct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitProduct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProductContext product() throws RecognitionException {
		ProductContext _localctx = new ProductContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_product);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(RolParser.IDENT, 0); }
		public TerminalNode LCBRACE() { return getToken(RolParser.LCBRACE, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TerminalNode RCBRACE() { return getToken(RolParser.RCBRACE, 0); }
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_parameters);
		int _la;
		try {
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				match(IDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				match(LCBRACE);
				setState(147);
				parameter();
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__42) {
					{
					{
					setState(148);
					match(T__42);
					setState(149);
					parameter();
					}
					}
					setState(154);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(155);
				match(RCBRACE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(157);
				match(LCBRACE);
				setState(158);
				match(RCBRACE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
	 
		public ParameterContext() { }
		public void copyFrom(ParameterContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IdentParamValueContext extends ParameterContext {
		public TerminalNode IDENT() { return getToken(RolParser.IDENT, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public IdentParamValueContext(ParameterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterIdentParamValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitIdentParamValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitIdentParamValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringParamValueContext extends ParameterContext {
		public TerminalNode STRING() { return getToken(RolParser.STRING, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public StringParamValueContext(ParameterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterStringParamValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitStringParamValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitStringParamValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_parameter);
		try {
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringParamValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				match(STRING);
				setState(162);
				match(T__43);
				setState(163);
				value();
				}
				break;
			case IDENT:
				_localctx = new IdentParamValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				match(IDENT);
				setState(165);
				match(T__43);
				setState(166);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValuearrayContext extends ParserRuleContext {
		public TerminalNode LSBRACKET() { return getToken(RolParser.LSBRACKET, 0); }
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode RSBRACKET() { return getToken(RolParser.RSBRACKET, 0); }
		public ValuearrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valuearray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterValuearray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitValuearray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitValuearray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuearrayContext valuearray() throws RecognitionException {
		ValuearrayContext _localctx = new ValuearrayContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_valuearray);
		int _la;
		try {
			setState(182);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(169);
				match(LSBRACKET);
				setState(170);
				value();
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__42) {
					{
					{
					setState(171);
					match(T__42);
					setState(172);
					value();
					}
					}
					setState(177);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(178);
				match(RSBRACKET);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(180);
				match(LSBRACKET);
				setState(181);
				match(RSBRACKET);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(RolParser.STRING, 0); }
		public TerminalNode IDENT() { return getToken(RolParser.IDENT, 0); }
		public TerminalNode NUMBER() { return getToken(RolParser.NUMBER, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ValuearrayContext valuearray() {
			return getRuleContext(ValuearrayContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RolListener ) ((RolListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RolVisitor ) return ((RolVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_value);
		try {
			setState(191);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(184);
				match(STRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				match(IDENT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(186);
				match(NUMBER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(187);
				parameters();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(188);
				valuearray();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(189);
				match(T__44);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(190);
				match(T__45);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001:\u00c2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0001\u0000\u0004\u0000*\b\u0000\u000b\u0000"+
		"\f\u0000+\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u00013\b\u0001\u0001\u0001\u0003\u00016\b\u0001\u0001\u0001\u0003"+
		"\u00019\b\u0001\u0001\u0001\u0003\u0001<\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"Q\b\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006\\\b\u0006"+
		"\n\u0006\f\u0006_\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0003\u0007l\b\u0007\u0001\b\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0005\tu\b\t\n\t\f\tx\t\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0082\b\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0088\b\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u0097"+
		"\b\u0010\n\u0010\f\u0010\u009a\t\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0003\u0010\u00a0\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00a8\b\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00ae\b\u0012\n\u0012"+
		"\f\u0012\u00b1\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0003\u0012\u00b7\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00c0\b\u0013\u0001\u0013"+
		"\u0000\u0000\u0014\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u001e \"$&\u0000\u0004\u0001\u0000\u0001\u0011"+
		"\u0001\u0000\u001f!\u0001\u0000()\u0001\u0000/0\u00d3\u0000)\u0001\u0000"+
		"\u0000\u0000\u0002/\u0001\u0000\u0000\u0000\u0004?\u0001\u0000\u0000\u0000"+
		"\u0006P\u0001\u0000\u0000\u0000\bR\u0001\u0000\u0000\u0000\nT\u0001\u0000"+
		"\u0000\u0000\fW\u0001\u0000\u0000\u0000\u000ek\u0001\u0000\u0000\u0000"+
		"\u0010m\u0001\u0000\u0000\u0000\u0012p\u0001\u0000\u0000\u0000\u0014\u0081"+
		"\u0001\u0000\u0000\u0000\u0016\u0087\u0001\u0000\u0000\u0000\u0018\u0089"+
		"\u0001\u0000\u0000\u0000\u001a\u008b\u0001\u0000\u0000\u0000\u001c\u008d"+
		"\u0001\u0000\u0000\u0000\u001e\u008f\u0001\u0000\u0000\u0000 \u009f\u0001"+
		"\u0000\u0000\u0000\"\u00a7\u0001\u0000\u0000\u0000$\u00b6\u0001\u0000"+
		"\u0000\u0000&\u00bf\u0001\u0000\u0000\u0000(*\u0003\u0002\u0001\u0000"+
		")(\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000"+
		"\u0000+,\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0005\u0000"+
		"\u0000\u0001.\u0001\u0001\u0000\u0000\u0000/0\u0003\u0004\u0002\u0000"+
		"02\u0003\u0006\u0003\u000013\u0003\n\u0005\u000021\u0001\u0000\u0000\u0000"+
		"23\u0001\u0000\u0000\u000035\u0001\u0000\u0000\u000046\u0003\u0010\b\u0000"+
		"54\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u000068\u0001\u0000\u0000"+
		"\u000079\u0003\f\u0006\u000087\u0001\u0000\u0000\u000089\u0001\u0000\u0000"+
		"\u00009;\u0001\u0000\u0000\u0000:<\u0003 \u0010\u0000;:\u0001\u0000\u0000"+
		"\u0000;<\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u00052\u0000"+
		"\u0000>\u0003\u0001\u0000\u0000\u0000?@\u0007\u0000\u0000\u0000@\u0005"+
		"\u0001\u0000\u0000\u0000AQ\u0005\u0012\u0000\u0000BQ\u0005\u0013\u0000"+
		"\u0000CQ\u0005\u0014\u0000\u0000DE\u0005\u0015\u0000\u0000EQ\u0003\u001c"+
		"\u000e\u0000FQ\u0005\u0015\u0000\u0000GQ\u0005\u0016\u0000\u0000HQ\u0005"+
		"\u0017\u0000\u0000IQ\u0005\u0018\u0000\u0000JQ\u0005\u0019\u0000\u0000"+
		"KQ\u0005\u001a\u0000\u0000LQ\u0005\u001b\u0000\u0000MQ\u0005\u001c\u0000"+
		"\u0000NQ\u0005\u001d\u0000\u0000OQ\u0005\u001e\u0000\u0000PA\u0001\u0000"+
		"\u0000\u0000PB\u0001\u0000\u0000\u0000PC\u0001\u0000\u0000\u0000PD\u0001"+
		"\u0000\u0000\u0000PF\u0001\u0000\u0000\u0000PG\u0001\u0000\u0000\u0000"+
		"PH\u0001\u0000\u0000\u0000PI\u0001\u0000\u0000\u0000PJ\u0001\u0000\u0000"+
		"\u0000PK\u0001\u0000\u0000\u0000PL\u0001\u0000\u0000\u0000PM\u0001\u0000"+
		"\u0000\u0000PN\u0001\u0000\u0000\u0000PO\u0001\u0000\u0000\u0000Q\u0007"+
		"\u0001\u0000\u0000\u0000RS\u0007\u0001\u0000\u0000S\t\u0001\u0000\u0000"+
		"\u0000TU\u0005\"\u0000\u0000UV\u0003\b\u0004\u0000V\u000b\u0001\u0000"+
		"\u0000\u0000W]\u0003\u000e\u0007\u0000XY\u0003\u0018\f\u0000YZ\u0003\u000e"+
		"\u0007\u0000Z\\\u0001\u0000\u0000\u0000[X\u0001\u0000\u0000\u0000\\_\u0001"+
		"\u0000\u0000\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000"+
		"^\r\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000`a\u0005#\u0000\u0000"+
		"al\u0003\u001c\u000e\u0000bc\u0005$\u0000\u0000cl\u0003\"\u0011\u0000"+
		"de\u00053\u0000\u0000ef\u0003\f\u0006\u0000fg\u00054\u0000\u0000gl\u0001"+
		"\u0000\u0000\u0000hi\u0003\u001a\r\u0000ij\u0003\f\u0006\u0000jl\u0001"+
		"\u0000\u0000\u0000k`\u0001\u0000\u0000\u0000kb\u0001\u0000\u0000\u0000"+
		"kd\u0001\u0000\u0000\u0000kh\u0001\u0000\u0000\u0000l\u000f\u0001\u0000"+
		"\u0000\u0000mn\u0005%\u0000\u0000no\u0003\u0012\t\u0000o\u0011\u0001\u0000"+
		"\u0000\u0000pv\u0003\u0014\n\u0000qr\u0003\u0018\f\u0000rs\u0003\u0014"+
		"\n\u0000su\u0001\u0000\u0000\u0000tq\u0001\u0000\u0000\u0000ux\u0001\u0000"+
		"\u0000\u0000vt\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000w\u0013"+
		"\u0001\u0000\u0000\u0000xv\u0001\u0000\u0000\u0000y\u0082\u0003\u0016"+
		"\u000b\u0000z{\u00053\u0000\u0000{|\u0003\u0012\t\u0000|}\u00054\u0000"+
		"\u0000}\u0082\u0001\u0000\u0000\u0000~\u007f\u0003\u001a\r\u0000\u007f"+
		"\u0080\u0003\u0012\t\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081y"+
		"\u0001\u0000\u0000\u0000\u0081z\u0001\u0000\u0000\u0000\u0081~\u0001\u0000"+
		"\u0000\u0000\u0082\u0015\u0001\u0000\u0000\u0000\u0083\u0084\u0005&\u0000"+
		"\u0000\u0084\u0088\u0003&\u0013\u0000\u0085\u0086\u0005\'\u0000\u0000"+
		"\u0086\u0088\u0003&\u0013\u0000\u0087\u0083\u0001\u0000\u0000\u0000\u0087"+
		"\u0085\u0001\u0000\u0000\u0000\u0088\u0017\u0001\u0000\u0000\u0000\u0089"+
		"\u008a\u0007\u0002\u0000\u0000\u008a\u0019\u0001\u0000\u0000\u0000\u008b"+
		"\u008c\u0005*\u0000\u0000\u008c\u001b\u0001\u0000\u0000\u0000\u008d\u008e"+
		"\u0007\u0003\u0000\u0000\u008e\u001d\u0001\u0000\u0000\u0000\u008f\u0090"+
		"\u0005/\u0000\u0000\u0090\u001f\u0001\u0000\u0000\u0000\u0091\u00a0\u0005"+
		"/\u0000\u0000\u0092\u0093\u00055\u0000\u0000\u0093\u0098\u0003\"\u0011"+
		"\u0000\u0094\u0095\u0005+\u0000\u0000\u0095\u0097\u0003\"\u0011\u0000"+
		"\u0096\u0094\u0001\u0000\u0000\u0000\u0097\u009a\u0001\u0000\u0000\u0000"+
		"\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000"+
		"\u0099\u009b\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u00056\u0000\u0000\u009c\u00a0\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u00055\u0000\u0000\u009e\u00a0\u00056\u0000\u0000\u009f\u0091\u0001"+
		"\u0000\u0000\u0000\u009f\u0092\u0001\u0000\u0000\u0000\u009f\u009d\u0001"+
		"\u0000\u0000\u0000\u00a0!\u0001\u0000\u0000\u0000\u00a1\u00a2\u00050\u0000"+
		"\u0000\u00a2\u00a3\u0005,\u0000\u0000\u00a3\u00a8\u0003&\u0013\u0000\u00a4"+
		"\u00a5\u0005/\u0000\u0000\u00a5\u00a6\u0005,\u0000\u0000\u00a6\u00a8\u0003"+
		"&\u0013\u0000\u00a7\u00a1\u0001\u0000\u0000\u0000\u00a7\u00a4\u0001\u0000"+
		"\u0000\u0000\u00a8#\u0001\u0000\u0000\u0000\u00a9\u00aa\u00057\u0000\u0000"+
		"\u00aa\u00af\u0003&\u0013\u0000\u00ab\u00ac\u0005+\u0000\u0000\u00ac\u00ae"+
		"\u0003&\u0013\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ae\u00b1\u0001"+
		"\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000\u00af\u00b0\u0001"+
		"\u0000\u0000\u0000\u00b0\u00b2\u0001\u0000\u0000\u0000\u00b1\u00af\u0001"+
		"\u0000\u0000\u0000\u00b2\u00b3\u00058\u0000\u0000\u00b3\u00b7\u0001\u0000"+
		"\u0000\u0000\u00b4\u00b5\u00057\u0000\u0000\u00b5\u00b7\u00058\u0000\u0000"+
		"\u00b6\u00a9\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000"+
		"\u00b7%\u0001\u0000\u0000\u0000\u00b8\u00c0\u00050\u0000\u0000\u00b9\u00c0"+
		"\u0005/\u0000\u0000\u00ba\u00c0\u00051\u0000\u0000\u00bb\u00c0\u0003 "+
		"\u0010\u0000\u00bc\u00c0\u0003$\u0012\u0000\u00bd\u00c0\u0005-\u0000\u0000"+
		"\u00be\u00c0\u0005.\u0000\u0000\u00bf\u00b8\u0001\u0000\u0000\u0000\u00bf"+
		"\u00b9\u0001\u0000\u0000\u0000\u00bf\u00ba\u0001\u0000\u0000\u0000\u00bf"+
		"\u00bb\u0001\u0000\u0000\u0000\u00bf\u00bc\u0001\u0000\u0000\u0000\u00bf"+
		"\u00bd\u0001\u0000\u0000\u0000\u00bf\u00be\u0001\u0000\u0000\u0000\u00c0"+
		"\'\u0001\u0000\u0000\u0000\u0011+258;P]kv\u0081\u0087\u0098\u009f\u00a7"+
		"\u00af\u00b6\u00bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}