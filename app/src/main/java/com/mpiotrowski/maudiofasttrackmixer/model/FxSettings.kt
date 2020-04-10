package com.mpiotrowski.maudiofasttrackmixer.model

data class FxSettings(var fxType: FxType = FxType.ROOM1, var duration: Int = 0, var feedback: Int = 0, var volume: Int = 0) {

     enum class FxType(apiCode: Int, name: String) {
        ROOM1(0x0000, "Room 1"),
        ROOM2(0x0100, "Room 2"),
        ROOM3(0x0200, "Room 3"),
        HALL1(0x0300, "HALL 1"),
        HALL2(0x0400, "HALL 2"),
        PLATE(0x0500, "PLATE"),
        DELAY(0x0600, "Delay"),
        ECHO(0x0700, "Echo")
    }
}