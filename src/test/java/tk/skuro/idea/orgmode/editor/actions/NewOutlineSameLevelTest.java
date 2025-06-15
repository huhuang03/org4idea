package tk.skuro.idea.orgmode.editor.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.After;
import org.junit.Before;

public class NewOutlineSameLevelTest extends LightJavaCodeInsightFixtureTestCase {

    private NewOutlineSameLevel action = new NewOutlineSameLevel();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNewOutlineInEmptyFile() {
        addOutlineAndAssertCaret("", "* ".length());
    }

    public void testNewOutlineInFileWithNoOutlines() {
        final String orgText =
            "#+BEGIN_SRC\n" +
                "I'm code!\n" +
                "#+END_SRC";
        addOutlineAndAssertCaret(orgText, orgText.length() + "\n* ".length());
    }

    public void testNewOutlineFromBeginningOfOutline() {
        final String orgText =
            "* I'm an outline\n" +
                "I'm the body of the outline";
        addOutlineAndAssertCaret(orgText, orgText.length() + "\n* ".length());
    }

    public void testNewOutlineFromOutlineBody() {
        final String orgText =
            "* I'm an outline\n" +
                "I'm the body of the outline";
        moveCaretAddOutlineAndAssertCaret(orgText.length() - 10, orgText, orgText.length() + "\n* ".length());
    }

    public void testNewOutlineFromInnerOutlineBody() {
        final String orgText =
            "* I'm an outer outline\n" +
                "I'm the body of the outline\n" +
                "** I'm the inner outline\n" +
                "And here's some text";
        moveCaretAddOutlineAndAssertCaret(orgText.length() - 10, orgText, orgText.length() + "\n** ".length());
    }

    public void testNewOutlineFromInnerOutlineBodyWithFollowingOutline() {
        final String orgTextBeforeCaret =
            "* I'm an outer outline\n" +
                "I'm the body of the outline\n" +
                "** I'm the inner outline\n" +
                "And here's ";
        final String orgTextAfterCaret = "some text\n" +
            "* Now another first level outline\n" +
            "Then also some other text";
        final String orgText = orgTextBeforeCaret + orgTextAfterCaret;
        final String expectedOrgTextBeforeCaret = orgTextBeforeCaret +
            "some text\n" +
            "** ";
        moveCaretAddOutlineAndAssertCaret(orgTextBeforeCaret.length(), orgText, expectedOrgTextBeforeCaret.length());
    }

    public void testNewOutlineWhenFirstOutlineAppearsAfterwards() {
        final String textBeforeCaret = "No outlines at this point\n";
        final String textAfterCaret = "then some text\n" +
            "**** And here's an outline\n" +
            "with some other body";
        final String orgText = textBeforeCaret + textAfterCaret;
        final String expectedOrgTextBeforeCaret = textBeforeCaret +
            "then some text\n" +
            "* ";
        moveCaretAddOutlineAndAssertCaret(textBeforeCaret.length(), orgText, expectedOrgTextBeforeCaret.length());

    }

    private void addOutlineAndAssertCaret(final String text, final int position) {
        moveCaretAddOutlineAndAssertCaret(0, text, position);
    }

    private void moveCaretAddOutlineAndAssertCaret(final int moveTo, final String text, final int position) {
        final PsiFile org = myFixture.configureByText("test.org", text);
        final Editor editor = myFixture.getEditor();
        final DataContext ctx = buildDataContext(editor, org);

        if (moveTo > 0) {
            moveCaret(editor, moveTo);
        }

        AnActionEvent event = TestActionEvent.createTestEvent(action, ctx);
        action.actionPerformed(event);
        assertCaretPosition("The caret was not placed after the newly created outline", editor, position);
    }

    private void moveCaret(Editor editor, int moveTo) {
        editor.getCaretModel().getCurrentCaret().moveToOffset(moveTo);
    }

    private void assertCaretPosition(String errMessage, Editor editor, int expected) {
        int currentPosition = editor.getCaretModel().getCurrentCaret().getOffset();
        assertEquals(errMessage, expected, currentPosition);
    }

    private DataContext buildDataContext(Editor editor, PsiFile file) {
        return SimpleDataContext.builder()
            .add(CommonDataKeys.EDITOR, editor)
            .add(CommonDataKeys.PSI_FILE, file)
            .build();
    }
}