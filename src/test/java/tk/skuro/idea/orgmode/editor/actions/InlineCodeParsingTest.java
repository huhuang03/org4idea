package tk.skuro.idea.orgmode.editor.actions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;

/**
 * @author wenfan.hu
 */
public class InlineCodeParsingTest extends BasePlatformTestCase {

    @Test
    public void testVerbatimTokenIsParsedCorrectly() {
        testHasVerbatimToken("~inline code~");
        testHasVerbatimToken("aa ~inline code~ aa");
    }

    private void testHasVerbatimToken(String str) {
        PsiFile file = myFixture.configureByText("test.org", str);

        StringBuilder result = new StringBuilder();

        file.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@org.jetbrains.annotations.NotNull PsiElement element) {
                IElementType type = element.getNode().getElementType();
                String content = element.getText();

                result.append("[").append(type).append(": ").append(content).append("]\n");

                super.visitElement(element);
            }
        });

        // 打印调试
        System.out.println(result);

        // 断言包含 VERBATIM
        assertTrue(result.toString().contains("VERBATIM: ~inline code~"));
    }
}
