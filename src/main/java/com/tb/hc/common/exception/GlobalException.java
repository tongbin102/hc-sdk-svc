package com.tb.hc.common.exception;

import com.tb.hc.common.enums.ResCodeEnum;

/**
 * @author bin.tong
 * @since 2021/3/15 9:41
 **/
public class GlobalException extends RuntimeException {

  private ResCodeEnum resCodeEnum;

  public GlobalException(ResCodeEnum resCodeEnum) {
    super(resCodeEnum.getStatusName());
    this.resCodeEnum = resCodeEnum;
  }

  public ResCodeEnum getResCodeEnum() {
    return resCodeEnum;
  }
}
