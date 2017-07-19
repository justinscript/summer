package com.ms.commons.weixin.bean;

/**
 * <pre>
 * {
 *     "id": 2,
 *     "name": "广州研发中心",
 *     "parentid": 1
 * }
 * </pre>
 */
public class Department {

    private Integer id;
    private String  name;
    private Integer parentid;

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
     * @return the parentid
     */
    public Integer getParentid() {
        return parentid;
    }

    /**
     * @param parentid the parentid to set
     */
    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public String toString() {
        return "Department [id=" + id + ", name=" + name + ", parentid=" + parentid + "]";
    }
}
