package com.yer.library.model.validators;

import com.yer.library.model.Location;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationValidator implements
        ConstraintValidator<ValidLocationConstraint, Location> {
    @Override
    public void initialize(ValidLocationConstraint contactNumber) {
    }

    @Override
    public boolean isValid(Location locationField,
                           ConstraintValidatorContext cxt) {
        if (locationField == null) return true;

        Short floor = locationField.getFloor();
        if (floor == null || floor < 0 || floor > Location.NR_OF_FLOORS) return false;
        Short bookcase = locationField.getBookcase();
        if (bookcase == null || bookcase < 1 || bookcase > Location.MAX_BOOKCASES) return false;
        Short shelve = locationField.getShelve();
        if (shelve == null || shelve < 1 || shelve > Location.MAX_SHELVES) return false;

        return true;
    }
}
