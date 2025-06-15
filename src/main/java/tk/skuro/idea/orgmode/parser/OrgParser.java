// This is a generated file. Not intended for manual editing.
package tk.skuro.idea.orgmode.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static tk.skuro.idea.orgmode.parser.OrgTokenTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class OrgParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return orgFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // BLOCK_START BLOCK_CONTENT* BLOCK_END
  public static boolean block(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "block")) return false;
    if (!nextTokenIs(builder_, BLOCK_START)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BLOCK_START);
    result_ = result_ && block_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BLOCK_END);
    exit_section_(builder_, marker_, BLOCK, result_);
    return result_;
  }

  // BLOCK_CONTENT*
  private static boolean block_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "block_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, BLOCK_CONTENT)) break;
      if (!empty_element_parsed_guard_(builder_, "block_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // DRAWER_DELIMITER DRAWER_CONTENT* DRAWER_DELIMITER
  public static boolean drawer(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drawer")) return false;
    if (!nextTokenIs(builder_, DRAWER_DELIMITER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DRAWER_DELIMITER);
    result_ = result_ && drawer_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DRAWER_DELIMITER);
    exit_section_(builder_, marker_, DRAWER, result_);
    return result_;
  }

  // DRAWER_CONTENT*
  private static boolean drawer_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drawer_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, DRAWER_CONTENT)) break;
      if (!empty_element_parsed_guard_(builder_, "drawer_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMENT|KEYWORD|CODE|PROPERTIES|WHITE_SPACE|UNMATCHED_DELIMITER|outlineBlock|block|drawer|text_element|verbatim_element
  static boolean item_(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item_")) return false;
    boolean result_;
    result_ = consumeToken(builder_, COMMENT);
    if (!result_) result_ = consumeToken(builder_, KEYWORD);
    if (!result_) result_ = consumeToken(builder_, CODE);
    if (!result_) result_ = consumeToken(builder_, PROPERTIES);
    if (!result_) result_ = consumeToken(builder_, WHITE_SPACE);
    if (!result_) result_ = consumeToken(builder_, UNMATCHED_DELIMITER);
    if (!result_) result_ = outlineBlock(builder_, level_ + 1);
    if (!result_) result_ = block(builder_, level_ + 1);
    if (!result_) result_ = drawer(builder_, level_ + 1);
    if (!result_) result_ = text_element(builder_, level_ + 1);
    if (!result_) result_ = verbatim_element(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // item_*
  static boolean orgFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orgFile")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!item_(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "orgFile", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // OUTLINE
  public static boolean outlineBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "outlineBlock")) return false;
    if (!nextTokenIs(builder_, OUTLINE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OUTLINE);
    exit_section_(builder_, marker_, OUTLINE_BLOCK, result_);
    return result_;
  }

  /* ********************************************************** */
  // TEXT | UNDERLINE | BOLD | CRLF
  public static boolean text_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "text_element")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TEXT_ELEMENT, "<text element>");
    result_ = consumeToken(builder_, TEXT);
    if (!result_) result_ = consumeToken(builder_, UNDERLINE);
    if (!result_) result_ = consumeToken(builder_, BOLD);
    if (!result_) result_ = consumeToken(builder_, CRLF);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // VERBATIM
  public static boolean verbatim_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "verbatim_element")) return false;
    if (!nextTokenIs(builder_, VERBATIM)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, VERBATIM);
    exit_section_(builder_, marker_, VERBATIM_ELEMENT, result_);
    return result_;
  }

}
