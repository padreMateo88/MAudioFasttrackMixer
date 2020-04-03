package com.mpiotrowski.maudiofasttrackmixer.model

data class AudioChannel(var volume : Int = 0,
                        var channelId : Int = 0,
                        var panorama: Int = 0,
                        var mute: Boolean = false,
                        var solo: Boolean = false)