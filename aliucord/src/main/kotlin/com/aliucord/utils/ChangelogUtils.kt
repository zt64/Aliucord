package com.aliucord.utils

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import com.aliucord.Utils
import com.discord.widgets.changelog.WidgetChangeLog

public object ChangelogUtils {
    /**
     * Show ChangeLog modal
     *
     * @param context Context
     * @param version Version shown in the header
     * @param media Url to an image or a video that is displayed above body, null for nothing
     * @param body Changelog text in markdown (with custom discord rules)
     * @param footerActions Icons shown in the footer
     */
    @JvmStatic
    public fun show(
        context: Context,
        version: String,
        media: String?,
        body: String,
        vararg footerActions: FooterAction
    ) {
        val bundle = bundleOf(
            "INTENT_EXTRA_VERSION" to version,
            "INTENT_EXTRA_REVISION" to "1",
            "INTENT_EXTRA_VIDEO" to media,
            "INTENT_EXTRA_BODY" to body,
            "INTENT_EXTRA_FOOTER_ACTIONS" to footerActions
        )

        Utils.openPage(context, WidgetChangeLog::class.java, Intent().putExtras(bundle))
    }

    /**
     * Icon that will show up in changelog's footer
     */
    public class FooterAction(
        @DrawableRes
        public val drawableResourceId: Int,
        public val url: String,
    ) : Parcelable {
        private constructor(input: Parcel) : this(
            drawableResourceId = input.readInt(),
            url = input.readString()!!
        )

        override fun describeContents(): Int = 0

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeInt(drawableResourceId)
            parcel.writeString(url)
        }

        public companion object {
            @JvmField
            public val CREATOR: Parcelable.Creator<FooterAction?> = object : Parcelable.Creator<FooterAction?> {
                override fun createFromParcel(input: Parcel) = FooterAction(input)

                override fun newArray(size: Int) = arrayOfNulls<FooterAction>(size)
            }
        }
    }
}
