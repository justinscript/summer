/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation;

import com.ms.app.web.validation.annotation.ValidationInfo;

/**
 * @author zxc Apr 12, 2013 11:21:52 PM
 */
public class MockForm implements Form {

    @ValidationInfo(key = "email", displayName = "email", validators = { "test.required", "test.email.length",
            "test.email" })
    private String email;

    @ValidationInfo(key = "password", displayName = "password", validators = { "test.required", "test.password" })
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
