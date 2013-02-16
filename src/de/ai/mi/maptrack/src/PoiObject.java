package de.ai.mi.maptrack.src;

import com.google.android.gms.maps.model.MarkerOptions;

public class PoiObject {

	private MarkerOptions markerOptions;
	private String markerTyp;
	private String pathToImageOrVideo;

	public PoiObject(MarkerOptions markerOptions, String markerTyp, String pathToImageOrVideo) {
		this.markerOptions = markerOptions;
		this.markerTyp = markerTyp;
		this.pathToImageOrVideo = pathToImageOrVideo;
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	public String getMarkerTyp() {
		return markerTyp;
	}

	public String getMarkerLink() {
		return pathToImageOrVideo;
	}

}
