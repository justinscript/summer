package com.ms.commons.weixin.bean;

import java.util.List;

/**
 * <pre>
 * {
 *    "errcode": 0,
 *    "errmsg": "ok",
 *    "department": [
 *        {
 *            "id": 2,
 *            "name": "广州研发中心",
 *            "parentid": 1
 *        },
 *        {
 *            "id": 3
 *            "name": "邮箱产品部",
 *            "parentid": 2
 *        }
 *    ]
 * }
 * </pre>
 */
public class QYWeixinDepartment extends WeixinResult {

    private List<Department> department;

    /**
     * @return the department
     */
    public List<Department> getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(List<Department> department) {
        this.department = department;
    }

    public String toString() {
        return "QYWeixinDepartment [department=" + department + "]";
    }
}
