/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import com.ms.commons.lang.RangeBuilder.Range;

/**
 * @author zxc Apr 12, 2013 2:59:41 PM
 */
public class RangeBuilderTest {

    @Test
    public void testPrimitive() {
        List<Person> data = new ArrayList<Person>();
        for (int i = 0; i < 5; i++) {
            Random r = new Random(System.nanoTime());
            Person p = new Person();
            p.setSalary(r.nextFloat());
            p.setAge((r.nextInt(10)));
            p.setId(r.nextLong());
            p.setSex(r.nextInt(10));
            p.setName("name" + r.nextInt(10));
            data.add(p);
        }
        println(data);
        Range range = RangeBuilder.data(data).property("age").range();
        println(range);
        range = RangeBuilder.data(data).property("name").keyName("newName").range();
        println(range);
        range = RangeBuilder.data(data).property("salary").desc().range();
        println(range);
        range = RangeBuilder.data(data).property("id").asc().range();
        println(range);
    }

    @SuppressWarnings("rawtypes")
    private void println(Object data) {
        if (data instanceof Collection) {
            for (Object obj : (Collection) data) {
                System.out.println(ToStringBuilder.reflectionToString(obj, ToStringStyle.SHORT_PREFIX_STYLE));
            }
        } else {
            System.out.println(ToStringBuilder.reflectionToString(data, ToStringStyle.SHORT_PREFIX_STYLE));
        }
    }

    public class Person {

        private int     age;
        private Integer sex;
        private Long    id;
        private Float   salary;
        private String  name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Float getSalary() {
            return salary;
        }

        public void setSalary(Float salary) {
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
