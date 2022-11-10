package io.github.giovannilamarmora.utils.excelObjectMpper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * A simple Excel to Object mapper utility using Apache POI. This class provides utility methods, to
 * read an Excel file and convert each rows of the Excel file into appropriate model object as
 * specified and return all rows of Excel file as list of given object type.
 *
 * @author Giovanni Lamarmora.
 */
@Logged
@Service
public class ExcelToObjectMapper {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  private Workbook workbook;

  /*public ExcelToObjectMapper(String fileUrl) throws IOException, ExcelException {
    LOG.info("Creating workbook for filepath {}", fileUrl);
    try {
      workbook = createWorkBook(fileUrl);
    } catch (InvalidFormatException e) {
      LOG.error("Error on creating workbook for filepath {}", fileUrl);
      throw new ExcelException(ExcelException.Code.UNABLE_TO_READ_THE_FILE, e.getMessage());
    }
  }*/

  public ExcelToObjectMapper() {}

  /**
   * Use this if you don't need validation
   *
   * @param filePath url file
   * @throws ExcelException
   * @throws IOException
   */
  public ExcelToObjectMapper(String filePath) throws UtilsException, IOException {
    createWorkbook(filePath);
  }

  /**
   * Method that create the workbook, call this first
   *
   * @param fileUrl filepath of workbook
   * @throws IOException
   * @throws ExcelException
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public void createWorkbook(String fileUrl) throws IOException, UtilsException {
    LOG.info("Creating workbook for filepath {}", fileUrl);
    try {
      workbook = createWorkBook(fileUrl);
    } catch (InvalidFormatException e) {
      LOG.error("Error on creating workbook for filepath {}", fileUrl);
      throw new UtilsException(ExcelException.ERREXCUTL001, e.getMessage());
    }
  }
  /**
   * Read data from Excel file and convert each rows into list of given object of Type T.
   *
   * @param cls Class of Type T.
   * @param <T> Generic type T, result will list of type T objects.
   * @return List of object of type T.
   * @throws Exception if failed to generate mapping.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public <T> ArrayList<T> mapClass(Class<T> cls, Integer sheetIndex, Integer rowIndex)
      throws Exception {
    LOG.info(
        "Mapping {} for sheet n {} with header row at {}", cls.getName(), sheetIndex, rowIndex);
    ArrayList<T> list = new ArrayList();

    Sheet sheet = workbook.getSheetAt(sheetIndex);
    int lastRow = sheet.getLastRowNum();
    for (int i = rowIndex + 1; i <= lastRow; i++) {
      Object obj = cls.newInstance();
      Field[] fields = obj.getClass().getDeclaredFields();
      for (Field field : fields) {
        String fieldName = field.getName();
        int index = getHeaderIndex(fieldName, workbook, sheetIndex, rowIndex);
        Cell cell = sheet.getRow(i).getCell(index);
        Field classField = obj.getClass().getDeclaredField(fieldName);
        setObjectFieldValueFromCell(obj, classField, cell);
      }
      list.add((T) obj);
    }
    /** Validation Method (Read README.md) */
    validate(list);
    return list;
  }

  /**
   * Read value from Cell and set it to given field of given object. Note: supported data Type:
   * String, LocalDate, Integer, Long, Float, Double, BigDecimal and Boolean.
   *
   * @param obj Object whom given field belong.
   * @param field Field which value need to be set.
   * @param cell Apache POI cell from which value needs to be retried.
   */
  private void setObjectFieldValueFromCell(Object obj, Field field, Cell cell)
      throws UtilsException {
    Class<?> cls = field.getType();
    field.setAccessible(true);
    if (cell == null || cell.getCellType() == CellType.BLANK) {
      try {
        field.set(obj, null);
      } catch (IllegalAccessException e) {
        LOG.error("Error on setting blank cell for field {}", field.getName());
        throw new UtilsException(ExcelException.ERREXCUTL002, e.getMessage());
      }
    } else if (cls == String.class) {
      try {
        field.set(obj, cell.getStringCellValue());
      } catch (Exception e) {
        LOG.error(
            "Error on getting string cell for field {} with message {}",
            field.getName(),
            e.getMessage());
        try {
          field.set(obj, null);
        } catch (IllegalAccessException e1) {
          LOG.error("Error on setting null cell for field {}", field.getName());
          throw new UtilsException(ExcelException.ERREXCUTL002, e1.getMessage());
        }
      }
    } else if (cls == LocalDate.class || cls == LocalDateTime.class) {
      try {
        LocalDateTime value = cell.getLocalDateTimeCellValue();
        if (cls == LocalDate.class) {
          field.set(obj, value.toLocalDate());
        } else {
          field.set(obj, value);
        }
      } catch (Exception e) {
        LOG.error(
            "Error on getting Date cell for field {} with message {}",
            field.getName(),
            e.getMessage());
        try {
          field.set(obj, null);
        } catch (IllegalAccessException e1) {
          LOG.error("Error on setting null cell for field {}", field.getName());
          throw new UtilsException(ExcelException.ERREXCUTL002, e1.getMessage());
        }
      }
    } else if (cls == Integer.class
        || cls == Long.class
        || cls == Float.class
        || cls == Double.class
        || cls == BigDecimal.class) {
      try {
        double value = cell.getNumericCellValue();
        // Check Percentage Value
        if (cell.getCellStyle().getDataFormatString().contains("%")) {
          value *= 100;
        }
        if (cls == Integer.class) {
          field.set(obj, (int) value);
        } else if (cls == Long.class) {
          field.set(obj, (long) value);
        } else if (cls == Float.class) {
          field.set(obj, (float) value);
        } else if (cls == BigDecimal.class) {
          field.set(obj, new BigDecimal(value));
        } else {
          // Double value
          field.set(obj, value);
        }
      } catch (Exception e) {
        LOG.error(
            "Error on getting number cell for field {} with message {}",
            field.getName(),
            e.getMessage());
        try {
          field.set(obj, null);
        } catch (IllegalAccessException e1) {
          LOG.error("Error on setting null cell for field {}", field.getName());
          throw new UtilsException(ExcelException.ERREXCUTL002, e1.getMessage());
        }
      }
    } else if (cls == Boolean.class) {
      try {
        boolean value = cell.getBooleanCellValue();
        field.set(obj, value);
      } catch (Exception e) {
        LOG.warn(
            "Error on getting boolean cell for field {} with message {}",
            field.getName(),
            e.getMessage());
        try {
          LOG.info("Trying to parsing field {} into boolean", field.getName());
          String valueParse = cell.getStringCellValue();
          if (valueParse.equalsIgnoreCase("True") || valueParse.equalsIgnoreCase("Yes")) {
            field.set(obj, true);
          } else {
            field.set(obj, false);
          }
        } catch (Exception e2) {
          try {
            field.set(obj, null);
          } catch (IllegalAccessException e1) {
            LOG.error("Error on setting null cell for field {}", field.getName());
            throw new UtilsException(ExcelException.ERREXCUTL002, e1.getMessage());
          }
        }
      }
    } else {
      LOG.error("Unsupported Type {}", cell);
    }
  }

  /**
   * Create Apache POI @{@link Workbook} for given Excel file.
   *
   * @param file
   * @return
   * @throws IOException
   * @throws InvalidFormatException
   */
  private Workbook createWorkBook(String file) throws IOException, InvalidFormatException {
    InputStream inp = new FileInputStream(file);
    return WorkbookFactory.create(inp);
  }

  /**
   * Read first row/header of Excel file, match given header name and return its index.
   *
   * @param headerName class name
   * @param workbook excel workbook
   * @param sheetIndex sheet at index
   * @param rowIndex row at index
   * @return Current index header
   * @throws ExcelException to be handled
   */
  private int getHeaderIndex(
      String headerName, Workbook workbook, Integer sheetIndex, Integer rowIndex)
      throws UtilsException {
    String headerNameFixed = headerName.replace("_", " ");
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    int totalColumns = sheet.getRow(rowIndex).getLastCellNum();
    int index = -1;
    for (index = 0; index < totalColumns; index++) {
      Cell cell = sheet.getRow(rowIndex).getCell(index);
      String cellValue = cell.getStringCellValue().replace("% ", "").toLowerCase();
      if (cellValue.equalsIgnoreCase(headerNameFixed) || cellValue.contains(headerName)) {
        break;
      }
    }
    if (index == -1) {
      throw new UtilsException(ExcelException.ERREXCUTL003, "Invalid object field name provided.");
    }
    return index;
  }

  /**
   * Use this if you need validation, just extend the class and override this method
   *
   * @param excelData Set automatically
   * @param <T> Set automatically
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public <T> void validate(ArrayList<T> excelData) {}
}
