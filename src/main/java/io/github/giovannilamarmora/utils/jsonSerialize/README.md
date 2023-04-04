# JSON Serialize Annotation

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

<hr>

## How to use it

### LowerCase

```
@LowerCase private String username
```

Request: "TextField"
Result: "textfield"

### UpperCase

```
@UpperCase private String username
```

Request: "TextField"
Result: "TEXTFIELD"

### UpperCamelCase

```
@UpperCamelCase private String username
```

Request: "textField"
Result: "Textfield"