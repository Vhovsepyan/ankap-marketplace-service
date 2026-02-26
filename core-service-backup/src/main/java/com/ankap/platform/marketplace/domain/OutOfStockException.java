package com.ankap.platform.marketplace.domain;

public class OutOfStockException extends RuntimeException {
  public OutOfStockException(String msg) {
    super(msg);
  }
}