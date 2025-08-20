package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class BuildingNotFoundException extends RuntimeException {
  private final ErrorCode errorCode = ErrorCode.BUILDING_NOT_FOUND;

  public BuildingNotFoundException(String message) {
        super(message);
  }

  public BuildingNotFoundException(Long buildingId) {
    super("Building not found with id: " + buildingId);
  }
}
