package tk.skuro.idea.orgmode.parser;

import com.intellij.psi.tree.IElementType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LexerTest {

    private volatile OrgLexer lexer;

    @Before
    public void setup() {
        lexer = new OrgLexer();
    }

    @Test
    public void canReadWhitespace() {
        final String whites = "  \t\n";
        lexer.start(whites);

        assertEquals("Whitespaces not properly recognized", OrgTokenTypes.WHITE_SPACE, lexer.getTokenType());
        assertEquals("Whitespaces of length > 1 not properly recognized", 4, lexer.getTokenEnd() - lexer.getTokenStart());
    }

    @Test
    public void canReadComments() {
        final String comment = "# I'm a comment";
        lexer.start(comment);

        assertEquals("Comment not properly parsed", OrgTokenTypes.COMMENT, lexer.getTokenType());
        assertEquals("Comment parsing stops too early", comment, lexer.getTokenText());
    }

    @Test
    public void canReadCommentsWithHeadingWhitespaces() {
        final String comment = "  # I'm a comment";
        lexer.start(comment);

        eatWhitespace();
        assertEquals("Comment not properly parsed", OrgTokenTypes.COMMENT, lexer.getTokenType());
    }

    @Test
    public void canReadOutlines() {
        final String outlines =
                "* I'm an outline\n" +
                "** I'm a second outline";
        lexer.start(outlines);
        assertEquals("Outline not properly parsed", OrgTokenTypes.OUTLINE, lexer.getTokenType());

        eatWhitespace();
        assertEquals("Outline not properly parsed", OrgTokenTypes.OUTLINE, lexer.getTokenType());
    }


    @Test
    public void canReadVerbatim() {
        final String outlines =
            "haha ~some inline code~ end";
        lexer.start(outlines);
        assertEquals("Outline not properly parsed", OrgTokenTypes.VERBATIM, lexer.getTokenType());

        eatWhitespace();
        assertEquals("Outline not properly parsed", OrgTokenTypes.VERBATIM, lexer.getTokenType());
    }

    @Test
    public void canReadBlocks() {
        final String block =
                "#+BEGIN_SRC\n" +
                "(defn foobar)\n" +
                "#+END_SRC";

        lexer.start(block);
        assertEquals("Block start not properly parsed", OrgTokenTypes.BLOCK_START, lexer.getTokenType());

        lexer.advance();
        eatWhitespace();
        assertEquals("Block content not properly parsed", OrgTokenTypes.BLOCK_CONTENT, lexer.getTokenType());

        eatBlockContent();
        eatWhitespace();
        assertEquals("Block end not properly parsed", OrgTokenTypes.BLOCK_END, lexer.getTokenType());
    }

    @Test
    public void detectsWrongBlocks(){
        final String notABlock =
                        "#+END_SRC\n" +
                        "(defn foobar)\n" +
                        "#+BEGIN_SRC";

        lexer.start(notABlock);
        assertEquals("Block end not properly parsed", OrgTokenTypes.UNMATCHED_DELIMITER, lexer.getTokenType());
    }

    @Test
    public void canReadKeyword(){
        final String keyword = "#+FOOBAR: foobar all the way down";

        lexer.start(keyword);
        assertEquals("Keyword not properly parsed", OrgTokenTypes.KEYWORD, lexer.getTokenType());
    }

    @Test
    public void canReadKeywordWithHeadingWhitespace(){
        final String keyword = "   #+FOOBAR: foobar all the way down";

        lexer.start(keyword);
        assertEquals("Keyword not properly parsed", OrgTokenTypes.KEYWORD, lexer.getTokenType());
    }

    @Test
    public void canReadUnderline(){
        final String underlined = "_Ima underline text_";

        lexer.start(underlined);
        assertEquals("Underline not properly parsed", OrgTokenTypes.UNDERLINE, lexer.getTokenType());
        assertEquals("Underline not properly parsed", underlined, lexer.getTokenText());
    }

    @Test
    public void canReadBold(){
        final String bold = " *I'm a bold text*";
        lexer.start(bold);

        eatWhitespace();

        assertEquals("Underline not properly parsed", OrgTokenTypes.BOLD, lexer.getTokenType());
        assertEquals("Underline not properly parsed", bold.substring(1), lexer.getTokenText());

        final String bold2 = "After some _underline_ I have some *bold*";
        lexer.start(bold2);

        eatUntil(OrgTokenTypes.TEXT, OrgTokenTypes.WHITE_SPACE);
        eatUntil(OrgTokenTypes.UNDERLINE);
        eatUntil(OrgTokenTypes.TEXT, OrgTokenTypes.WHITE_SPACE);
        assertEquals("Underline not properly parsed", OrgTokenTypes.BOLD, lexer.getTokenType());
        assertEquals("Underline not properly parsed", "*bold*", lexer.getTokenText());
    }

    @Test
    public void canReadProperties(){
        final String properties =
                "    :PROPERTIES:\n" +
                "       :TEST: foo\n" +
                "    :END:";

        lexer.start(properties);
        assertEquals("Properties block start not properly parsed", OrgTokenTypes.DRAWER_DELIMITER, lexer.getTokenType());

        lexer.advance();
        eatWhitespace();
        assertEquals("Properties block content not properly parsed", OrgTokenTypes.DRAWER_CONTENT, lexer.getTokenType());

        eatPropertiesContent();
        eatWhitespace();
        assertEquals("Properties block end not properly parsed", OrgTokenTypes.DRAWER_DELIMITER, lexer.getTokenType());
    }

    private void eatWhitespace() {
        eatUntil(OrgTokenTypes.WHITE_SPACE);
    }

    /**
     * Eats all characters inside a block
     */
    private void eatBlockContent() {
        eatUntil(OrgTokenTypes.BLOCK_CONTENT);
    }

    private void eatPropertiesContent(){
        eatUntil(OrgTokenTypes.DRAWER_CONTENT);
    }

    private void eatUntil(final IElementType... stop) {
        Set<IElementType> ignores = new HashSet<IElementType>(stop.length);
        ignores.addAll(Arrays.asList(stop));
        while(ignores.contains(lexer.getTokenType())) {
            lexer.advance();
        }
    }
}
