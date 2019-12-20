/**
 * @filename: JsonResult.java 2019-09-23
 * @project power-base  V1.0
 * Copyright(c) 2018 BianPeng Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.exchange.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Copyright: Copyright (c) 2019 
 *
 * <p>说明： 用户服务层</P>
 * @version: V1.0
 * @author: flying-cattle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1071681926787951549L;

    private static Integer CODE_SUCCESS = 200;
    private static Integer CODE_FAILED = 501;
    /**
     *<p>状态码</p>
     */
    private Integer code;
    /**
     *<p> 状态说明</p>
     */
    private String msg;
    /**
     * <p>返回数据</p>
     */
    private T data;

    /**
     * <p>返回成功,有数据</p>
     * @param message 操作说明
     * @param data 对象
     * @return JsonResult
     */
    public JsonResult<T> success(String message, T data) {
        this.setCode(CODE_SUCCESS);
        this.setMsg(message);
        this.setData(data);
        return this;
    }

    /**
     * <p>返回成功,有数据</p>
     * @param data 对象
     * @return JsonResult
     */
    public JsonResult<T> success(T data) {
        this.setCode(CODE_SUCCESS);
        this.setMsg("操作成功");
        this.setData(data);
        return this;
    }

    /**
     * <p>返回成功,无数据</p>
     * @param message 操作说明
     * @return JsonResult
     */
    public JsonResult<T> success(String message) {
        this.setCode(CODE_SUCCESS);
        this.setMsg(message);
        this.setData(null);
        return this;

    }

    /**
     * <p>返回失败,无数据</p>
     * @param message 消息
     * @return JsonResult
     */
    public JsonResult<T> error(String message) {
        this.setCode(CODE_FAILED);
        this.setMsg(message);
        this.setData(null);
        return this;
    }

    /**
     * <p>返回失败,有数据</p>
     * @param message 消息
     * @param data 对象
     * @return JsonResult
     */
    public JsonResult<T> error(String message, T data) {
        this.setCode(CODE_FAILED);
        this.setMsg(message);
        this.setData(data);
        return this;
    }

    /**
     * <p>返回失败,无数据</p>
     * @param BindingResult
     * @return JsonResult
     */
    public JsonResult<T> error(BindingResult result, MessageSource messageSource) {
        StringBuffer msg = new StringBuffer();
        // 获取错位字段集合
        List<FieldError> fieldErrors = result.getFieldErrors();
        // 获取本地locale,zh_CN
        Locale currentLocale = LocaleContextHolder.getLocale();
        for (FieldError fieldError : fieldErrors) {
            // 获取错误信息
            String errorMessage = messageSource.getMessage(fieldError, currentLocale);
            // 添加到错误消息集合内
            msg.append(fieldError.getField() + "：" + errorMessage + " ");
        }
        this.setCode(CODE_FAILED);
        this.setMsg(msg.toString());
        this.setData(null);
        return this;
    }

    public JsonResult(Throwable throwable) {
        this.code = CODE_FAILED;
        if (throwable instanceof NullPointerException) {
            this.code = 1001;
            this.msg = "空指针：" + throwable;
        } else if (throwable instanceof ClassCastException) {
            this.code = 1002;
            this.msg = "类型强制转换异常：" + throwable;
        } else if (throwable instanceof ConnectException) {
            this.code = 1003;
            this.msg = "链接失败：" + throwable;
        } else if (throwable instanceof IllegalArgumentException) {
            this.code = 1004;
            this.msg = "传递非法参数异常：" + throwable;
        } else if (throwable instanceof NumberFormatException) {
            this.code = 1005;
            this.msg = "数字格式异常：" + throwable;
        } else if (throwable instanceof IndexOutOfBoundsException) {
            this.code = 1006;
            this.msg = "下标越界异常：" + throwable;
        } else if (throwable instanceof SecurityException) {
            this.code = 1007;
            this.msg = "安全异常：" + throwable;
        } else if (throwable instanceof SQLException) {
            this.code = 1008;
            this.msg = "数据库异常：" + throwable;
        } else if (throwable instanceof ArithmeticException) {
            this.code = 1009;
            this.msg = "算术运算异常：" + throwable;
        } else if (throwable instanceof RuntimeException) {
            this.code = 1010;
            this.msg = "运行时异常：" + throwable;
        } else if (throwable instanceof Exception) {
            this.code = 9999;
            this.msg = "未知异常" + throwable;
        }
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}

