package com.mpiotrowski.maudiofasttrackmixer.model

data class MasterChannel (var volume : Int = 0,
                          var fxReturn : Int = 0,
                          var mute: Boolean = false)