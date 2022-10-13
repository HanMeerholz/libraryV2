package com.yer.library.model.attributeconverters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Year;

@Converter(autoApply = true)
public class YearAttributeConverter implements AttributeConverter<Year, Short> {

    @Override
    public Short convertToDatabaseColumn(Year entityAttribute) {
        if (entityAttribute != null) {
            return (short) entityAttribute.getValue();
        }
        return null;
    }

    @Override
    public Year convertToEntityAttribute(Short databaseColumn) {
        if (databaseColumn != null) {
            return Year.of(databaseColumn);
        }
        return null;
    }
}
