# J3DMarkdown File Standard

(All j3Engine Markdown Documentation files must end in `.j3.md`)

This is just the standard i will be using whilst parsing and writing
markdown documentation files which become the help menu. (via F1 menu)

# Nuances

## Multiple Styling

Styling such as `inline code`, _italics_ or **bold** can only
be applied per word basis. Multiple words or tokens cannot all be
styled in one go. Example:

Invalid:
```md
**_styled [text](link) here_**
```

Valid:
```md
**_styled_** **_[text](link)_** **_here_**
```

### Link Styling

Links can only be styled as a whole and not partially within
the braces

Invalid:
```md
[_s_](https://google.com)
```

Valid:
```md
_[md](https://google.com)_
```

(The input is treated as pure plain text.)

## Paragraph Breaks

Paragraph breaks are explicitly marked with `---`. Meaning the following input
text is parsed as a single paragraph and not split:

```md
lorem ipsum

sit dolor amet
```

as

```md
lorem ipsum sit dolor amet
```

## HTML Tag placement

See [HTML Tags](#html-tags)

HTML Tags cannot be part of a line and need to be in it's own separate line.

(This pairs with the above rule as it will get inlined anyways
 if it isn't an image)

Invalid:
```md
hello this is a <tag >with stuff</tag>
```

Valid:
```md
hello this is a
<tag >with stuff</tag>
```

# Styling

`#` for H1 header (only document titles can be H1)

`##` for H2 header (only sub headings in a document can be H2)

`###` for H3 header (etc etc. No more # tag beyond this.)

`**` to **bold** text

`_` to _italicize_ text

`(backtick)` for `inline code` 

`---` for a line separator

`[me](link)` to link to another doc

## Codeblocks

All codeblocks work as normal but due to obvious limitations no code colouring is available.

````
```language
code here
preservers line indentation
and shows line numbers
```
````

Mostly all languages are useless, except for `cmd` and `command` which tell the reader that this code block
represents a single executable command which if the user double-clicks with the EngineFrame open, can
immediately insert said command ready to be edited or invoked.

````
```cmd
transform rotate points (0, 0, 1)
```
````

## HTML Tags

All HTML tags _can_ be parsed but must be accepted within 
[HTMLTags Enum](../../java/com/j3d/gen/docs/tokens/wrappers/HTMLTags.java)

and have a few caveats:

1. All tags without attributes must have a space before closing
it's first tag
> `<tag></tag>` is invalid
> 
> `<tag ></tag>` is valid

2. All tags without content must have a closing tag either way.
> `<img ... />` is invalid
> 
> `<img ... >` is invalid
> 
> `<img ... ></img>` is valid

All tag attributes are stored as a
`HashMap<String, String>`, type conversion
is up to u man.

Any attribute is accepted in any valid html tag.

### Accepted Tags

`<img alt="" src=""></img>` Image Tags

## Possible Future Additions

### Custom formatting rules

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
