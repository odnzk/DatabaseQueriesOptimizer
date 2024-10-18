package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.impl

import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.util.*

sealed interface SqlQueryParserError {
    class Syntax(val message: String?) : SqlQueryParserError
    data object ResolvingAmbiguity : SqlQueryParserError
}

interface SqlQueryParserErrorListener : ANTLRErrorListener {
    val hasError: Boolean
    fun containsSpecificError(error: SqlQueryParserError) : Boolean
}

class SimpleAntlrErrorListener : SqlQueryParserErrorListener {

    private val errors: HashMap<Class<*>, SqlQueryParserError> = hashMapOf()

    override val hasError: Boolean
        get() = errors.isNotEmpty()

    override fun containsSpecificError(error: SqlQueryParserError): Boolean = errors.contains(error::class.java)

    /* This method is called when the parser encounters a syntax error, such as an unexpected token or invalid input.
     This is one of the most commonly used methods in custom error listeners, as it handles typical syntax errors during parsing.
     */
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        errors[SqlQueryParserError.Syntax::class.java] = SqlQueryParserError.Syntax(msg)
    }

    /*
    This method is called when the parser detects ambiguity in the input, meaning multiple grammar rules could apply
     at a particular point. Ambiguity occurs when more than one valid parse exists for the same input.
     */
    override fun reportAmbiguity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        exact: Boolean,
        ambigAlts: BitSet?,
        configs: ATNConfigSet?
    ) = Unit

    /*
    This method is called when the parser attempts to resolve ambiguity by using full context
     (i.e., it tries to explore all possible parsing options using the entire parse tree).
      This happens when simple lookahead fails to disambiguate the input.
     */
    override fun reportAttemptingFullContext(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        conflictingAlts: BitSet?,
        configs: ATNConfigSet?
    ) {
        errors[SqlQueryParserError.ResolvingAmbiguity::class.java] = SqlQueryParserError.ResolvingAmbiguity
    }

    /*
    This method is called when the parser successfully resolves ambiguity by using context-sensitive information.
     This means the parser managed to differentiate between multiple parsing possibilities based on additional context.
     */
    override fun reportContextSensitivity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        prediction: Int,
        configs: ATNConfigSet?
    ) = Unit
}
