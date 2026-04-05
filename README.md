You can generate your own MSDF atlases using msdf-atlas-gen: https://github.com/Chlumsky/msdf-atlas-gen Make sure your charset starts at 0x20, so it aligns with Java's char implementation

If you don’t want to generate anything yourself, the mod already includes a few prebaked fonts.You can access them through McsdfFonts.java.

To render text, create an McsdfFontRenderer and call its methods:

```java
McsdfFontRenderer fr = new McsdfFontRenderer();

// Example:
fr.renderString(graphics, McsdfFonts.ARIAL, "Hello", 20, 20);
fr.renderStringScaled(graphics, McsdfFonts.ARIAL, "Scaled text", 40, 60, 300);
fr.drawCenteredString(graphics, McsdfFonts.ARIAL, "Centered", width / 2f, height / 2f);
```

MSDF allow for rendering of text at all scales