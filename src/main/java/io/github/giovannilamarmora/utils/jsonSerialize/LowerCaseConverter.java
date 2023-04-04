package io.github.giovannilamarmora.utils.jsonSerialize;

import com.fasterxml.jackson.databind.util.StdConverter;

public class LowerCaseConverter extends StdConverter<String, String> {
    @Override
    public String convert(String value) {
        if (value == null){
            return null;
        }
        return value.toLowerCase();
    }
}