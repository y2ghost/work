package study.ywork.doc.util;

import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.splitting.ISplitCharacters;

public class PipeSplitCharacter implements ISplitCharacters {
    @Override
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        if (!text.get(glyphPos).hasValidUnicode()) {
            return false;
        }

        int charCode = text.get(glyphPos).getUnicode();
        return (charCode == '|' || charCode <= ' ' || charCode == '-');
    }
}
