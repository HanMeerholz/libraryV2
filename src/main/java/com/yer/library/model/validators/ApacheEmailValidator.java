package com.yer.library.model.validators;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check a string if it's a valid eMail using {@see org.apache.commons.validator.routines.EmailValidator}
 * You need commons-validator-1.4 or later as a dependency.
 *
 * @author Robert Oschwald
 */
public class ApacheEmailValidator implements ConstraintValidator<ApacheEmailConstraint, String> {

    /**
     * Initialize the validator in preparation for isValid calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p/>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     *
     * @param annotation annotation instance for a given constraint declaration
     */
    public void initialize(ApacheEmailConstraint annotation) {
    }

    /**
     * {@inheritDoc} check if given string is a valid mail.
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        return EmailValidator.getInstance().isValid(value);
    }
}