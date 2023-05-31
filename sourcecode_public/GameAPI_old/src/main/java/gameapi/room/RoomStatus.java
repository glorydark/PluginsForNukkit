package gameapi.room;

/**
 * @author Glorydark
 */
public enum RoomStatus {
    ROOM_STATUS_WAIT,
    ROOM_STATUS_PreStart,
    ROOM_STATUS_GameReadyStart,
    ROOM_STATUS_GameStart,
    ROOM_STATUS_GameEnd,
    ROOM_STATUS_Ceremony,
    ROOM_STATUS_NextRoundPreStart,
    ROOM_STATUS_End,
    ROOM_MapInitializing,
    ROOM_MapLoadFailed,
}
