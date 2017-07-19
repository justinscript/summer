/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.mvc;

import java.util.Date;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 4:07:53 PM
 */
public class AbstractControllerTest extends TestCase {

    public void testcopyBean() {
        UserTest ut = new UserTest();
        ut.setId(100);
        ut.setName("abc");
        ut.setAddress("ggg");
        PersonTest copyBean = (PersonTest) AbstractController.copyBean(ut, PersonTest.class);
        assertEquals(copyBean.getId().intValue(), 100);
        assertEquals(copyBean.getName(), "abc");
        assertEquals(copyBean.getMyaddress(), null);
    }

    public static class UserTest {

        private Integer id;
        private String  name;
        private String  address;
        private Date    createDate;

        /**
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the createDate
         */
        public Date getCreateDate() {
            return createDate;
        }

        /**
         * @param createDate the createDate to set
         */
        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        /**
         * @return the address
         */
        public String getAddress() {
            return address;
        }

        /**
         * @param address the address to set
         */
        public void setAddress(String address) {
            this.address = address;
        }

    }

    public static class PersonTest {

        private Integer id;
        private String  name;
        private String  myaddress;
        private Date    createDate;

        /**
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the createDate
         */
        public Date getCreateDate() {
            return createDate;
        }

        /**
         * @param createDate the createDate to set
         */
        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        /**
         * @return the myaddress
         */
        public String getMyaddress() {
            return myaddress;
        }

        /**
         * @param myaddress the myaddress to set
         */
        public void setMyaddress(String myaddress) {
            this.myaddress = myaddress;
        }

    }
}
