/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.util.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.object.AbstractObjectMorpher;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author zxc Apr 12, 2013 4:24:55 PM
 */
public final class JsonNumberMorpher extends AbstractObjectMorpher {

    private Number   defaultValue;
    private Class<?> type;

    /**
     * Creates a new morpher for the target type.
     * 
     * @param type must be a primitive or wrapper type. BigDecimal and BigInteger are also supported.
     */
    public JsonNumberMorpher(Class<?> type) {
        super(false);

        if (type == null) {
            throw new MorphException("Must specify a type");
        }

        if (type != Byte.TYPE && type != Short.TYPE && type != Integer.TYPE && type != Long.TYPE && type != Float.TYPE
            && type != Double.TYPE && !Byte.class.isAssignableFrom(type) && !Short.class.isAssignableFrom(type)
            && !Integer.class.isAssignableFrom(type) && !Long.class.isAssignableFrom(type)
            && !Float.class.isAssignableFrom(type) && !Double.class.isAssignableFrom(type)
            && !BigInteger.class.isAssignableFrom(type) && !BigDecimal.class.isAssignableFrom(type)) {
            throw new MorphException("Must specify a Number subclass");
        }

        this.type = type;
    }

    /**
     * Creates a new morpher for the target type with a default value.<br>
     * The defaultValue should be of the same class as the target type.
     * 
     * @param type must be a primitive or wrapper type. BigDecimal and BigInteger are also supported.
     * @param defaultValue return value if the value to be morphed is null
     */
    public JsonNumberMorpher(Class<?> type, Number defaultValue) {
        super(true);

        if (type == null) {
            throw new MorphException("Must specify a type");
        }

        if (type != Byte.TYPE && type != Short.TYPE && type != Integer.TYPE && type != Long.TYPE && type != Float.TYPE
            && type != Double.TYPE && !Byte.class.isAssignableFrom(type) && !Short.class.isAssignableFrom(type)
            && !Integer.class.isAssignableFrom(type) && !Long.class.isAssignableFrom(type)
            && !Float.class.isAssignableFrom(type) && !Double.class.isAssignableFrom(type)
            && !BigInteger.class.isAssignableFrom(type) && !BigDecimal.class.isAssignableFrom(type)) {
            throw new MorphException("Must specify a Number subclass");
        }

        this.type = type;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof JsonNumberMorpher)) {
            return false;
        }

        JsonNumberMorpher other = (JsonNumberMorpher) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(type, other.type);
        if (isUseDefault() && other.isUseDefault()) {
            builder.append(getDefaultValue(), other.getDefaultValue());
            return builder.isEquals();
        } else if (!isUseDefault() && !other.isUseDefault()) {
            return builder.isEquals();
        } else {
            return false;
        }
    }

    /**
     * Returns the default value for this Morpher.
     */
    public Number getDefaultValue() {
        return defaultValue;
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(type);
        if (isUseDefault()) {
            builder.append(getDefaultValue());
        }
        return builder.toHashCode();
    }

    public Object morph(Object value) {
        if (value != null && type.isAssignableFrom(value.getClass())) {
            // no conversion needed
            return value;
        }

        String str = String.valueOf(value).trim();

        if (!type.isPrimitive() && (value == null || str.length() == 0 || "null".equalsIgnoreCase(str))) {
            // if empty string and class != primitive treat it like null
            return null;
        }

        try {
            if (isDecimalNumber(type)) {
                if (Float.class.isAssignableFrom(type) || Float.TYPE == type) {
                    return morphToFloat(str);
                } else if (Double.class.isAssignableFrom(type) || Double.TYPE == type) {
                    return morphToDouble(str);
                } else {
                    return morphToBigDecimal(str);
                }
            } else {
                if (Byte.class.isAssignableFrom(type) || Byte.TYPE == type) {
                    return morphToByte(str);
                } else if (Short.class.isAssignableFrom(type) || Short.TYPE == type) {
                    return morphToShort(str);
                } else if (Integer.class.isAssignableFrom(type) || Integer.TYPE == type) {
                    return morphToInteger(str);
                } else if (Long.class.isAssignableFrom(type) || Long.TYPE == type) {
                    return morphToLong(str);
                } else {
                    return morphToBigInteger(str);
                }
            }
        } catch (ConvertErrorException e) {
            // JsonPropertyConvertContext.setConvertError((ConvertErrorException) e);
            return null;
        }
    }

    public Class<?> morphsTo() {
        return type;
    }

    private boolean isDecimalNumber(Class<?> type) {
        return (Double.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || Double.TYPE == type
                || Float.TYPE == type || BigDecimal.class.isAssignableFrom(type));
    }

    private Object morphToBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToBigInteger(String str) {
        try {
            return new BigInteger(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToByte(String str) {
        try {
            return new Byte(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToDouble(String str) {
        try {
            return new Double(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToFloat(String str) {
        try {
            return new Float(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToInteger(String str) {
        try {
            return new Integer(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToLong(String str) {
        try {
            return new Long(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }

    private Object morphToShort(String str) {
        try {
            return new Short(str);
        } catch (Exception e) {
            throw new ConvertErrorException(str, this.type);
        }
    }
}
