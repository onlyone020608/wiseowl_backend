package com.hyewon.wiseowl_backend.domain.course.dto;

import com.hyewon.wiseowl_backend.domain.course.entity.Room;

public record RoomDto(Long buildingId, String roomNumber) {
    public static RoomDto from(Room room) {
        return new RoomDto(room.getBuilding().getId(), room.getRoomNumber());
    }
}
