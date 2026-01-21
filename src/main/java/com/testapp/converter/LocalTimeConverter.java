package com.testapp.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.LocalTime;

@FacesConverter(value = "localTimeConverter", managed = true)
public class LocalTimeConverter implements Converter<LocalTime> {

    @Override
    public LocalTime getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalTime.parse(value); // HH:mm
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalTime value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
