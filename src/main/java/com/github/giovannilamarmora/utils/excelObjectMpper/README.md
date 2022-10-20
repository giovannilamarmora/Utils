# Excel Mapper

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

You can use this mapper in different ways:

## 1. ExcelMapper without validation

You can `@Autowire` the class or just declare it into a method like this:

```
try {
    ExcelToObjectMapper mapper = new ExcelToObjectMapper(FILEPATH);
    classList = mapper.mapClass(ModelClass.class, 4, 1); // List of your model
} catch (IOException e) {
    throw new Exception();
} catch (Exception e) {
    throw new Exception();
}
```

## 2. ExcelMapper with validation

If you need validation of that Object you can extend the class, for example: <br>
`public class PoiImportExcelService extends ExcelToObjectMapper {}`

Then you can set up the filepath in:

### 2.1 Unique Filepath

```
@PostConstruct
void setUp() throws ExcelException, IOException {
    new ExcelToObjectMapper(FILEPATH);
}
```

Then

```
classList = mapper.mapClass(ModelClass.class, 4, 1); // List of your model
```

### 2.2 Different Filepath

```
createWorkbook(FILEPATH);
classList = mapper.mapClass(ModelClass.class, 4, 1); // List of your model
```

<hr>

## Validation

```
@Override
public <T> void validate(ArrayList<T> excelData) {
    if (excelData.get(0).getClass() == ModelClass.class) {
        // Your validation goes here
    }
}
```