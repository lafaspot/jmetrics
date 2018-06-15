/*
 * Copyright [yyyy] [name of copyright owner]
 * 
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  ====================================================================
 */
package com.lafaspot.jmetrics.common;

/**
 * Exception for illegal values.
 *
 */
public class TypeFormatException extends IllegalArgumentException {

    private static final long serialVersionUID = 7005469064820675831L;
    /**
     * Constructor.
     * @param message for the exception
     * @param cause of the exception
     */
    public TypeFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructor.
     * @param message for the exception.
     */
    public TypeFormatException(final String message) {
        super(message);
    }

    private String keyName = null;

    /**
     * Set the config key name.
     *
     * @param keyName Config key name
     */
    public void setKeyName(final String keyName) {
        this.keyName = keyName;
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
    	final StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append("Invalid key '");
    	stringBuilder.append(keyName);
    	stringBuilder.append("' value. ");
    	stringBuilder.append(super.getMessage());
    	return stringBuilder.toString();
    }
}