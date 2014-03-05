package cn.buding.common.location;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * address interface.
 */
public interface IAddress extends Parcelable, Serializable {
	public String getCityName();

	/** @return the detail address. contain city area district and street */
	public String getDetailAddress();
}
