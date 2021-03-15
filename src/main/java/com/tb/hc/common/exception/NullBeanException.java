package com.tb.hc.common.exception;

/**
 * @author bin.tong
 * @since 2021/3/15 9:42
 **/
public class NullBeanException extends NullPointerException {

  private static final long serialVersionUID = -3419798163602428634L;

  public NullBeanException() {
    super();
  }

  public NullBeanException(String name) {
    super(name + ": 没有获取到Bean");
  }

  public <T> NullBeanException(Class<T> clazz) {
    super(clazz.getClass().getName() + ": 没有获取到Bean");
  }

}
