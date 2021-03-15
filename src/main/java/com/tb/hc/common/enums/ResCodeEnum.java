package com.tb.hc.common.enums;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author bin.tong
 * @since 2021/3/15 9:36
 **/
public enum ResCodeEnum {

  /**
   * "200", "成功"
   */
  RESCODE_SUCCESS(HttpServletResponse.SC_OK, "成功"),

  /**
   * "400","请求返回错误"
   */
  RESCODE_BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "请求返回错误"),

  /**
   * "401","无相关权限"
   */
  RESCODE_UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "权限校验未通过"),

  /**
   * "403","无相关权限"
   */
  RESCODE_FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "无相关权限"),

  /**
   * "404","无相关资源"
   */
  RESCODE_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "无相关资源");

  private Integer statusCode;
  private String statusName;

  ResCodeEnum(Integer statusCode, String statusName) {
    this.statusCode = statusCode;
    this.statusName = statusName;
  }

  /**
   * 成功
   *
   * @return
   */
  public static Integer OK = ResCodeEnum.RESCODE_SUCCESS.getStatusCode();
  public static String OK_NAME = ResCodeEnum.RESCODE_SUCCESS.getStatusName();
  /**
   * 失败
   */
  public static Integer FAIL = ResCodeEnum.RESCODE_BAD_REQUEST.getStatusCode();
  public static String FAIL_NAME = ResCodeEnum.RESCODE_BAD_REQUEST.getStatusName();


  /**
   * 通过statusCode获取statusName
   *
   * @param statusCode
   * @return
   */
  public static String getResCodeEnum(Integer statusCode) {
    ResCodeEnum[] values = ResCodeEnum.values();
    for (ResCodeEnum value : values) {
      if (Objects.equals(value.statusCode, statusCode)) {
        return value.statusName;
      }
    }
    return null;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

}
