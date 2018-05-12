package com.lexleontiev.karmaauth.framework.vision

import android.graphics.Bitmap


data class ImagesHolder(val mOriginImage: Bitmap,
                        var mCroppedImage: Bitmap? = null,
                        var mCompleteImage: Bitmap? = null)
