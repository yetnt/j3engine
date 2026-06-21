# Markdown File Standard

This is just the standard i will be using whilst parsing and writing
markdown documentation files which become the help menu. (via F1 menu)

# Assumptions

1. Follow normal markdown new line rules. If there is a single new line,
    it gets appended to the previous line, otherwise its a line break.
2. No tables, formatted number or bullet lists. They will be formatted as normal 
strings.

# Styling

`#` for H1 header (only document titles can be H1)

`##` for H2 header (only sub headings in a document can be H2)

`###` for H3 header (etc etc. No more # tag beyond this.)

`**` to **bold** text

`_` to _underline_ text

`(backtick)` for `inline code` 

`---` for a line separator

`<image>` HTML tag for an image

`[me](link)` to link to another doc

(any custom formatting rules that need be applied to a line, use a code
 block with `j3md-style` as the code (Applies to the entire line)) e.g.
---
yo it's me 
```j3md-style
col=#FFFE32#
text=cole!
```
 and i like pie

---

will be formatted as 
> yo it's me <font color="#FFFE32">cole!</font> and i like pie
